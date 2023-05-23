package tqs.project.backend.service;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.*;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.backend.util.ConverterUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionPointService {

    private final CollectionPointRepository collectionPointRepository;
    private final ParcelRepository parcelRepository;

    @Autowired
    public CollectionPointService(CollectionPointRepository collectionPointRepository, ParcelRepository parcelRepository) {
        this.collectionPointRepository = collectionPointRepository;
        this.parcelRepository = parcelRepository;
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

    public ParcelMinimal checkIn(Integer parcelId) throws ParcelNotFoundException, InvalidParcelStatusChangeException {
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
        //        throw new InvalidParcelStatusChangeException("Parcel is not in transit");
        //    }
        //} else {
        //    throw new ParcelNotFoundException("Can't access this parcel");
        //}

        if(parcel.getStatus().equals(ParcelStatus.IN_TRANSIT)){
            parcel.setStatus(ParcelStatus.DELIVERED);
            parcelRepository.save(parcel);
            return ConverterUtils.fromParcelToParcelMinimal(parcel);
        } else {
            throw new InvalidParcelStatusChangeException(parcel.getStatus(), ParcelStatus.DELIVERED);
        }
    }

    public ParcelMinimal checkOut(Integer parcelId, Integer token) throws IncorrectParcelTokenException, ParcelNotFoundException, InvalidParcelStatusChangeException {
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
        //        throw new InvalidParcelStatusChangeException("Parcel is not in transit");
        //    }
        //} else {
        //    throw new ParcelNotFoundException("Can't access this parcel");
        //}

        if (parcel.getStatus().equals(ParcelStatus.DELIVERED)) {
            if (!token.equals(parcel.getToken())) {
                throw new IncorrectParcelTokenException(token, parcelId);
            } else {
                parcel.setStatus(ParcelStatus.COLLECTED);
                parcelRepository.save(parcel);
                return ConverterUtils.fromParcelToParcelMinimal(parcel);
            }
        } else {
            throw new InvalidParcelStatusChangeException(parcel.getStatus(), ParcelStatus.COLLECTED);
        }
    }

    public ParcelMinimal returnParcel(Integer parcelId) throws ParcelNotFoundException, InvalidParcelStatusChangeException {
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
        //        throw new InvalidParcelStatusChangeException("Parcel is not in transit");
        //    }
        //} else {
        //    throw new ParcelNotFoundException("Can't access this parcel");
        //}

        if(parcel.getStatus().equals(ParcelStatus.COLLECTED)){
            parcel.setStatus(ParcelStatus.RETURNED);
            parcelRepository.save(parcel);
            return ConverterUtils.fromParcelToParcelMinimal(parcel);
        } else {
            throw new InvalidParcelStatusChangeException(parcel.getStatus(), ParcelStatus.RETURNED);
        }

    }
}
