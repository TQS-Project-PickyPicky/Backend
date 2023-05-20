package ies.project.backend.service;

import ies.project.backend.data.collection_point.CollectionPoint;
import ies.project.backend.data.collection_point.CollectionPointRepository;
import ies.project.backend.data.parcel.*;
import ies.project.backend.data.store.Store;
import ies.project.backend.data.store.StoreStatus;
import ies.project.backend.util.CantAccessParcelException;
import ies.project.backend.util.DifferentStateException;
import ies.project.backend.util.IncorrectParcelTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CollectionPointServiceTest {

    @Mock(lenient = true)
    private CollectionPointRepository collectionPointRepository;

    @Mock(lenient = true)
    private ParcelRepository parcelRepository;

    @InjectMocks
    private CollectionPointService collectionPointService;

    @BeforeEach
    void setUp() {
        CollectionPoint collectionPoint = new CollectionPoint();
        collectionPoint.setName("Collection Point 1");
        collectionPoint.setType("Collection Point");
        collectionPoint.setCapacity(100);
        collectionPoint.setAddress("Rua do ISEP");
        collectionPoint.setLatitude(41.178);
        collectionPoint.setLongitude(-8.608);
        collectionPoint.setOwnerName("João");
        collectionPoint.setOwnerEmail("joao@ua.pt");
        collectionPoint.setOwnerGender("M");
        collectionPoint.setOwnerPhone(123456789);
        collectionPoint.setOwnerMobilePhone(987654321);

        Store store = new Store();
        store.setName("Store 1");
        store.setStatus(StoreStatus.ACCEPTED);

        Parcel parcel = new Parcel();
        parcel.setId(3);
        parcel.setToken(123456);
        parcel.setClientName("João");
        parcel.setClientEmail("joao@ua.pt");
        parcel.setClientPhone(123456789);
        parcel.setClientMobilePhone(987654321);
        parcel.setExpectedArrival(LocalDate.now().plusDays(5));
        parcel.setStore(store);
        parcel.setStatus(ParcelStatus.IN_TRANSIT);
        parcel.setCollectionPoint(collectionPoint);

        Parcel parcel2 = new Parcel();
        parcel2.setId(4);
        parcel2.setToken(123456);
        parcel2.setClientName("Jorge");
        parcel2.setClientEmail("jorge@ua.pt");
        parcel2.setClientPhone(123456789);
        parcel2.setClientMobilePhone(987654321);
        parcel2.setExpectedArrival(LocalDate.now().plusDays(7));
        parcel2.setStore(store);
        parcel2.setStatus(ParcelStatus.DELIVERED);
        parcel2.setCollectionPoint(collectionPoint);

        Parcel parcel4 = new Parcel();

        Parcel parcel3 = new Parcel();
        parcel3.setId(6);
        parcel3.setToken(123456);
        parcel3.setClientName("Gabriel");
        parcel3.setClientEmail("gabriel@ua.pt");
        parcel3.setClientPhone(123456789);
        parcel3.setClientMobilePhone(987654321);
        parcel3.setExpectedArrival(LocalDate.now());
        parcel3.setStore(store);
        parcel3.setStatus(ParcelStatus.COLLECTED);
        parcel3.setCollectionPoint(collectionPoint);

        List<Parcel> parcels = new ArrayList<>();
        parcels.add(parcel);
        parcels.add(parcel2);
        parcels.add(parcel3);

        collectionPoint.setParcels(parcels);

        when(collectionPointRepository.findById(1))
                .thenReturn(Optional.of(collectionPoint));

        when(parcelRepository.findById(3))
                .thenReturn(Optional.of(parcel));

        when(parcelRepository.findById(4))
                .thenReturn(Optional.of(parcel2));

        when(parcelRepository.findById(5))
                .thenReturn(Optional.of(parcel4));

        when(parcelRepository.findById(6))
                .thenReturn(Optional.of(parcel3));


    }

    @Test
    void getAllParcelsFromCollectionPoint() {
        List<ParcelAllDto> parcels = collectionPointService.getallParcels(1);

        ParcelAllDto parcel = new ParcelAllDto(3,ParcelStatus.IN_TRANSIT);
        ParcelAllDto parcel2 = new ParcelAllDto(4,ParcelStatus.DELIVERED);
        ParcelAllDto parcel3 = new ParcelAllDto(6,ParcelStatus.COLLECTED);

        assertEquals(3, parcels.size());
        assertEquals(parcel.getId(), parcels.get(0).getId());
        assertEquals(parcel.getStatus(), parcels.get(0).getStatus());
        assertEquals(parcel2.getId(), parcels.get(1).getId());
        assertEquals(parcel2.getStatus(), parcels.get(1).getStatus());
        assertEquals(parcel3.getId(), parcels.get(2).getId());
        assertEquals(parcel3.getStatus(), parcels.get(2).getStatus());
    }

    @Test
    void getParcel_ifParcelExistsCollectionPoint() throws CantAccessParcelException {
        ParcelDto parcel = collectionPointService.getParcel(3);

        long days = DAYS.between(LocalDate.now(),LocalDate.now().plusDays(5));

        ParcelDto parcelDto = new ParcelDto(3,ParcelStatus.IN_TRANSIT, days);

        assertEquals(3, parcel.getId());
        assertEquals(parcelDto.getStatus(), parcel.getStatus());
        assertEquals(parcelDto.getEta(), parcel.getEta());
    }

    //@Test
    //void getParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(CantAccessParcelException.class, () -> {
    //        collectionPointService.getParcel(5);
    //    });
    //}

    @Test
    void checkinParcel_ifParcelExistsCollectionPoint() throws CantAccessParcelException, DifferentStateException {
        ParcelAllDto parcel = collectionPointService.checkIn(3);

        ParcelAllDto parcelAllDto = new ParcelAllDto(3,ParcelStatus.DELIVERED);

        assertEquals(3, parcel.getId());
        assertEquals(parcelAllDto.getStatus(), parcel.getStatus());
    }

    //@Test
    //void checkinParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(CantAccessParcelException.class, () -> {
    //        collectionPointService.checkIn(5);
    //    });
    //}

    @Test
    void checkinParcel_ifParcelDoesNotHaveStateInTransit() {
        assertThrows(DifferentStateException.class, () -> {
            collectionPointService.checkIn(4);
        });
    }

    @Test
    void checkoutParcel_ifParcelExistsCollectionPoint() throws CantAccessParcelException, DifferentStateException, IncorrectParcelTokenException {
        ParcelAllDto parcel = collectionPointService.checkOut(4,123456);

        ParcelAllDto parcelDto = new ParcelAllDto(3,ParcelStatus.COLLECTED);

        assertEquals(4, parcel.getId());
        assertEquals(parcelDto.getStatus(), parcel.getStatus());
    }

    //@Test
    //void checkoutParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(CantAccessParcelException.class, () -> {
    //        collectionPointService.checkOut(5,123456);
    //    });
    //}

    @Test
    void checkoutParcel_ifParcelDoesNotHaveStateDelivered() {
        assertThrows(DifferentStateException.class, () -> {
            collectionPointService.checkOut(3,123456);
        });
    }

    @Test
    void checkoutParcel_ifParcelDoesNotHaveCorrectToken() {
        assertThrows(IncorrectParcelTokenException.class, () -> {
            collectionPointService.checkOut(4,1234567);
        });
    }

    @Test
    void returnParcel__ifParcelExistsCollectionPoint() throws DifferentStateException, CantAccessParcelException {
        ParcelAllDto parcel = collectionPointService.returnParcel(6);

        ParcelAllDto parcelDto = new ParcelAllDto(6,ParcelStatus.RETURNED);

        assertEquals(6, parcel.getId());
        assertEquals(parcelDto.getStatus(), parcel.getStatus());
    }

    //@Test
    //void returnParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(CantAccessParcelException.class, () -> {
    //        collectionPointService.returnParcel(5);
    //    });
    //}

    @Test
    void returnParcel_ifParcelDoesNotHaveStateCollected() {
        assertThrows(DifferentStateException.class, () -> {
            collectionPointService.returnParcel(3);
        });
    }
}