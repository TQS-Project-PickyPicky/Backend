package tqs.project.backend.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tqs.project.backend.data.admin.Admin;
import tqs.project.backend.data.admin.AdminRepository;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
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

    public boolean saveCPPoint(CollectionPoint point, String zipCode) {
    
        point.setStatus(false);
    
        ArrayList<Double> latlon = ResolveLocation.resolveAddress(zipCode);
        if (latlon.isEmpty()) {
            return false;
        }
    
        point.setLatitude(latlon.get(0));
        point.setLongitude(latlon.get(1));
    
        partnerRepository.save(point.getPartner());
        collectionPointRepository.save(point);
    
        return true;
    }

    public Admin findAdmin(String username, String password){
        return adminRepository.findByUsernameAndPassword(username, password);
    }

    public Partner findByUsernameAndPassword(String username, String password){
        return partnerRepository.findByUsernameAndPassword(username, password);    
    }

}
