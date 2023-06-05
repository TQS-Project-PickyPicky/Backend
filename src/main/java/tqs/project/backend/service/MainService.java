package tqs.project.backend.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tqs.project.backend.data.admin.AdminRepository;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.user.User;
import tqs.project.backend.util.ResolveLocation;

@Service
public class MainService {

    private final PartnerRepository partnerRepository;
    private final CollectionPointRepository collectionPointRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public MainService(CollectionPointRepository collectionPointRepository, PartnerRepository partnerRepository, AdminRepository adminRepository) {
        this.collectionPointRepository = collectionPointRepository;
        this.partnerRepository = partnerRepository;
        this.adminRepository = adminRepository;
    }

    public CollectionPoint saveCPPoint(CollectionPoint point, String zipCode) {
    
        point.setStatus(false);
    
        ArrayList<Double> latlon = ResolveLocation.resolveAddress(zipCode);
        if (latlon.isEmpty()) {
            return null;
        }

        if (partnerRepository.findByUsername(point.getPartner())){
            return null;
        }
    
        point.setLatitude(latlon.get(0));
        point.setLongitude(latlon.get(1));
    
        partnerRepository.save(point.getPartner());
        CollectionPoint cp1 = collectionPointRepository.save(point);
    
        return cp1;
    }

    public Partner findPartnerByUsername(String username){
        Partner partner = partnerRepository.findByUsername(username);
        return partner;
    }

    public User findByUsernameAndPassword(String username, String password){
        return adminRepository.findByUsernameAndPassword(username, password) != null
            ? adminRepository.findByUsernameAndPassword(username, password)
            : partnerRepository.findByUsernameAndPassword(username, password);

    }

    public Integer getCollectionPointByPartnerId(Integer id) throws Exception{
        Partner part = partnerRepository.findById(id).orElseThrow( () -> new Exception("Partner with id: " + id + " not found"));
        return part.getCollectionPoint().getId();
    }

}
