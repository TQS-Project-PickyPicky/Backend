package ies.project.backend.service;

import ies.project.backend.data.collection_point.CollectionPoint;
import ies.project.backend.data.collection_point.CollectionPointRepository;
import ies.project.backend.data.parcel.*;
import ies.project.backend.data.partner.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class CollectionPointService {

    private final CollectionPointRepository collectionPointRepository;
    private final ParcelRepository parcelRepository;

    @Autowired
    public CollectionPointService(CollectionPointRepository collectionPointRepository, ParcelRepository parcelRepository) {
        this.collectionPointRepository = collectionPointRepository;
        this.parcelRepository = parcelRepository;
    }

    public List<ParcelAllDto> getallParcels(Integer id) {
        CollectionPoint collectionPoint = collectionPointRepository.findById(id).orElseThrow();
        List<Parcel> parcels = collectionPoint.getParcels();

        List<ParcelAllDto> parcelDtos = new ArrayList<>();
        for (Parcel parcel : parcels) {
            parcelDtos.add(new ParcelAllDto(parcel.getId(), parcel.getStatus()));
        }
        return parcelDtos;
    }

    public ParcelDto getParcel(Integer id) {
        Parcel parcel = parcelRepository.findById(id).orElseThrow();

        long days = DAYS.between(LocalDate.now(),parcel.getExpectedArrival());

        return new ParcelDto(parcel.getId(), parcel.getStatus(), days);
    }

    public void checkIn(Integer parcelId) {
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow();
        parcel.setStatus(ParcelStatus.DELIVERED);
        parcelRepository.save(parcel);
    }

    public boolean checkOut(Integer parcelId, Integer token) {
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow();
        if (!token.equals(parcel.getToken())) {
            System.out.println("Token is not correct");
            return false;
        } else {
            parcel.setStatus(ParcelStatus.COLLECTED);
            parcelRepository.save(parcel);
            return true;
        }
    }

    public void returnParcel(Integer parcelId) {
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow();
        parcel.setStatus(ParcelStatus.RETURNED);
        parcelRepository.save(parcel);
    }
}
