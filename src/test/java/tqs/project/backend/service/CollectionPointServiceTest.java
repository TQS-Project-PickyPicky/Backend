package tqs.project.backend.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.utils.ResolveLocation;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CollectionPointServiceTest {
    
    @Mock(lenient = true)
    private CollectionPointRepository cpRepository;
 
    @Mock(lenient = true)
    private PartnerRepository partnerRepository;

    @InjectMocks 
    private CollectionPointService cpService;

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
        when(cpRepository.save(Mockito.any(CollectionPoint.class))).thenReturn(cp);
        when(partnerRepository.save(Mockito.any(Partner.class))).thenReturn(partner);

    }

    @Test
    public void saveCPPointSuccess_Test(){

        //to use in functions inside the service
        CollectionPoint cp = new CollectionPoint();
        String zipCode = "3810-193";
        String city = "Aveiro";


        boolean result = cpService.saveCPPoint(cp, zipCode, city);
        ArrayList<Double> coordinates = ResolveLocation.resolveAddress(zipCode, city);

        assertTrue(result);
        assertFalse(cp.getStatus());
        
        assertEquals(zipCode + ", " + city + ", Portugal", cp.getAddress());
        assertEquals(coordinates.get(0), cp.getLatitude());
        assertEquals(coordinates.get(1), cp.getLongitude());

    }

    @Test
    public void saveCPPointFailureAPITest(){

        CollectionPoint cp = new CollectionPoint();
        String zipCode = "";
        String city = "";

        boolean result = cpService.saveCPPoint(cp, zipCode, city);
        ArrayList<Double> coordinates = ResolveLocation.resolveAddress(zipCode, city);

        assertFalse(result);
        assertFalse(cp.getStatus());

        assertEquals(null, cp.getAddress());
        assertNull(coordinates);
        assertEquals(null, cp.getLatitude());
        assertEquals(null, cp.getLongitude());
    }
}
