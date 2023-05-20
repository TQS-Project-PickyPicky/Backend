package ies.project.backend.boundary;

import ies.project.backend.data.parcel.Parcel;
import ies.project.backend.data.parcel.ParcelAllDto;
import ies.project.backend.data.parcel.ParcelDto;
import ies.project.backend.service.CollectionPointService;
import ies.project.backend.util.CantAccessParcelException;
import ies.project.backend.util.DifferentStateException;
import ies.project.backend.util.IncorrectParcelTokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CollectionPointRestController {

    private final CollectionPointService collectionPointService;

    public CollectionPointRestController(CollectionPointService collectionPointService) {
        this.collectionPointService = collectionPointService;
    }

    @GetMapping("/acp")
    public List<ParcelAllDto> acp(@RequestParam(value="id") Integer id) {
        // TODO - Change to ask for id of logged in user
        List<ParcelAllDto> parcels = collectionPointService.getallParcels(id);
        return parcels;
    }

    @GetMapping("/acp/parcel")
    public ResponseEntity<ParcelDto> parcel(@RequestParam(value="id") Integer id) throws CantAccessParcelException {
        try{
            ParcelDto parcel = collectionPointService.getParcel(id);
            return ResponseEntity.ok(parcel);
        } catch (CantAccessParcelException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/acp/parcel/checkin")
    public ResponseEntity<ParcelAllDto> parcelCheckIn(@RequestParam(value="id") Integer id) {
        try{
            ParcelAllDto parcel = collectionPointService.checkIn(id);
            return ResponseEntity.ok(parcel);
        } catch (CantAccessParcelException | DifferentStateException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }

    }

    @PostMapping("/acp/parcel/checkout")
    public ResponseEntity<ParcelAllDto> parcelCheckOut(@RequestParam(value="id") Integer id, @RequestParam(value="token") Integer token) {
        try{
            ParcelAllDto parcel = collectionPointService.checkOut(id,token);
            return ResponseEntity.ok(parcel);
        } catch (IncorrectParcelTokenException | CantAccessParcelException | DifferentStateException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/acp/parcel/return")
    public ResponseEntity<ParcelAllDto> parcelCheckOut(@RequestParam(value="id") Integer id) {
        try{
            ParcelAllDto parcel = collectionPointService.returnParcel(id);
            return ResponseEntity.ok(parcel);
        } catch (CantAccessParcelException | DifferentStateException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

}
