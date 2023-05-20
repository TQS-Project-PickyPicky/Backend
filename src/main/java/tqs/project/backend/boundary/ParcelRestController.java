package tqs.project.backend.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelCreateDto;
import tqs.project.backend.data.parcel.ParcelDto;
import tqs.project.backend.data.parcel.ParcelUpdateDto;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.service.ParcelService;
import tqs.project.backend.util.ConverterUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ParcelRestController {

    private final ParcelService parcelService;

    public ParcelRestController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping("/parcels/{id}")
    public ResponseEntity<ParcelDto> getParcel(@PathVariable("id") Integer parcelId) {
        try {
            Parcel parcel = parcelService.getParcel(parcelId);
            ParcelDto parcelDto = ConverterUtils.fromParcelToParcelDto(parcel);
            return new ResponseEntity<>(parcelDto, HttpStatus.OK);
        } catch (ParcelNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/parcels")
    public ResponseEntity<List<ParcelDto>> getAllParcels() {
        try {
            List<Parcel> parcels = parcelService.getAllParcels();
            List<ParcelDto> parcelsDto = parcels.stream().map(ConverterUtils::fromParcelToParcelDto).collect(Collectors.toList());
            return new ResponseEntity<>(parcelsDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/parcels")
    public ResponseEntity<ParcelDto> createParcel(@RequestBody ParcelCreateDto parcelCreateDto) {
        try {
            Parcel parcel = parcelService.createParcel(parcelCreateDto.getClientName(), parcelCreateDto.getClientEmail(), parcelCreateDto.getClientPhone(), parcelCreateDto.getClientMobilePhone(), parcelCreateDto.getStoreId(), parcelCreateDto.getCollectionPointId());
            ParcelDto parcelDto = ConverterUtils.fromParcelToParcelDto(parcel);
            return new ResponseEntity<>(parcelDto, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage() + " " + e.getCause());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/parcels/{id}")
    public ResponseEntity<ParcelDto> updateParcel(@PathVariable("id") Integer parcelId, @RequestBody ParcelUpdateDto parcelUpdateDto, @RequestParam(required = false) Integer token) {
        try {
            Parcel parcel = parcelService.updateParcel(parcelId, ConverterUtils.fromParcelUpdateDtoToParcel(parcelUpdateDto), token);
            ParcelDto parcelDto = ConverterUtils.fromParcelToParcelDto(parcel);
            return new ResponseEntity<>(parcelDto, HttpStatus.OK);
        } catch (ParcelNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidParcelStatusChangeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IncorrectParcelTokenException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/parcels/{id}")
    public ResponseEntity<ParcelDto> deleteParcel(@PathVariable("id") Integer parcelId) {
        try {
            Parcel parcel = parcelService.deleteParcel(parcelId);
            ParcelDto parcelDto = ConverterUtils.fromParcelToParcelDto(parcel);
            return new ResponseEntity<>(parcelDto, HttpStatus.OK);
        } catch (ParcelNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
