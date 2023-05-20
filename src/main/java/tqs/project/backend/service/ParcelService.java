package tqs.project.backend.service;

import org.springframework.stereotype.Service;

import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.StoreRepository;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.util.TokenUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final StoreRepository storeRepository;
    private final CollectionPointRepository collectionPointRepository;

    public ParcelService(ParcelRepository parcelRepository, StoreRepository storeRepository, CollectionPointRepository collectionPointRepository) {
        this.parcelRepository = parcelRepository;
        this.storeRepository = storeRepository;
        this.collectionPointRepository = collectionPointRepository;
    }

    public Parcel getParcel(Integer id) throws ParcelNotFoundException {
        return parcelRepository.findById(id).orElseThrow(() -> new ParcelNotFoundException(id));
    }

    public List<Parcel> getAllParcels() {
        return parcelRepository.findAll();
    }

    public Parcel createParcel(String clientName, String clientEmail, Integer clientPhone, Integer clientMobilePhone, LocalDate expectedArrival, Integer storeId, Integer collectionPointId) {
        Parcel parcel = new Parcel();
        parcel.setToken(TokenUtils.generateParcelToken());
        parcel.setClientName(clientName);
        parcel.setClientEmail(clientEmail);
        parcel.setClientPhone(clientPhone);
        parcel.setClientMobilePhone(clientMobilePhone);
        parcel.setExpectedArrival(expectedArrival);
        parcel.setStatus(ParcelStatus.PLACED);
        parcel.setStore(storeRepository.findById(storeId).orElse(null));
        parcel.setCollectionPoint(collectionPointRepository.findById(collectionPointId).orElse(null));
        return parcelRepository.save(parcel);
    }

    public Parcel updateParcel(Integer id, Parcel parcel, Integer token) throws ParcelNotFoundException, InvalidParcelStatusChangeException, IncorrectParcelTokenException {
        Parcel oldParcel = parcelRepository.findById(id).orElseThrow(() -> new ParcelNotFoundException(id));
        // Check if there was a valid status change
        if (!oldParcel.getStatus().equals(parcel.getStatus())
                && (oldParcel.getStatus().equals(ParcelStatus.PLACED) && !parcel.getStatus().equals(ParcelStatus.IN_TRANSIT)
                || oldParcel.getStatus().equals(ParcelStatus.IN_TRANSIT) && !parcel.getStatus().equals(ParcelStatus.DELIVERED)
                || oldParcel.getStatus().equals(ParcelStatus.DELIVERED) && !parcel.getStatus().equals(ParcelStatus.COLLECTED)
                || oldParcel.getStatus().equals(ParcelStatus.COLLECTED) && !parcel.getStatus().equals(ParcelStatus.RETURNED))
        ) {
            throw new InvalidParcelStatusChangeException(oldParcel.getStatus(), parcel.getStatus());
        }
        // Check if the token is valid
        if (oldParcel.getStatus().equals(ParcelStatus.DELIVERED) && parcel.getStatus().equals(ParcelStatus.COLLECTED)
                && !oldParcel.getToken().equals(token)) {
            throw new IncorrectParcelTokenException(token, oldParcel.getId());
        }
        // Update the parcel
        oldParcel.setClientName(parcel.getClientName());
        oldParcel.setClientEmail(parcel.getClientEmail());
        oldParcel.setClientPhone(parcel.getClientPhone());
        oldParcel.setClientMobilePhone(parcel.getClientMobilePhone());
        oldParcel.setExpectedArrival(parcel.getExpectedArrival());
        oldParcel.setStatus(parcel.getStatus());
        return parcelRepository.save(oldParcel);
    }

    public Parcel deleteParcel(Integer id) throws ParcelNotFoundException {
        Parcel parcel = parcelRepository.findById(id).orElseThrow(() -> new ParcelNotFoundException(id));
        parcelRepository.delete(parcel);
        return parcel;
    }
}
