package tqs.project.backend.util;

import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelDto;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreDto;
import tqs.project.backend.data.store.StoreRepository;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class ConverterUtils {

    private ConverterUtils() {
    }

    public static ParcelDto fromParcelToParcelDto(Parcel parcel) {
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
                parcel.getCollectionPoint().getId());
    }

    public static Parcel fromParcelDtoToParcel(ParcelDto parcelDto, StoreRepository storeRepository, CollectionPointRepository collectionPointRepository) {
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
                collectionPointRepository.findById(parcelDto.getCollectionPointId()).orElse(null));
    }

    public static StoreDto fromStoreToStoreDto(Store store) {
        return new StoreDto(
                store.getId(),
                store.getName(),
                store.getParcels().stream().map(Parcel::getId).collect(Collectors.toList()));
    }

    public static Store fromStoreDtoToStore(StoreDto storeDto, ParcelRepository parcelRepository) {
        return new Store(
                storeDto.getId(),
                storeDto.getName(),
                parcelRepository.findAllById(storeDto.getParcelIds()));
    }
}
