package tqs.project.backend.boundary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.project.backend.data.parcel.ParcelDto;
import tqs.project.backend.data.store.StoreDto;
import tqs.project.backend.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StoreRestController {

    private final StoreService storeService;

    public StoreRestController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/stores/{id}")
    public ResponseEntity<StoreDto> getStore(@PathVariable("id") Integer storeId) {
        return null;
    }

    @GetMapping("/stores")
    public ResponseEntity<List<StoreDto>> getAllStores() {
        return null;
    }

    @PostMapping("/stores")
    public ResponseEntity<StoreDto> createStore(@RequestBody StoreDto storeDto) {
        return null;
    }

    @PutMapping("/stores/{id}")
    public ResponseEntity<StoreDto> updateStore(@PathVariable("id") Integer storeId, @RequestBody StoreDto storeDto) {
        return null;
    }

    @DeleteMapping("/stores/{id}")
    public ResponseEntity<StoreDto> deleteStore(@PathVariable("id") Integer storeId) {
        return null;
    }

    @GetMapping("/stores/{id}/parcels")
    public ResponseEntity<List<ParcelDto>> getParcelsFromStore(@PathVariable("id") Integer storeId) {
        return null;
    }

    @PostMapping("/stores/{id}/parcels")
    public ResponseEntity<ParcelDto> createParcelForStore(@PathVariable("id") Integer storeId, @RequestBody ParcelDto parcelDto) {
        return null;
    }
}
