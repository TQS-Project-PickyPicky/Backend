package tqs.project.backend.boundary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.project.backend.data.parcel.ParcelCreateDto;
import tqs.project.backend.data.parcel.ParcelDto;
import tqs.project.backend.data.parcel.ParcelUpdateDto;
import tqs.project.backend.service.ParcelService;

@RestController
@RequestMapping("/api")
public class ParcelRestController {

    private final ParcelService parcelService;

    public ParcelRestController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping("/parcels/{id}")
    public ResponseEntity<ParcelDto> getParcel(@PathVariable("id") Integer parcelId) {
        return null;
    }

    @GetMapping("/parcels")
    public ResponseEntity<ParcelDto> getAllParcels() {
        return null;
    }

    @PostMapping("/parcels")
    public ResponseEntity<ParcelDto> createParcel(@RequestBody ParcelCreateDto parcelCreateDto) {
        return null;
    }

    @PutMapping("/parcels/{id}")
    public ResponseEntity<ParcelDto> updateParcel(@PathVariable("id") Integer parcelId, @RequestBody ParcelUpdateDto parcelUpdateDto, @RequestParam(required = false) Integer token) {
        return null;
    }

    @DeleteMapping("/parcels/{id}")
    public ResponseEntity<ParcelDto> deleteParcel(@PathVariable("id") Integer parcelId) {
        return null;
    }
}
