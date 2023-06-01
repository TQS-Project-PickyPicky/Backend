package tqs.project.backend.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import lombok.extern.slf4j.Slf4j;
import tqs.project.backend.data.admin.AdminRepository;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
=======
import tqs.project.backend.data.admin.Admin;
import tqs.project.backend.data.admin.AdminRepository;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.partner.Partner;
>>>>>>> d19509d792dc30898f61775b7a08ead04eb47b97
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.user.User;
import tqs.project.backend.util.ResolveLocation;

@Service
<<<<<<< HEAD
@Slf4j
=======
>>>>>>> d19509d792dc30898f61775b7a08ead04eb47b97
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

    public User findByUsernameAndPassword(String username, String password){
        return adminRepository.findByUsernameAndPassword(username, password) != null
            ? adminRepository.findByUsernameAndPassword(username, password)
            : partnerRepository.findByUsernameAndPassword(username, password);

    }

}
