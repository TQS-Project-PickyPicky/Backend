package tqs.project.backend.service;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRDto;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.*;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.exception.CollectionPointNotFoundException;
import tqs.project.backend.exception.ParcelNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tqs.project.backend.util.ConverterUtils;
import tqs.project.backend.util.ResolveLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CollectionPointService {

    private final PartnerRepository partnerRepository;
    private final CollectionPointRepository collectionPointRepository;
    private final ParcelRepository parcelRepository;

    @Autowired
    public CollectionPointService(CollectionPointRepository collectionPointRepository, ParcelRepository parcelRepository, PartnerRepository partnerRepository) {
        this.collectionPointRepository = collectionPointRepository;
        this.parcelRepository = parcelRepository;
        this.partnerRepository = partnerRepository;
    }

    public double calculateDistanceBetweenPoints(
            double x1,
            double y1,
            double x2,
            double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    private List<CollectionPoint> nearestCollectionPoints(List<CollectionPoint> collectionPoints, double latitude, double longitude) {
        List<CollectionPoint> nearestCollectionPoints = new ArrayList<>(collectionPoints);
        nearestCollectionPoints.sort(Comparator.comparingDouble(cp -> calculateDistanceBetweenPoints(cp.getLatitude(), cp.getLongitude(), latitude, longitude)));
        return nearestCollectionPoints;
    }


    public boolean saveCPPoint(CollectionPoint point, String zipCode) {
    
        point.setStatus(false); // not accepted yet
    
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

    public CollectionPointRDto updateCPPoint(Integer id, CollectionPoint cp) {
        CollectionPoint oldcp = collectionPointRepository.findById(id).orElseThrow(() -> new CollectionPointNotFoundException(id));

        //Update CollectionPoint
        oldcp.setName(cp.getName());
        oldcp.setType(cp.getType());
        oldcp.setCapacity(cp.getCapacity());
        oldcp.setOwnerPhone(cp.getOwnerPhone());
        oldcp.setOwnerMobilePhone(cp.getOwnerMobilePhone());
        oldcp.setStatus(cp.getStatus());

        collectionPointRepository.save(oldcp);

        return ConverterUtils.fromCollectionPointToCollectionPointRDto(oldcp);
    }

    public CollectionPointRDto deleteCPPoint(Integer id) {
        CollectionPoint cp = collectionPointRepository.findById(id).orElseThrow(() -> new CollectionPointNotFoundException(id));
        collectionPointRepository.delete(cp);
        return ConverterUtils.fromCollectionPointToCollectionPointRDto(cp);
    }

    public List<CollectionPointRDto> getAll() {
        List<CollectionPointRDto> list = new ArrayList<>();
        for (CollectionPoint collectionPoint : collectionPointRepository.findAll()) {
            if(collectionPoint.getStatus()) {
                CollectionPointRDto collectionPointRDto = ConverterUtils.fromCollectionPointToCollectionPointRDto(collectionPoint);
                list.add(collectionPointRDto);
            }
        }
        return list;
    }

    public List<CollectionPointRDto> getAll(String zip) {
        ArrayList<Double> latlon = ResolveLocation.resolveAddress(zip);

        // Check which collection points are closest to the latlon
        List<CollectionPoint> collectionPoints = collectionPointRepository.findAll();
        List<CollectionPoint> nearestCollectionPoints = nearestCollectionPoints(collectionPoints, latlon.get(0), latlon.get(1));

        List<CollectionPointRDto> list = new ArrayList<>();
        for (CollectionPoint collectionPoint : nearestCollectionPoints) {
            if(collectionPoint.getStatus()) {
                CollectionPointRDto collectionPointRDto = ConverterUtils.fromCollectionPointToCollectionPointRDto(collectionPoint);
                list.add(collectionPointRDto);
            }
        }
        return list;
    }

    public CollectionPointRDto getCP(Integer id) {
        CollectionPoint cp = collectionPointRepository.findById(id).orElseThrow(() -> new CollectionPointNotFoundException(id));
        return ConverterUtils.fromCollectionPointToCollectionPointRDto(cp);
    }

    public List<ParcelMinimal> getAllParcels(Integer id) {
        CollectionPoint collectionPoint = collectionPointRepository.findById(id).orElseThrow();
        List<Parcel> parcels = collectionPoint.getParcels();

        // TODO - Check if id is the same as the logged in user

        List<ParcelMinimal> parcelsMinimal = new ArrayList<>();
        for (Parcel parcel : parcels) {
            parcelsMinimal.add(ConverterUtils.fromParcelToParcelMinimal(parcel));
        }
        return parcelsMinimal;
    }

    public ParcelMinimalEta getParcel(Integer id) throws ParcelNotFoundException {
        Parcel parcel = parcelRepository.findById(id).orElseThrow();

        // TODO - Change to ask for id of logged in user
        //CollectionPoint collectionPoint = collectionPointRepository.findById(1).orElseThrow();
        //List<Parcel> parcels = collectionPoint.getParcels();

        //if(parcels.contains(parcel)){
        //    long days = DAYS.between(LocalDate.now(),parcel.getExpectedArrival());
        //    return new ParcelDto(parcel.getId(), parcel.getStatus(), days);
        //} else {
        //    throw new ParcelNotFoundException("Can't access this parcel");
        //}
        return ConverterUtils.fromParcelToParcelMinimalEta(parcel);
    }
}
