package tqs.project.backend.service;

import tqs.project.backend.data.admin.Admin;
import tqs.project.backend.data.admin.AdminRepository;
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

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminService {
    private final PartnerRepository partnerRepository;
    private final CollectionPointRepository collectionPointRepository;
    private final ParcelRepository parcelRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(CollectionPointRepository collectionPointRepository, ParcelRepository parcelRepository, PartnerRepository partnerRepository, AdminRepository adminRepository) {
        this.collectionPointRepository = collectionPointRepository;
        this.parcelRepository = parcelRepository;
        this.partnerRepository = partnerRepository;
        this.adminRepository = adminRepository;

    }

    @PostConstruct
    public void initData() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin");
            adminRepository.save(admin);
        }
    }

    public CollectionPoint getCollectionPointById(Integer id) throws Exception{
        CollectionPoint cp = collectionPointRepository.findById(id).orElseThrow( () -> new Exception("Collection point with id: " + id + " not found."));
        return cp;
    }

    public List<CollectionPointDDto> getCollectionPointsDDto(Boolean status) {
        List<CollectionPoint> collectionPoints = this.collectionPointRepository.findByStatus(status);
        log.info("" + collectionPoints.size());
    
        List<CollectionPointDDto> dtoList = ConverterUtils.fromCollectionPointsToCollectionPointDDto(collectionPoints);
    
        return dtoList;
    }

    public void deleteCollectionPointAndParcels(Integer idACP) throws Exception{
        //must delete partner and parcels associated 
        CollectionPoint cp = getCollectionPointById(idACP); 
        partnerRepository.delete(cp.getPartner());          
        //parcelRepository.deleteAll(cp.getParcels());        
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
