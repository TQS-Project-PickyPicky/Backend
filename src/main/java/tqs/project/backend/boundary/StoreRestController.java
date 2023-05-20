package tqs.project.backend.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelDto;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreCreateDto;
import tqs.project.backend.data.store.StoreDto;
import tqs.project.backend.data.store.StoreUpdateDto;
import tqs.project.backend.exception.StoreNotFoundException;
import tqs.project.backend.service.StoreService;
import tqs.project.backend.util.ConverterUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class StoreRestController {

    private final StoreService storeService;

    public StoreRestController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/stores/{id}")
    public ResponseEntity<StoreDto> getStore(@PathVariable("id") Integer storeId) {
        try {
            Store store = storeService.getStore(storeId);
            StoreDto storeDto = ConverterUtils.fromStoreToStoreDto(store);
            return new ResponseEntity<>(storeDto, HttpStatus.OK);
        } catch (StoreNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stores")
    public ResponseEntity<List<StoreDto>> getAllStores() {
        try {
            List<Store> stores = storeService.getAllStores();
            List<StoreDto> storesDto = stores.stream().map(ConverterUtils::fromStoreToStoreDto).collect(Collectors.toList());
            return new ResponseEntity<>(storesDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/stores")
    public ResponseEntity<StoreDto> createStore(@RequestBody StoreCreateDto storeCreateDto) {
        try {
            Store store = storeService.createStore(storeCreateDto.getName());
            StoreDto storeDto = ConverterUtils.fromStoreToStoreDto(store);
            return new ResponseEntity<>(storeDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/stores/{id}")
    public ResponseEntity<StoreDto> updateStore(@PathVariable("id") Integer storeId, @RequestBody StoreUpdateDto storeUpdateDto) {
        try {
            Store store = storeService.updateStore(storeId, ConverterUtils.fromStoreUpdateDtoToStore(storeUpdateDto));
            StoreDto storeDto = ConverterUtils.fromStoreToStoreDto(store);
            return new ResponseEntity<>(storeDto, HttpStatus.OK);
        } catch (StoreNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/stores/{id}")
    public ResponseEntity<StoreDto> deleteStore(@PathVariable("id") Integer storeId) {
        try {
            Store store = storeService.deleteStore(storeId);
            StoreDto storeDto = ConverterUtils.fromStoreToStoreDto(store);
            return new ResponseEntity<>(storeDto, HttpStatus.OK);
        } catch (StoreNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stores/{id}/parcels")
    public ResponseEntity<List<ParcelDto>> getParcelsFromStore(@PathVariable("id") Integer storeId) {
        try {
            Store store = storeService.getStore(storeId);
            List<Parcel> parcels = store.getParcels();
            List<ParcelDto> parcelsDto = parcels.stream().map(ConverterUtils::fromParcelToParcelDto).collect(Collectors.toList());
            return new ResponseEntity<>(parcelsDto, HttpStatus.OK);
        } catch (StoreNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
