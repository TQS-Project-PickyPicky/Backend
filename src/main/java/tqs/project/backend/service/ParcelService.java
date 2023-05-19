package tqs.project.backend.service;

import org.springframework.stereotype.Service;

import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;

import java.util.List;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;

    public ParcelService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    public Parcel getParcel(Integer id) {
        return null;
    }

    public List<Parcel> getAllParcels() {
        return null;
    }

    public List<Parcel> getAllParcelsByStore(Integer storeId) {
        return null;
    }

    public List<Parcel> getAllParcelsByCollectionPoint(Integer collectionPointId) {
        return null;
    }

    public Parcel createParcel(String clientName, String clientEmail, Integer clientPhone, Integer clientMobilePhone, Integer storeId, Integer collectionPointId) {
        return null;
    }

    public Parcel updateParcel(Integer id, Parcel parcel, Integer token) {
        return null;
    }

    public Parcel deleteParcel(Integer id) {
        return null;
    }
}
