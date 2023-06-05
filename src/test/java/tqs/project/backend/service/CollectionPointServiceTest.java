package tqs.project.backend.service;

import tqs.project.backend.data.collection_point.*;
import tqs.project.backend.data.parcel.*;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.exception.CollectionPointNotFoundException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.util.ConverterUtils;
import tqs.project.backend.util.ResolveLocation;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
    void setUp() {
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
        collectionPoint.setId(1);
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
        collectionPoint.setStatus(true);

        CollectionPoint collectionPoint2 = new CollectionPoint();
        collectionPoint2.setId(2);
        collectionPoint2.setName("Collection Point 2");
        collectionPoint2.setType("Collection Point");
        collectionPoint2.setCapacity(100);
        collectionPoint2.setAddress("Rua do Prof António");
        collectionPoint2.setLatitude(41.174660);
        collectionPoint2.setLongitude(-8.588069);
        collectionPoint2.setOwnerName("João");
        collectionPoint2.setStatus(true);
        collectionPoint2.setOwnerEmail("joao@ua.pt");
        collectionPoint2.setOwnerGender("M");
        collectionPoint2.setOwnerPhone(123456789);
        collectionPoint2.setOwnerMobilePhone(987654321);

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
        when(collectionPointRepository.findAll())
                .thenReturn(List.of(collectionPoint, collectionPoint2));
    }

    @Test
    void getCollectionPointById() {
        CollectionPointRDto collectionPoint = collectionPointService.getCP(1);
        assertEquals(1, collectionPoint.getId());
        assertEquals("Collection Point 1", collectionPoint.getName());
    }

    @Test
    void getCollectionPointByIdNotFound() {
        assertThrows(CollectionPointNotFoundException.class, () -> collectionPointService.getCP(3));
    }

    @Test
    void getAllCollectionPoints() {
        List<CollectionPointRDto> collectionPoints = collectionPointService.getAll();
        assertEquals(2, collectionPoints.size());
        assertEquals(1, collectionPoints.get(0).getId());
        assertEquals(2, collectionPoints.get(1).getId());
        assertEquals("Collection Point 1", collectionPoints.get(0).getName());
        assertEquals("Collection Point 2", collectionPoints.get(1).getName());
    }

    @Test
    void getAllCollectionPointsByLocation() {
        List<CollectionPointRDto> collectionPoints = collectionPointService.getAll("4435-677");
        assertEquals(2, collectionPoints.size());
        assertEquals(2, collectionPoints.get(0).getId());
        assertEquals(1, collectionPoints.get(1).getId());
    }

    @Test
    void saveCPPointSuccess_Test(){

        //to use in functions inside the service
        CollectionPointDto cp = new CollectionPointDto();
        String zipCode = "3810-193";

        CollectionPoint collectionPoint = ConverterUtils.fromCollectionPointDTOToCollectionPoint(cp);

        boolean result = collectionPointService.saveCPPoint(collectionPoint, zipCode);
        ArrayList<Double> coordinates = ResolveLocation.resolveAddress(zipCode);

        assertTrue(result);
        assertFalse(collectionPoint.getStatus());
        assertEquals(coordinates.get(0), collectionPoint.getLatitude());
        assertEquals(coordinates.get(1), collectionPoint.getLongitude());

    }

    @Test
    void saveCPPointFailureAPITest(){

        CollectionPoint cp = new CollectionPoint();
        String zipCode = "";

        boolean result = collectionPointService.saveCPPoint(cp, zipCode);
        ArrayList<Double> coordinates = ResolveLocation.resolveAddress(zipCode);

        assertFalse(result);
        assertFalse(cp.getStatus());

        assertEquals(null, cp.getAddress());
        assertEquals(coordinates.size(), 0);
        assertEquals(null, cp.getLatitude());
        assertEquals(null, cp.getLongitude());
    }

    @Test
    void updateCPPointSuccess_Test(){
        CollectionPointUpdateDto cp = new CollectionPointUpdateDto("Collection Point 1", "Lavandaria", 50, 965833174, 965833174,true);

        CollectionPointRDto cop = collectionPointService.updateCPPoint(1,ConverterUtils.fromCollectionPointUpdateDtoToCollectionPoint(cp));

        assertEquals(cop.getName(), cp.getName());
        assertEquals(cop.getType(), cp.getType());
        assertEquals(cop.getCapacity(), cp.getCapacity());
    }

    @Test
    void updateCPPointFailure_Test(){
        CollectionPointUpdateDto cp = new CollectionPointUpdateDto("Collection Point 1", "Lavandaria", 50, 965833174, 965833174,true);

        assertThrows(CollectionPointNotFoundException.class, () -> {
            collectionPointService.updateCPPoint(3,ConverterUtils.fromCollectionPointUpdateDtoToCollectionPoint(cp));
        });
    }

    @Test
    void deleteCPPointSuccess_Test(){
        CollectionPointRDto result = collectionPointService.deleteCPPoint(1);

        assertThat(result).isNotNull();
        assertEquals(result.getId(), 1);
        verify(collectionPointRepository, times(1)).delete(any());
    }

    @Test
    void deleteCPPointFailure_Test(){
        assertThrows(CollectionPointNotFoundException.class, () -> {
            collectionPointService.deleteCPPoint(3);
        });
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
}