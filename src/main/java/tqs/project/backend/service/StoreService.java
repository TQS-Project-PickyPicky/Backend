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

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Store getStore(Integer id) throws StoreNotFoundException {
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

    public Store updateStore(Integer id, Store store) throws StoreNotFoundException {
        Store oldStore = storeRepository.findById(id).orElseThrow(() -> new StoreNotFoundException(id));
        // Update the store
        oldStore.setName(store.getName());
        return storeRepository.save(oldStore);
    }

    public Store deleteStore(Integer id) throws StoreNotFoundException {
        Store store = storeRepository.findById(id).orElseThrow(() -> new StoreNotFoundException(id));
        storeRepository.delete(store);
        return store;
    }
}
