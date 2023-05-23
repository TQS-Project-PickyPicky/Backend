package tqs.project.backend.service;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.*;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.util.ResolveLocation;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

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

    @Mock(lenient = true)
    private PartnerRepository partnerRepository;

    @InjectMocks
    private CollectionPointService collectionPointService;


    @BeforeEach
    public void setUp() {
        //CP + zip code + city
        CollectionPoint cp = new CollectionPoint();
        cp.setName("cp1");
        cp.setType("Library");
        cp.setCapacity(100);
        cp.setAddress("Rua do ISEP");
        cp.setOwnerName("João");
        cp.setOwnerEmail("joao@ua.pt");
        cp.setOwnerPhone(910000000);

        Partner partner = new Partner();
        partner.setId(1);
        partner.setPassword("pass");
        partner.setUsername("partner1");

        cp.setPartner(partner);

        //mock repository methods
        when(collectionPointRepository.save(Mockito.any(CollectionPoint.class))).thenReturn(cp);
        when(partnerRepository.save(Mockito.any(Partner.class))).thenReturn(partner);

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
    public void saveCPPointSuccess_Test(){

        //to use in functions inside the service
        CollectionPoint cp = new CollectionPoint();
        String zipCode = "3810-193";
        String city = "Aveiro";


        boolean result = collectionPointService.saveCPPoint(cp, zipCode, city);
        ArrayList<Double> coordinates = ResolveLocation.resolveAddress(zipCode, city);

        assertTrue(result);
        assertFalse(cp.getStatus());
        assertEquals(coordinates.get(0), cp.getLatitude());
        assertEquals(coordinates.get(1), cp.getLongitude());

    }

    @Test
    public void saveCPPointFailureAPITest(){

        CollectionPoint cp = new CollectionPoint();
        String zipCode = "";
        String city = "";

        boolean result = collectionPointService.saveCPPoint(cp, zipCode, city);
        ArrayList<Double> coordinates = ResolveLocation.resolveAddress(zipCode, city);

        assertFalse(result);
        assertFalse(cp.getStatus());

        assertEquals(null, cp.getAddress());
        assertNull(coordinates);
        assertEquals(null, cp.getLatitude());
        assertEquals(null, cp.getLongitude());
    }

    @Test
    void getAllParcelsFromCollectionPoint() {
        List<ParcelMinimal> parcels = collectionPointService.getAllParcels(1);

        ParcelMinimal parcel = new ParcelMinimal(3, ParcelStatus.IN_TRANSIT);
        ParcelMinimal parcel2 = new ParcelMinimal(4, ParcelStatus.DELIVERED);
        ParcelMinimal parcel3 = new ParcelMinimal(6, ParcelStatus.COLLECTED);

        assertEquals(3, parcels.size());
        assertEquals(parcel.getId(), parcels.get(0).getId());
        assertEquals(parcel.getStatus(), parcels.get(0).getStatus());
        assertEquals(parcel2.getId(), parcels.get(1).getId());
        assertEquals(parcel2.getStatus(), parcels.get(1).getStatus());
        assertEquals(parcel3.getId(), parcels.get(2).getId());
        assertEquals(parcel3.getStatus(), parcels.get(2).getStatus());
    }

    @Test
    void getParcel_ifParcelExistsCollectionPoint() throws ParcelNotFoundException {
        ParcelMinimalEta parcel = collectionPointService.getParcel(3);

        long days = DAYS.between(LocalDate.now(), LocalDate.now().plusDays(5));

        ParcelMinimalEta parcelCheck = new ParcelMinimalEta(3, ParcelStatus.IN_TRANSIT, days);

        assertEquals(parcelCheck.getId(), parcel.getId());
        assertEquals(parcelCheck.getStatus(), parcel.getStatus());
        assertEquals(parcelCheck.getEta(), parcel.getEta());
    }

    //@Test
    //void getParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(ParcelNotFoundException.class, () -> {
    //        collectionPointService.getParcel(5);
    //    });
    //}

    @Test
    void checkinParcel_ifParcelExistsCollectionPoint() throws ParcelNotFoundException, InvalidParcelStatusChangeException {
        ParcelMinimal parcel = collectionPointService.checkIn(3);

        ParcelMinimal parcelCheck = new ParcelMinimal(3, ParcelStatus.DELIVERED);

        assertEquals(parcelCheck.getId(), parcel.getId());
        assertEquals(parcelCheck.getStatus(), parcel.getStatus());
    }

    //@Test
    //void checkinParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(ParcelNotFoundException.class, () -> {
    //        collectionPointService.checkIn(5);
    //    });
    //}

    @Test
    void checkinParcel_ifParcelDoesNotHaveStateInTransit() {
        assertThrows(InvalidParcelStatusChangeException.class, () -> {
            collectionPointService.checkIn(4);
        });
    }

    @Test
    void checkoutParcel_ifParcelExistsCollectionPoint() throws ParcelNotFoundException, InvalidParcelStatusChangeException, IncorrectParcelTokenException {
        ParcelMinimal parcel = collectionPointService.checkOut(4, 123456);

        ParcelMinimal parcelCheck = new ParcelMinimal(4, ParcelStatus.COLLECTED);

        assertEquals(parcelCheck.getId(), parcel.getId());
        assertEquals(parcelCheck.getStatus(), parcel.getStatus());
    }

    //@Test
    //void checkoutParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(ParcelNotFoundException.class, () -> {
    //        collectionPointService.checkOut(5,123456);
    //    });
    //}

    @Test
    void checkoutParcel_ifParcelDoesNotHaveStateDelivered() {
        assertThrows(InvalidParcelStatusChangeException.class, () -> {
            collectionPointService.checkOut(3, 123456);
        });
    }

    @Test
    void checkoutParcel_ifParcelDoesNotHaveCorrectToken() {
        assertThrows(IncorrectParcelTokenException.class, () -> {
            collectionPointService.checkOut(4, 1234567);
        });
    }

    @Test
    void returnParcel__ifParcelExistsCollectionPoint() throws InvalidParcelStatusChangeException, ParcelNotFoundException {
        ParcelMinimal parcel = collectionPointService.returnParcel(6);

        ParcelMinimal parcelCheck = new ParcelMinimal(6, ParcelStatus.RETURNED);

        assertEquals(parcelCheck.getId(), parcel.getId());
        assertEquals(parcelCheck.getStatus(), parcel.getStatus());
    }

    //@Test
    //void returnParcel_ifParcelDoesNotExistCollectionPoint() {
    //    assertThrows(ParcelNotFoundException.class, () -> {
    //        collectionPointService.returnParcel(5);
    //    });
    //}

    @Test
    void returnParcel_ifParcelDoesNotHaveStateCollected() {
        assertThrows(InvalidParcelStatusChangeException.class, () -> {
            collectionPointService.returnParcel(3);
        });
    }
}