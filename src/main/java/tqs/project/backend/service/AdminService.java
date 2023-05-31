package tqs.project.backend.service;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDDto;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.*;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.exception.CollectionPointNotFoundException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.util.ConverterUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminService {
    private final PartnerRepository partnerRepository;
    private final CollectionPointRepository collectionPointRepository;
    private final ParcelRepository parcelRepository;

    @Autowired
    public AdminService(CollectionPointRepository collectionPointRepository, ParcelRepository parcelRepository, PartnerRepository partnerRepository) {
        this.collectionPointRepository = collectionPointRepository;
        this.parcelRepository = parcelRepository;
        this.partnerRepository = partnerRepository;

    }

    public CollectionPoint getCollectionPointById(Integer id) throws Exception{
        return collectionPointRepository.findById(id).orElseThrow( () -> new Exception("Collection point with id: " + id + " not found."));
    }

    public List<CollectionPointDDto> getCollectionPointsDDto(Boolean status) {
        List<CollectionPoint> collectionPoints = this.collectionPointRepository.findByStatus(status);
        log.info("" + collectionPoints.size());
    
        List<CollectionPointDDto> dtoList = ConverterUtils.fromCollectionPointsToCollectionPointDDto(collectionPoints);
    
        return dtoList;
    }

    public void deleteCollectionPointAndParcels(Integer idACP) throws Exception{
        //must delete partner and parcels associated 
        CollectionPoint cp = getCollectionPointById(idACP); //metodo ja implementado
        partnerRepository.delete(cp.getPartner());          //elimina partner associado
        parcelRepository.deleteAll(cp.getParcels());        //elimina todas as parcels associadas
        collectionPointRepository.delete(cp);
        
    }

    public void saveACPoint(CollectionPoint point){
        collectionPointRepository.save(point);
    }

    public void deletePartnerById(Integer id){
        partnerRepository.deleteById(id);
    }

    public void deleteCPPoint(Integer id) {
        CollectionPoint cp = collectionPointRepository.findById(id).orElseThrow(() -> new CollectionPointNotFoundException(id));
        collectionPointRepository.delete(cp);
    }

    public Map<String, Long> getStatusParcels(Integer collectionPointId) throws Exception{
        List<Parcel> parcels = collectionPointRepository.findById(collectionPointId).orElseThrow(
            () -> new Exception("CP with ID: " + collectionPointId.toString() + " not found.")
        ).getParcels();

        log.info("psize: " + parcels.size());

        Map<String, Long> statusCountMap = countParcelsByStatus(parcels);
        
        return statusCountMap;
        
    }

    public List<Parcel> getParcelByCollectionPointId(Integer idACP) throws Exception{
        return collectionPointRepository.findById(idACP).orElseThrow(
            () -> new Exception("Collection point with id: " + idACP.toString() + " not found.")
        ).getParcels();
    }
    
    public Map<String, Long> countParcelsByStatus(List<Parcel> parcels) {
        return parcels.stream()
                .collect(Collectors.groupingBy(parcel -> parcel.getStatus().toString(), Collectors.counting()));
    }

    public Parcel deleteParcel(Integer id) throws ParcelNotFoundException {
        Parcel parcel = parcelRepository.findById(id).orElseThrow(() -> new ParcelNotFoundException(id));
        parcel.setCollectionPoint(null);
        parcelRepository.delete(parcel);
        return parcel;
    }

}
