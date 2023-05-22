package tqs.project.backend.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.utils.ResolveLocation;

@Service
@Slf4j
public class CollectionPointService {

    @Autowired
    private CollectionPointRepository cpRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    public boolean saveCPPoint(CollectionPoint point, String zipCode, String city){
        point.setStatus(false); //not accepted yet

        ArrayList<Double> latlon = ResolveLocation.resolveAddress(zipCode, city);
        if (latlon == null){
            return false;
        }

        point.setLatitude(latlon.get(0));
        point.setLongitude(latlon.get(1));

        partnerRepository.save(point.getPartner());
        cpRepository.save(point);

        log.info("" + point);

        return true;

    }

}
