package tqs.project.backend.boundary;

import org.springframework.http.HttpStatus;
import tqs.project.backend.data.collection_point.*;
import tqs.project.backend.data.parcel.ParcelMinimal;
import tqs.project.backend.data.parcel.ParcelMinimalEta;
import tqs.project.backend.exception.CollectionPointNotFoundException;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.service.CollectionPointService;
import tqs.project.backend.util.ConverterUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CollectionPointRestController {

    private final CollectionPointService collectionPointService;

    public CollectionPointRestController(CollectionPointService collectionPointService) {
        this.collectionPointService = collectionPointService;
    }

    @GetMapping("/acp/all")
    public List<CollectionPointRDto> acpAll(@RequestParam(value = "zip", required = false) String zip) {
        if(zip == null)
            return collectionPointService.getAll();
        else {
            return collectionPointService.getAll(zip);
        }
    }

    @PostMapping("/acp/add")
    public ResponseEntity<CollectionPointRDto> addAcp(@RequestBody CollectionPointCreateDto collectionPointCreateDto) {
        String zipcode = collectionPointCreateDto.getZipcode();
        System.out.println(zipcode);
        CollectionPoint cp = ConverterUtils.fromCollectionPointCreateDtoToCollectionPoint(collectionPointCreateDto);
        Boolean b = collectionPointService.saveCPPoint(cp, zipcode);
        if(b){
            return ResponseEntity.ok(ConverterUtils.fromCollectionPointToCollectionPointRDto(cp));
        }
        else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/acp/{id}")
    public ResponseEntity<CollectionPointRDto> updateAcp(@PathVariable(value = "id") Integer id, @RequestBody CollectionPointUpdateDto collectionPointUpdateDto) {
        CollectionPoint cp = ConverterUtils.fromCollectionPointUpdateDtoToCollectionPoint(collectionPointUpdateDto);
        try{
            CollectionPointRDto cpRDto = collectionPointService.updateCPPoint(id, cp);
            return ResponseEntity.ok(cpRDto);
        } catch (CollectionPointNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/acp/{id}")
    public ResponseEntity<CollectionPointRDto> deleteAcp(@PathVariable(value = "id") Integer id) {
        try{
            CollectionPointRDto cpRDto = collectionPointService.deleteCPPoint(id);
            return ResponseEntity.ok(cpRDto);
        } catch (CollectionPointNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/acp/{id}")
    public List<ParcelMinimal> acp(@PathVariable(value = "id") Integer id) {
        // TODO - Change to ask for id of logged in user
        return collectionPointService.getAllParcels(id);
    }

    @GetMapping("/acp/parcel/{id}")
    public ResponseEntity<ParcelMinimalEta> parcel(@PathVariable(value = "id") Integer id) throws ParcelNotFoundException {
        try {
            ParcelMinimalEta parcel = collectionPointService.getParcel(id);
            return ResponseEntity.ok(parcel);
        } catch (ParcelNotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/acp/parcel/checkin/{id}")
    public ResponseEntity<ParcelMinimal> parcelCheckIn(@PathVariable(value = "id") Integer id) {
        try {
            ParcelMinimal parcel = collectionPointService.checkIn(id);
            return ResponseEntity.ok(parcel);
        } catch (ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @PostMapping("/acp/parcel/checkout/{id}")
    public ResponseEntity<ParcelMinimal> parcelCheckOut(@PathVariable(value = "id") Integer id, @RequestParam(value = "token") Integer token) {
        try {
            ParcelMinimal parcel = collectionPointService.checkOut(id, token);
            return ResponseEntity.ok(parcel);
        } catch (IncorrectParcelTokenException | ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/acp/parcel/return/{id}")
    public ResponseEntity<ParcelMinimal> parcelCheckOut(@PathVariable(value = "id") Integer id) {
        try {
            ParcelMinimal parcel = collectionPointService.returnParcel(id);
            return ResponseEntity.ok(parcel);
        } catch (ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
