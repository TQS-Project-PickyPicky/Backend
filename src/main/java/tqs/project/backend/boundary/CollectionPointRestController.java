package tqs.project.backend.boundary;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDto;
import tqs.project.backend.data.parcel.ParcelMinimal;
import tqs.project.backend.data.parcel.ParcelMinimalEta;
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

    @PostMapping("/acp")
    public ResponseEntity<String> addCollectionPoint(@RequestBody CollectionPointDto collectionPointDto, 
                                                                @RequestParam(value = "password-check") String passwordCheck,
                                                                @RequestParam(value = "zip-code") String zipCode,
                                                                @RequestParam(value = "city") String city){
        
        //TODO -> testing
        if (!collectionPointDto.getPartner().getPassword().equals(passwordCheck)) {
            return ResponseEntity.ok().body("Passwords do not match!");
        }     

        CollectionPoint cp = ConverterUtils.fromCollectionPointDTOToCollectionPoint(collectionPointDto);
        if (!collectionPointService.saveCPPoint(cp, zipCode, city)){ 
            return ResponseEntity.ok().body("Couldn't get coordinates for that address...");
        }
                                                 
        
        return ResponseEntity.ok().body("Collection point created with success!");
        
    }

    @GetMapping("/acp")
    public List<ParcelMinimal> acp(@RequestParam(value = "id") Integer id) {
        // TODO - Change to ask for id of logged in user
        return collectionPointService.getAllParcels(id);
    }

    @GetMapping("/acp/parcel")
    public ResponseEntity<ParcelMinimalEta> parcel(@RequestParam(value = "id") Integer id) throws ParcelNotFoundException {
        try {
            ParcelMinimalEta parcel = collectionPointService.getParcel(id);
            return ResponseEntity.ok(parcel);
        } catch (ParcelNotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/acp/parcel/checkin")
    public ResponseEntity<ParcelMinimal> parcelCheckIn(@RequestParam(value = "id") Integer id) {
        try {
            ParcelMinimal parcel = collectionPointService.checkIn(id);
            return ResponseEntity.ok(parcel);
        } catch (ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @PostMapping("/acp/parcel/checkout")
    public ResponseEntity<ParcelMinimal> parcelCheckOut(@RequestParam(value = "id") Integer id, @RequestParam(value = "token") Integer token) {
        try {
            ParcelMinimal parcel = collectionPointService.checkOut(id, token);
            return ResponseEntity.ok(parcel);
        } catch (IncorrectParcelTokenException | ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/acp/parcel/return")
    public ResponseEntity<ParcelMinimal> parcelCheckOut(@RequestParam(value = "id") Integer id) {
        try {
            ParcelMinimal parcel = collectionPointService.returnParcel(id);
            return ResponseEntity.ok(parcel);
        } catch (ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
