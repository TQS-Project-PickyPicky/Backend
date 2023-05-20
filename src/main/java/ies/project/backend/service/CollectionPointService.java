package ies.project.backend.service;

import ies.project.backend.data.collection_point.CollectionPoint;
import ies.project.backend.data.collection_point.CollectionPointRepository;
import ies.project.backend.data.parcel.*;
import ies.project.backend.data.partner.PartnerRepository;
import ies.project.backend.util.CantAccessParcelException;
import ies.project.backend.util.DifferentStateException;
import ies.project.backend.util.IncorrectParcelTokenException;
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

        // TODO - Check if id is the same as the logged in user

        List<ParcelAllDto> parcelDtos = new ArrayList<>();
        for (Parcel parcel : parcels) {
            parcelDtos.add(new ParcelAllDto(parcel.getId(), parcel.getStatus()));
        }
        return parcelDtos;
    }

    public ParcelDto getParcel(Integer id) throws CantAccessParcelException {
        Parcel parcel = parcelRepository.findById(id).orElseThrow();

        // TODO - Change to ask for id of logged in user
        //CollectionPoint collectionPoint = collectionPointRepository.findById(1).orElseThrow();
        //List<Parcel> parcels = collectionPoint.getParcels();

        //if(parcels.contains(parcel)){
        //    long days = DAYS.between(LocalDate.now(),parcel.getExpectedArrival());
        //    return new ParcelDto(parcel.getId(), parcel.getStatus(), days);
        //} else {
        //    throw new CantAccessParcelException("Can't access this parcel");
        //}

        long days = DAYS.between(LocalDate.now(),parcel.getExpectedArrival());
        return new ParcelDto(parcel.getId(), parcel.getStatus(), days);
    }

    public ParcelAllDto checkIn(Integer parcelId) throws CantAccessParcelException, DifferentStateException {
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow();

        // TODO - Change to ask for id of logged in user
        //CollectionPoint collectionPoint = collectionPointRepository.findById(1).orElseThrow();
        //List<Parcel> parcels = collectionPoint.getParcels();

        //if(parcels.contains(parcel)){
        //    if(parcel.getStatus().equals(ParcelStatus.IN_TRANSIT)){
        //        parcel.setStatus(ParcelStatus.DELIVERED);
        //        parcelRepository.save(parcel);
        //        return new ParcelAllDto(parcel.getId(), parcel.getStatus());
        //    } else {
        //        throw new DifferentStateException("Parcel is not in transit");
        //    }
        //} else {
        //    throw new CantAccessParcelException("Can't access this parcel");
        //}

        if(parcel.getStatus().equals(ParcelStatus.IN_TRANSIT)){
            parcel.setStatus(ParcelStatus.DELIVERED);
            parcelRepository.save(parcel);
            return new ParcelAllDto(parcel.getId(), parcel.getStatus());
        } else {
            throw new DifferentStateException("Parcel is not in transit");
        }
    }

    public ParcelAllDto checkOut(Integer parcelId, Integer token) throws IncorrectParcelTokenException, CantAccessParcelException, DifferentStateException {
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow();

        // TODO - Change to ask for id of logged in user
        //CollectionPoint collectionPoint = collectionPointRepository.findById(1).orElseThrow();
        //List<Parcel> parcels = collectionPoint.getParcels();
//
        //if(parcels.contains(parcel)){
        //    if (parcel.getStatus().equals(ParcelStatus.DELIVERED)) {
        //        if (!token.equals(parcel.getToken())) {
        //            System.out.println("Token is not correct");
        //            throw new IncorrectParcelTokenException("Token is not correct");
        //        } else {
        //            parcel.setStatus(ParcelStatus.COLLECTED);
        //            parcelRepository.save(parcel);
        //            return new ParcelAllDto(parcel.getId(), parcel.getStatus());
        //        }
        //    } else {
        //        throw new DifferentStateException("Parcel is not in transit");
        //    }
        //} else {
        //    throw new CantAccessParcelException("Can't access this parcel");
        //}

        if (parcel.getStatus().equals(ParcelStatus.DELIVERED)) {
            if (!token.equals(parcel.getToken())) {
                System.out.println("Token is not correct");
                throw new IncorrectParcelTokenException("Token is not correct");
            } else {
                parcel.setStatus(ParcelStatus.COLLECTED);
                parcelRepository.save(parcel);
                return new ParcelAllDto(parcel.getId(), parcel.getStatus());
            }
        } else {
            throw new DifferentStateException("Parcel is not in transit");
        }
    }

    public ParcelAllDto returnParcel(Integer parcelId) throws CantAccessParcelException, DifferentStateException {
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow();

        // TODO - Change to ask for id of logged in user
        //CollectionPoint collectionPoint = collectionPointRepository.findById(1).orElseThrow();
        //List<Parcel> parcels = collectionPoint.getParcels();

        //if(parcels.contains(parcel)){
        //    if(parcel.getStatus().equals(ParcelStatus.COLLECTED)){
        //        parcel.setStatus(ParcelStatus.RETURNED);
        //        parcelRepository.save(parcel);
        //        return new ParcelAllDto(parcel.getId(), parcel.getStatus());
        //    } else {
        //        throw new DifferentStateException("Parcel is not in transit");
        //    }
        //} else {
        //    throw new CantAccessParcelException("Can't access this parcel");
        //}

        if(parcel.getStatus().equals(ParcelStatus.COLLECTED)){
            parcel.setStatus(ParcelStatus.RETURNED);
            parcelRepository.save(parcel);
            return new ParcelAllDto(parcel.getId(), parcel.getStatus());
        } else {
            throw new DifferentStateException("Parcel is not in transit");
        }

    }
}
