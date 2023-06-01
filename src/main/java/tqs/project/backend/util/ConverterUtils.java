package tqs.project.backend.util;

import tqs.project.backend.data.collection_point.*;
import tqs.project.backend.data.parcel.*;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreDto;
import tqs.project.backend.data.store.StoreRepository;
import tqs.project.backend.data.store.StoreUpdateDto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

    public static ParcelMinimal fromParcelToParcelMinimal(Parcel parcel) {
        return new ParcelMinimal(
                parcel.getId(),
                parcel.getStatus());
    }

    public static ParcelMinimalEta fromParcelToParcelMinimalEta(Parcel parcel) {
        return new ParcelMinimalEta(
                parcel.getId(),
                parcel.getStatus(),
                ChronoUnit.DAYS.between(LocalDate.now(), parcel.getExpectedArrival()));
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

    public static List<CollectionPointDDto> fromCollectionPointsToCollectionPointDDto(List<CollectionPoint> collectionPoints){
        
        List<CollectionPointDDto> dtoList = collectionPoints.stream()
            .map(collectionPoint -> {
                CollectionPointDDto dto = new CollectionPointDDto();
                dto.setId(collectionPoint.getId());
                dto.setName(collectionPoint.getName());
                dto.setType(collectionPoint.getType());
                dto.setEmail(collectionPoint.getOwnerEmail());
                return dto;
            })
            .collect(Collectors.toList());
    
        return dtoList;
    }

    public static Store fromStoreDtoToStore(StoreDto storeDto, ParcelRepository parcelRepository) {
        return new Store(
                storeDto.getId(),
                storeDto.getName(),
                parcelRepository.findAllById(storeDto.getParcelsId()));
    }

    public static Parcel fromParcelUpdateDtoToParcel(ParcelUpdateDto parcelUpdateDto) {
        return new Parcel(
                null,
                null,
                parcelUpdateDto.getClientName(),
                parcelUpdateDto.getClientEmail(),
                parcelUpdateDto.getClientPhone(),
                parcelUpdateDto.getClientMobilePhone(),
                LocalDate.parse(parcelUpdateDto.getExpectedArrival()),
                ParcelStatus.valueOf(parcelUpdateDto.getStatus()),
                null,
                null);
    }

    public static Store fromStoreUpdateDtoToStore(StoreUpdateDto storeUpdateDto) {
        return new Store(
                null,
                storeUpdateDto.getName(),
                null);
    }

    public static CollectionPoint fromCollectionPointDTOToCollectionPoint(CollectionPointDto collectionPointDto){
        return new CollectionPoint(
            null, 
            collectionPointDto.getName(), 
            collectionPointDto.getType(),
            collectionPointDto.getCapacity(), 
            collectionPointDto.getAddress(), 
            null, 
            null, 
            collectionPointDto.getOwnerName(), 
            collectionPointDto.getOwnerEmail(), 
            collectionPointDto.getOwnerGender(), 
            collectionPointDto.getOwnerPhone(), 
            collectionPointDto.getOwnerMobilePhone(), 
            null, 
            collectionPointDto.getPartner(), 
            null);
    }

    public static CollectionPoint fromCollectionPointCreateDtoToCollectionPoint(CollectionPointCreateDto collectionPointCreateDto){
        return new CollectionPoint(
                null,
            collectionPointCreateDto.getName(),
            collectionPointCreateDto.getType(),
            collectionPointCreateDto.getCapacity(),
            collectionPointCreateDto.getAddress(),
            null,
            null,
            collectionPointCreateDto.getOwnerName(),
            collectionPointCreateDto.getOwnerEmail(),
            collectionPointCreateDto.getOwnerGender(),
            collectionPointCreateDto.getOwnerPhone(),
            collectionPointCreateDto.getOwnerMobilePhone(),
            null,
            collectionPointCreateDto.getPartner(),
            null);
    }

    public static CollectionPointRDto fromCollectionPointToCollectionPointRDto(CollectionPoint collectionPoint){
        return new CollectionPointRDto(
            collectionPoint.getId(),
            collectionPoint.getName(),
            collectionPoint.getType(),
            collectionPoint.getCapacity(),
            collectionPoint.getAddress(),
            collectionPoint.getStatus());
    }

    public static CollectionPoint fromCollectionPointUpdateDtoToCollectionPoint(CollectionPointUpdateDto collectionPointUpdateDto){
        return new CollectionPoint(
            null,
            collectionPointUpdateDto.getName(),
            collectionPointUpdateDto.getType(),
            collectionPointUpdateDto.getCapacity(),
            null,
            null,
            null,
            null,
            null,
            null,
            collectionPointUpdateDto.getOwnerPhone(),
            collectionPointUpdateDto.getOwnerMobilePhone(),
            collectionPointUpdateDto.getStatus(),
            null,
            null);
    }
}
