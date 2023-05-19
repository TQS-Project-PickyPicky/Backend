package tqs.project.backend.util;

import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelDto;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.StoreRepository;

import java.time.LocalDate;

public class ConverterUtils {

    public static ParcelDto fromParcel(Parcel parcel) {
        return new ParcelDto(
                parcel.getId(),
                parcel.getToken(),
                parcel.getClientName(),
                parcel.getClientEmail(),
                parcel.getClientPhone(),
                parcel.getClientMobilePhone(),
                parcel.getExpectedArrival().toString(),
                parcel.getStatus().toString(),
                parcel.getStore().getId(),
                parcel.getCollectionPoint().getId()
        );
    }

    public static Parcel toParcel(ParcelDto parcelDto, StoreRepository storeRepository, CollectionPointRepository collectionPointRepository) {
        return new Parcel(
                parcelDto.getId(),
                parcelDto.getToken(),
                parcelDto.getClientName(),
                parcelDto.getClientEmail(),
                parcelDto.getClientPhone(),
                parcelDto.getClientMobilePhone(),
                LocalDate.parse(parcelDto.getExpectedArrival()),
                ParcelStatus.valueOf(parcelDto.getStatus()),
                storeRepository.findById(parcelDto.getStoreId()).orElse(null),
                collectionPointRepository.findById(parcelDto.getCollectionPointId()).orElse(null)
        );
    }
}
