package tqs.project.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelMinimal;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreRepository;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.util.ConverterUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelServiceTest {

    @Mock(lenient = true)
    private ParcelRepository parcelRepository;

    @Mock(lenient = true)
    private StoreRepository storeRepository;

    @Mock(lenient = true)
    private CollectionPointRepository collectionPointRepository;

    @InjectMocks
    private ParcelService service;

    @BeforeEach
    void setUp() {
        // Create entities for mock database
        Store store = new Store(1, "Mock Store", new ArrayList<>());

        CollectionPoint collectionPoint = new CollectionPoint(1, "Mock Collection Point", "Mock Type", 100, "Mock Address", 40.6331731, -8.6594933, "Mock Owner Name", "Mock Owner Email", "Mock Owner Gender", 111000111, 111000222, true, null, new ArrayList<>());

        Parcel parcel1 = new Parcel(1, 111111, "Anna", "anna@mail.com", 111000111, 111000222, LocalDate.of(2023, 5, 19), ParcelStatus.PLACED, store, collectionPoint);
        Parcel parcel2 = new Parcel(2, 222222, "Bob", "bob@mail.com", 222000111, 222000222, LocalDate.of(2023, 5, 19), ParcelStatus.DELIVERED, store, collectionPoint);

        List<Parcel> allParcels = List.of(parcel1, parcel2);

        // CheckIn, CheckOut and Return

        Parcel parcel = new Parcel();
        parcel.setId(7);
        parcel.setToken(123456);
        parcel.setClientName("João");
        parcel.setClientEmail("joao@ua.pt");
        parcel.setClientPhone(123456789);
        parcel.setClientMobilePhone(987654321);
        parcel.setExpectedArrival(LocalDate.now().plusDays(5));
        parcel.setStore(store);
        parcel.setStatus(ParcelStatus.IN_TRANSIT);
        parcel.setCollectionPoint(collectionPoint);

        Parcel parcel3 = new Parcel();
        parcel3.setId(4);
        parcel3.setToken(123456);
        parcel3.setClientName("Jorge");
        parcel3.setClientEmail("jorge@ua.pt");
        parcel3.setClientPhone(123456789);
        parcel3.setClientMobilePhone(987654321);
        parcel3.setExpectedArrival(LocalDate.now().plusDays(7));
        parcel3.setStore(store);
        parcel3.setStatus(ParcelStatus.DELIVERED);
        parcel3.setCollectionPoint(collectionPoint);

        Parcel parcel5 = new Parcel();

        Parcel parcel4 = new Parcel();
        parcel4.setId(6);
        parcel4.setToken(123456);
        parcel4.setClientName("Gabriel");
        parcel4.setClientEmail("gabriel@ua.pt");
        parcel4.setClientPhone(123456789);
        parcel4.setClientMobilePhone(987654321);
        parcel4.setExpectedArrival(LocalDate.now());
        parcel4.setStore(store);
        parcel4.setStatus(ParcelStatus.COLLECTED);
        parcel4.setCollectionPoint(collectionPoint);

        when(parcelRepository.findById(7))
                .thenReturn(Optional.of(parcel));

        when(parcelRepository.findById(4))
                .thenReturn(Optional.of(parcel3));

        when(parcelRepository.findById(5))
                .thenReturn(Optional.of(parcel5));

        when(parcelRepository.findById(6))
                .thenReturn(Optional.of(parcel4));

        // Create expectations
        when(storeRepository.findById(1))
                .thenReturn(Optional.of(store));

        when(collectionPointRepository.findById(1))
                .thenReturn(Optional.of(collectionPoint));

        when(parcelRepository.findById(1))
                .thenReturn(Optional.of(parcel1));
        when(parcelRepository.findById(2))
                .thenReturn(Optional.of(parcel2));
        when(parcelRepository.findById(3))
                .thenReturn(Optional.empty());
        when(parcelRepository.findAll())
                .thenReturn(allParcels);
        when(parcelRepository.save(any()))
                .then(returnsFirstArg());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenGetExistingParcel_thenReturnParcel() {
        Parcel parcel = service.getParcel(1);

        assertThat(parcel).isNotNull();
        assertThat(parcel.getId()).isEqualTo(1);
    }

    @Test
    void whenGetNonExistingParcel_thenThrowParcelNotFoundException() {
        assertThatThrownBy(() -> service.getParcel(3))
                .isInstanceOf(ParcelNotFoundException.class)
                .hasMessageContaining("Parcel with id 3 not found");
    }

    @Test
    void whenGetAllParcels_thenReturnAllParcels() {
        List<Parcel> allParcels = service.getAllParcels();

        assertThat(allParcels).isNotNull();
        assertThat(allParcels).hasSize(2).extracting(Parcel::getId).contains(1, 2);
    }

    @Test
    void givenNewParcel_whenCreateParcel_thenReturnCreatedParcel() {
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
            Parcel parcel = cloneParcel(1);
            assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.PLACED);

            parcel.setStatus(ParcelStatus.IN_TRANSIT);
            Parcel updated = service.updateParcel(1, parcel, null);

            assertThat(updated).isNotNull();
            assertThat(updated.getStatus()).isEqualTo(ParcelStatus.IN_TRANSIT);
        }
        // IN_TRANSIT -> DELIVERED
        {
            Parcel parcel = cloneParcel(1);
            assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.IN_TRANSIT);

            parcel.setStatus(ParcelStatus.DELIVERED);
            Parcel updated = service.updateParcel(1, parcel, null);

            assertThat(updated).isNotNull();
            assertThat(updated.getStatus()).isEqualTo(ParcelStatus.DELIVERED);
        }
        // DELIVERED -> COLLECTED
        {
            Parcel parcel = cloneParcel(1);
            assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.DELIVERED);

            parcel.setStatus(ParcelStatus.COLLECTED);
            Parcel updated = service.updateParcel(1, parcel, 111111); // Requires parcel token to validate client

            assertThat(updated).isNotNull();
            assertThat(updated.getStatus()).isEqualTo(ParcelStatus.COLLECTED);
        }
        // COLLECTED -> RETURNED
        {
            Parcel parcel = cloneParcel(1);
            assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.COLLECTED);

            parcel.setStatus(ParcelStatus.RETURNED);
            Parcel updated = service.updateParcel(1, parcel, null);

            assertThat(updated).isNotNull();
            assertThat(updated.getStatus()).isEqualTo(ParcelStatus.RETURNED);
        }
    }

    @Test
    void givenParcelWithStatusX_whenUpdateParcelToInvalidStatusY_thenThrowInvalidParcelStatusChangeException() {
        Parcel parcel = cloneParcel(1);
        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.PLACED);

        parcel.setStatus(ParcelStatus.COLLECTED);

        assertThatThrownBy(() -> service.updateParcel(1, parcel, null))
                .isInstanceOf(InvalidParcelStatusChangeException.class)
                .hasMessageContaining("Invalid status change from PLACED to COLLECTED");
    }

    @Test
    void givenParcelWithStatusDelivered_whenUpdateParcelToCollectedWithInvalidToken_thenThrowInvalidParcelTokenException() {
        Parcel parcel = cloneParcel(2);
        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.DELIVERED);

        parcel.setStatus(ParcelStatus.COLLECTED);

        assertThatThrownBy(() -> service.updateParcel(2, parcel, 111111))
                .isInstanceOf(IncorrectParcelTokenException.class)
                .hasMessageContaining("Incorrect token 111111 for parcel with id 2");
    }

    @Test
    void whenUpdateNonExistingParcel_thenThrowParcelNotFoundException() {
        assertThatThrownBy(() -> service.updateParcel(3, new Parcel(), null))
                .isInstanceOf(ParcelNotFoundException.class)
                .hasMessageContaining("Parcel with id 3 not found");
    }

    @Test
    void whenDeleteExistingParcel_thenReturnDeletedParcel() {
        Parcel deleted = service.deleteParcel(1);

        assertThat(deleted).isNotNull();
        assertThat(deleted.getId()).isEqualTo(1);
        verify(parcelRepository, times(1)).delete(deleted);
    }

    @Test
    void whenDeleteNonExistingParcel_thenThrowParcelNotFoundException() {
        assertThatThrownBy(() -> service.deleteParcel(3))
                .isInstanceOf(ParcelNotFoundException.class)
                .hasMessageContaining("Parcel with id 3 not found");
    }

    public Parcel cloneParcel(Integer id) {
        return ConverterUtils.fromParcelDtoToParcel(ConverterUtils.fromParcelToParcelDto(service.getParcel(id)), storeRepository, collectionPointRepository);
    }

    @Test
    void checkinParcel_ifParcelExistsCollectionPoint() throws ParcelNotFoundException, InvalidParcelStatusChangeException {
        ParcelMinimal parcel = service.checkIn(7);

        ParcelMinimal parcelCheck = new ParcelMinimal(7, ParcelStatus.DELIVERED);

        assertEquals(parcelCheck.getId(), parcel.getId());
        assertEquals(parcelCheck.getStatus(), parcel.getStatus());
    }

    //@Test
    //void checkinParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(ParcelNotFoundException.class, () -> {
    //        service.checkIn(5);
    //    });
    //}

    @Test
    void checkinParcel_ifParcelDoesNotHaveStateInTransit() {
        assertThrows(InvalidParcelStatusChangeException.class, () -> {
            service.checkIn(4);
        });
    }

    @Test
    void checkoutParcel_ifParcelExistsCollectionPoint() throws ParcelNotFoundException, InvalidParcelStatusChangeException, IncorrectParcelTokenException {
        ParcelMinimal parcel = service.checkOut(4, 123456);

        ParcelMinimal parcelCheck = new ParcelMinimal(4, ParcelStatus.COLLECTED);

        assertEquals(parcelCheck.getId(), parcel.getId());
        assertEquals(parcelCheck.getStatus(), parcel.getStatus());
    }

    //@Test
    //void checkoutParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(ParcelNotFoundException.class, () -> {
    //        service.checkOut(5,123456);
    //    });
    //}

    @Test
    void checkoutParcel_ifParcelDoesNotHaveStateDelivered() {
        assertThrows(InvalidParcelStatusChangeException.class, () -> {
            service.checkOut(7, 123456);
        });
    }

    @Test
    void checkoutParcel_ifParcelDoesNotHaveCorrectToken() {
        assertThrows(IncorrectParcelTokenException.class, () -> {
            service.checkOut(4, 1234567);
        });
    }

    @Test
    void returnParcel__ifParcelExistsCollectionPoint() throws InvalidParcelStatusChangeException, ParcelNotFoundException {
        ParcelMinimal parcel = service.returnParcel(6);

        ParcelMinimal parcelCheck = new ParcelMinimal(6, ParcelStatus.RETURNED);

        assertEquals(parcelCheck.getId(), parcel.getId());
        assertEquals(parcelCheck.getStatus(), parcel.getStatus());
    }

    //@Test
    //void returnParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(ParcelNotFoundException.class, () -> {
    //        service.returnParcel(5);
    //    });
    //}

    @Test
    void returnParcel_ifParcelDoesNotHaveStateCollected() {
        assertThrows(InvalidParcelStatusChangeException.class, () -> {
            service.returnParcel(7);
        });
    }
}