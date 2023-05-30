package tqs.project.backend.boundary;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import tqs.project.backend.data.collection_point.*;
import tqs.project.backend.data.parcel.ParcelMinimal;
import tqs.project.backend.data.parcel.ParcelMinimalEta;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.exception.CollectionPointNotFoundException;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.service.CollectionPointService;
import tqs.project.backend.util.ConverterUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.project.backend.util.ConverterUtils;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CollectionPointRestController {

    private final CollectionPointService collectionPointService;

    public CollectionPointRestController(CollectionPointService collectionPointService) {
        this.collectionPointService = collectionPointService;
    }

    @GetMapping("/acp")
    public List<CollectionPointRDto> acpAll(@RequestParam(value = "zip", required = false) String zip) {
        if(zip == null)
            return collectionPointService.getAll();
        else {
            return collectionPointService.getAll(zip);
        }
    }

    @PostMapping("/acp")
    public ResponseEntity<CollectionPointRDto> addAcp(@RequestBody CollectionPointCreateDto collectionPointCreateDto) {
        String zipcode = collectionPointCreateDto.getZipcode();
        System.out.println(zipcode);
        CollectionPoint cp = ConverterUtils.fromCollectionPointCreateDtoToCollectionPoint(collectionPointCreateDto);
        Boolean b = collectionPointService.saveCPPoint(cp, zipcode);
        if(b){
            return new ResponseEntity<>(ConverterUtils.fromCollectionPointToCollectionPointRDto(cp), HttpStatus.CREATED);
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
            return new ResponseEntity<>(cpRDto, HttpStatus.OK);
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
            return new ResponseEntity<>(cpRDto, HttpStatus.OK);
        } catch (CollectionPointNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/acp/{id}/parcels")
    public List<ParcelMinimal> acp(@PathVariable(value = "id") Integer id) {
        // TODO - Change to ask for id of logged in user
        return collectionPointService.getAllParcels(id);
    }
}
