package tqs.project.backend.service;

import org.springframework.stereotype.Service;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreRepository;
import tqs.project.backend.exception.StoreNotFoundException;

import java.util.ArrayList;
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
        return storeRepository.findById(id).orElseThrow(() -> new StoreNotFoundException(id));
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store createStore(String name) {
        Store store = new Store();
        store.setName(name);
        store.setParcels(new ArrayList<>());
        return storeRepository.save(store);
    }

    public Store updateStore(Integer id, Store store) {
        Store oldStore = storeRepository.findById(id).orElseThrow(() -> new StoreNotFoundException(id));
        // Update the store
        oldStore.setName(store.getName());
        oldStore.setParcels(store.getParcels());
        return storeRepository.save(oldStore);
    }

    public Store deleteStore(Integer id) {
        Store store = storeRepository.findById(id).orElseThrow(() -> new StoreNotFoundException(id));
        storeRepository.delete(store);
        return store;
    }
}
