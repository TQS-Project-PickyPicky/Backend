package tqs.project.backend.service;

import org.springframework.stereotype.Service;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreRepository;

import java.util.List;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final ParcelRepository parcelRepository;

    public StoreService(StoreRepository storeRepository, ParcelRepository parcelRepository) {
        this.storeRepository = storeRepository;
        this.parcelRepository = parcelRepository;
    }

    public Store getStore(Integer id) {
        return null;
    }

    public List<Store> getAllStores() {
        return null;
    }

    public Store createStore(String name) {
        return null;
    }

    public Store updateStore(Integer id, Store store) {
        return null;
    }

    public Store deleteStore(Integer id) {
        return null;
    }
}
