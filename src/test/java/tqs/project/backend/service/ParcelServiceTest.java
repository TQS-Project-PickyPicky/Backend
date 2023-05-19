package tqs.project.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ParcelServiceTest {

    @Mock
    private ParcelRepository repository;

    @InjectMocks
    private ParcelService service;

    @BeforeEach
    void setUp() {
        // Create entities for mock database
        Store store = new Store(1, "Mock Store", new ArrayList<>());
        CollectionPoint collectionPoint = new CollectionPoint(1, "Mock Collection Point", "Mock Address", 40.6331731, -8.6594933, new ArrayList<>());

        Parcel parcel1 = new Parcel(1, 111, "Anna", "anna@mail.com", 111000111, 111000222, LocalDate.of(2023, 5, 19), ParcelStatus.PLACED, store, collectionPoint);
        Parcel parcel2 = new Parcel(2, 222, "Bob", "bob@mail.com", 222000111, 222000222, LocalDate.of(2023, 5, 19), ParcelStatus.PLACED, store, collectionPoint);

        List<Parcel> allParcels = List.of(parcel1, parcel2);

        // Create expectations
        when(repository.findById(1))
                .thenReturn(Optional.of(parcel1));
        when(repository.findById(2))
                .thenReturn(Optional.of(parcel2));
        when(repository.findById(3))
                .thenReturn(Optional.empty());
        when(repository.findAll())
                .thenReturn(allParcels);
        when(repository.save(any()))
                .then(returnsFirstArg());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenGetValidParcel_thenReturnParcel() {
        Parcel parcel = service.getParcel(1);

        assertThat(parcel).isNotNull();
        assertThat(parcel.getId()).isEqualTo(1);
    }

    @Test
    void whenGetInvalidParcel_thenReturnNull() {
        Parcel parcel = service.getParcel(3);

        assertThat(parcel).isNull();
    }

    @Test
    void whenGetAllParcels_thenReturnAllParcels() {
        List<Parcel> allParcels = service.getAllParcels();

        assertThat(allParcels).isNotNull();
        assertThat(allParcels).hasSize(2).extracting(Parcel::getId).contains(1, 2);
    }

    @Test
    void givenNewParcel_whenCreateParcel_thenReturnParcel() {
        String clientName = "Charlie";
        String clientEmail = "charlie@mail.com";
        Integer clientPhone = 333000111;
        Integer clientMobilePhone = 333000222;
        Integer storeId = 1;
        Integer collectionPointId = 1;

        Parcel created = service.createParcel(clientName, clientEmail, clientPhone, clientMobilePhone, storeId, collectionPointId);

        assertThat(created).isNotNull();
        assertThat(created.getClientName()).isEqualTo("Charlie");
        assertThat(created.getStatus()).isEqualTo(ParcelStatus.PLACED);
        assertThat(created.getStore().getId()).isEqualTo(1);
        assertThat(created.getCollectionPoint().getId()).isEqualTo(1);
    }

    @Test
    void givenParcelWithStatusX_whenUpdateParcelToValidStatusY_thenReturnParcelWithStatusY() {
        // PLACED -> IN_TRANSIT
        {
            Parcel parcel = service.getParcel(1);
            assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.PLACED);

            parcel.setStatus(ParcelStatus.IN_TRANSIT);
            Parcel updated = service.updateParcel(1, parcel, null);

            assertThat(updated).isNotNull();
            assertThat(updated.getStatus()).isEqualTo(ParcelStatus.IN_TRANSIT);
        }
        // IN_TRANSIT -> DELIVERED
        {
            Parcel parcel = service.getParcel(1);
            assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.IN_TRANSIT);

            parcel.setStatus(ParcelStatus.DELIVERED);
            Parcel updated = service.updateParcel(1, parcel, null);

            assertThat(updated).isNotNull();
            assertThat(updated.getStatus()).isEqualTo(ParcelStatus.DELIVERED);
        }
        // DELIVERED -> COLLECTED
        {
            Parcel parcel = service.getParcel(1);
            assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.DELIVERED);

            parcel.setStatus(ParcelStatus.COLLECTED);
            Parcel updated = service.updateParcel(1, parcel, 111); // Requires parcel token to validate client

            assertThat(updated).isNotNull();
            assertThat(updated.getStatus()).isEqualTo(ParcelStatus.COLLECTED);
        }
        // COLLECTED -> RETURNED
        {
            Parcel parcel = service.getParcel(1);
            assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.COLLECTED);

            parcel.setStatus(ParcelStatus.RETURNED);
            Parcel updated = service.updateParcel(1, parcel, null);

            assertThat(updated).isNotNull();
            assertThat(updated.getStatus()).isEqualTo(ParcelStatus.RETURNED);
        }
    }

    @Test
    void givenParcelWithStatusX_whenUpdateParcelToInvalidStatusY_thenThrowInvalidParcelStatusChangeException() {
        Parcel parcel = service.getParcel(1);
        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.PLACED);

        parcel.setStatus(ParcelStatus.COLLECTED);

        assertThatThrownBy(() -> service.updateParcel(1, parcel, 111))
                .isInstanceOf(InvalidParcelStatusChangeException.class)
                .hasMessageContaining("Invalid status change from PLACED to COLLECTED");
    }

    @Test
    void whenDeleteValidParcel_thenReturnDeletedParcel() {
        Parcel deleted = service.deleteParcel(1);

        assertThat(deleted).isNotNull();
        assertThat(deleted.getId()).isEqualTo(1);
        assertThat(service.getParcel(1)).isNull();
    }

    @Test
    void whenDeleteInvalidParcel_thenReturnNull() {
        Parcel deleted = service.deleteParcel(3);

        assertThat(deleted).isNull();
    }
}