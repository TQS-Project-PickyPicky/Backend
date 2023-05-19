package tqs.project.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreRepository;
import tqs.project.backend.exception.StoreNotFoundException;
import tqs.project.backend.util.ConverterUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock(lenient = true)
    private StoreRepository storeRepository;

    @Mock(lenient = true)
    private ParcelRepository parcelRepository;

    @InjectMocks
    private StoreService service;

    @BeforeEach
    void setUp() {
        // Create entities for mock database
        Parcel parcel1 = new Parcel(1, 111111, "Anna", "anna@mail.com", 111000111, 111000222, LocalDate.of(2023, 5, 19), ParcelStatus.PLACED, null, null);
        Parcel parcel2 = new Parcel(2, 222222, "Bob", "bob@mail.com", 222000111, 222000222, LocalDate.of(2023, 5, 19), ParcelStatus.DELIVERED, null, null);

        List<Parcel> allParcels = List.of(parcel1, parcel2);

        Store store1 = new Store(1, "Mock Store 1", allParcels);
        Store store2 = new Store(2, "Mock Store 2", new ArrayList<>());

        // Create expectations
        when(parcelRepository.findById(1))
                .thenReturn(Optional.of(parcel1));
        when(parcelRepository.findById(2))
                .thenReturn(Optional.of(parcel2));

        when(storeRepository.findById(1))
                .thenReturn(Optional.of(store1));
        when(storeRepository.findById(2))
                .thenReturn(Optional.of(store2));
        when(storeRepository.findById(3))
                .thenReturn(Optional.empty());
        when(storeRepository.findAll())
                .thenReturn(List.of(store1, store2));
        when(storeRepository.save(any(Store.class)))
                .then(returnsFirstArg());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenGetExistingStore_thenReturnStore() {
        Store store = service.getStore(1);

        assertThat(store).isNotNull();
        assertThat(store.getId()).isEqualTo(1);
    }

    @Test
    void whenGetNonExistingStore_thenThrowStoreNotFoundException() {
        assertThatThrownBy(() -> service.getStore(3))
                .isInstanceOf(StoreNotFoundException.class)
                .hasMessageContaining("Store with id 3 not found");
    }

    @Test
    void whenGetAllStores_thenReturnAllStores() {
        List<Store> stores = service.getAllStores();

        assertThat(stores).isNotNull();
        assertThat(stores).hasSize(2).extracting(Store::getId).contains(1, 2);
    }

    @Test
    void givenNewStore_whenCreateStore_thenReturnCreatedStore() {
        String name = "Mock Store 3";

        Store store = service.createStore(name);

        assertThat(store).isNotNull();
        assertThat(store.getName()).isEqualTo("Mock Store 3");
    }

    @Test
    void givenExistingStore_whenUpdateStore_thenReturnUpdatedStore() {
        Store store = cloneStore(1);

        store.setName("Mock Store 1.1");
        Store updated = service.updateStore(1, store);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Mock Store 1.1");
    }

    @Test
    void whenUpdateNonExistingStore_thenThrowStoreNotFoundException() {
        assertThatThrownBy(() -> service.updateStore(3, new Store()))
                .isInstanceOf(StoreNotFoundException.class)
                .hasMessageContaining("Store with id 3 not found");
    }

    @Test
    void whenDeleteExistingStore_thenDeleteStore() {
        Store deleted = service.deleteStore(1);

        assertThat(deleted).isNotNull();
        assertThat(deleted.getId()).isEqualTo(1);
        verify(storeRepository, times(1)).delete(deleted);
    }

    @Test
    void whenDeleteNonExistingStore_thenThrowStoreNotFoundException() {
        assertThatThrownBy(() -> service.deleteStore(3))
                .isInstanceOf(StoreNotFoundException.class)
                .hasMessageContaining("Store with id 3 not found");
    }

    public Store cloneStore(Integer id) {
        return ConverterUtils.fromStoreDtoToStore(ConverterUtils.fromStoreToStoreDto(service.getStore(id)), parcelRepository);
    }
}