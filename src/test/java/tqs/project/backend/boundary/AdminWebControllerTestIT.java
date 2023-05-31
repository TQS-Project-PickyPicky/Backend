package tqs.project.backend.boundary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDDto;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.store.StoreRepository;
import tqs.project.backend.service.AdminService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "application-integrationtest.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class AdminWebControllerTestIT{

    @LocalServerPort
    int localPort;

    private CollectionPoint cp1;
    private List<Parcel> parcels;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionPointRepository collectionPointRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired 
    private PartnerRepository partnerRepository;

    @Autowired 
    private AdminService adminService;

    @BeforeEach
    void setup(){
        partnerRepository.deleteAll();
        collectionPointRepository.deleteAll();
        parcelRepository.deleteAll();
        storeRepository.deleteAll();

        cp1 = new CollectionPoint();
        cp1.setName("nome1");
        cp1.setType("library");
        cp1.setCapacity(100);
        cp1.setAddress("Some address 1");
        cp1.setLatitude(-8.1);
        cp1.setLongitude(42.0);
        cp1.setOwnerName("Matilde");
        cp1.setOwnerEmail("email1@ua.pt");
        cp1.setOwnerGender("Female");
        cp1.setOwnerPhone(910000000);
        cp1.setStatus(true);

        collectionPointRepository.save(cp1);

        parcels = new ArrayList<>();
        Parcel parcel1 = new Parcel();
        parcel1.setToken(123);
        parcel1.setClientName("John Doe");
        parcel1.setClientEmail("john@example.com");
        parcel1.setClientPhone(123456789);
        parcel1.setClientMobilePhone(987654321);
        parcel1.setExpectedArrival(LocalDate.now().plusDays(2));
        parcel1.setStatus(ParcelStatus.PLACED);
        parcel1.setCollectionPoint(cp1); 

        Parcel parcel2 = new Parcel();
        parcel2.setToken(456);
        parcel2.setClientName("Jane Smith");
        parcel2.setClientEmail("jane@example.com");
        parcel2.setClientPhone(987654321);
        parcel2.setClientMobilePhone(123456789);
        parcel2.setExpectedArrival(LocalDate.now().plusDays(3));
        parcel2.setStatus(ParcelStatus.IN_TRANSIT);
        parcel2.setCollectionPoint(cp1); 

        Parcel parcel3 = new Parcel();
        parcel3.setToken(789);
        parcel3.setClientName("Alice Johnson");
        parcel3.setClientEmail("alice@example.com");
        parcel3.setClientPhone(456789123);
        parcel3.setClientMobilePhone(321654987);
        parcel3.setStatus(ParcelStatus.DELIVERED);
        parcel3.setCollectionPoint(cp1);

        Parcel parcel4 = new Parcel();
        parcel4.setToken(987);
        parcel4.setClientName("Bob Wilson");
        parcel4.setClientEmail("bob@example.com");
        parcel4.setClientPhone(654789123);
        parcel4.setClientMobilePhone(987321654);
        parcel4.setStatus(ParcelStatus.COLLECTED);
        parcel4.setCollectionPoint(cp1);
        
        parcels.add(parcel1);
        parcels.add(parcel2);
        parcels.add(parcel3);
        parcels.add(parcel4);

            
        Partner partner1 = new Partner();
        partner1.setPassword("pass123");
        partner1.setUsername("username1");
        partner1.setCollectionPoint(cp1);
    
        cp1.setPartner(partner1);

        partnerRepository.save(partner1);
        collectionPointRepository.save(cp1);
        parcelRepository.save(parcel1);
        parcelRepository.save(parcel2);
        parcelRepository.save(parcel3);
        parcelRepository.save(parcel4);

    }
    
    
    @Test
    void testGetAcpPages() throws Exception {
        List<CollectionPointDDto> cps = adminService.getCollectionPointsDDto(true);
        mockMvc.perform(get("/admin/acp-pages"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-dashboard"))
            .andExpect(model().attributeExists("cps"))
            .andExpect(model().attribute("cps", hasSize(cps.size())))
            .andExpect(model().attribute("cps", hasItem(hasProperty("id", equalTo(cps.get(0).getId())))))
            .andReturn();
    }
    
    @Test
    void testDeleteACP() throws Exception {
        mockMvc.perform(get("/admin/acp-pages/" + cp1.getId() +"/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/acp-pages"));
        
        assertEquals(collectionPointRepository.findById(cp1.getId()), Optional.empty());
    }

    @Test
    void testGetAcpPagesById() throws Exception {
        mockMvc.perform(get("/admin/acp-pages/" + cp1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("acp-info"))
                .andExpect(model().attributeExists("cp"))
                .andExpect(model().attributeExists("chartData"))
                .andExpect(model().attributeExists("parcels"))
                .andExpect(result -> {
                    Object cpObject = result.getModelAndView().getModel().get("cp");
                    assertEquals(cp1.getId(), ((CollectionPoint) cpObject).getId());
                });;
    }

    @Test
    void testDeleteParcel() throws Exception {
        Integer idACP = cp1.getId();
        Integer idParcel = parcels.get(0).getId();

        mockMvc.perform(get("/admin/acp-pages/{idACP}/delete/{idParcel}", idACP, idParcel))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/acp-pages/" + idACP));

    }

        
}