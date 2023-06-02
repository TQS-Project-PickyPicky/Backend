package tqs.project.backend.boundary;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.store.StoreRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "application-integrationtest.properties")
@AutoConfigureMockMvc
public class MainWebControllerTestIT {

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
    void getAllForms() throws Exception{

        mockMvc.perform(get("/main/registerACP").contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(view().name("acp-application"))
            .andExpect(model().attributeExists("cp"));
    }

    @Test
    void registerACP_ValidForm_Success() throws Exception {

        mockMvc.perform(post("/main/registerACP")
                .param("name", "cp1")
                .param("type", "Library")
                .param("capacity", "100")              
                .param("ownerName", "João")
                .param("ownerEmail", "joao@ua.pt")
                .param("ownerPhone", "910000000")
                .param("passwordCheck", "pass1")
                .param("zipcode", "3810-193")
                .param("city", "Aveiro")
                .param("address", "Rua do ISEP")
                .param("partner.username", "username1")
                .param("partner.password", "pass1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/main/login"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeDoesNotExist("errorCoordinates"));

    }

    @Test
    void registerACP_InValidZip_Failure() throws Exception {

        mockMvc.perform(post("/main/registerACP")
                .param("name", "cp1")
                .param("type", "Library")
                .param("capacity", "100")              
                .param("ownerName", "João")
                .param("ownerEmail", "joao@ua.pt")
                .param("ownerPhone", "910000000")
                .param("passwordCheck", "pass1")
                .param("zipcode", "0")
                .param("city", "Aveiro")
                .param("address", "Rua do ISEP")
                .param("partner.username", "username1")
                .param("partner.password", "pass1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorCoordinates"))
                .andExpect(model().attributeDoesNotExist("error"));

    }

    @Test
    void registerACP_InvalidForm_ValidationError() throws Exception {
        mockMvc.perform(post("/main/registerACP")
                .param("name", "")
                .param("type", "")
                .param("capacity", "0")
                .param("address", "")
                .param("ownerName", "")
                .param("ownerEmail", "invalid_email")
                .param("ownerPhone", "invalid_phone")
                .param("passwordCheck", "password")
                .param("zipcode", "12345")
                .param("city", "Aveiro"))
                .andExpect(status().isOk())
                .andExpect(view().name("acp-application"))
                .andExpect(model().attributeExists("cp"))
                .andExpect(model().hasErrors());
    }

    @Test
    void registerACP_PasswordMismatch_Error() throws Exception {
        mockMvc.perform(post("/main/registerACP")
                .param("name", "cp1")
                .param("type", "Library")
                .param("capacity", "100")
                .param("address", "Rua do ISEP")
                .param("ownerName", "João")
                .param("ownerEmail", "joao@ua.pt")
                .param("ownerPhone", "910000000")
                .param("passwordCheck", "mismatched_password")
                .param("partner.username", "username1")
                .param("partner.password", "pass1")
                .param("zipcode", "12345")
                .param("city", "Aveiro"))
                .andExpect(status().isOk())
                .andExpect(view().name("acp-application"))
                .andExpect(model().attributeExists("cp"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeDoesNotExist("errorCoordinates"));
    }

    @Test
    void badAuthentication() throws Exception {

        mockMvc.perform(post("/main/login")
                .param("username", "invalid")
                .param("password", "invalid"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("home-picky"));
    }

    @Test
    void adminAuthentication() throws Exception {
        mockMvc.perform(post("/main/login")
                .param("username", "admin")
                .param("password", "admin"))
                .andExpect(redirectedUrl("/admin/acp-pages"));
    }

    @Test
    void partnerAuthentication() throws Exception {

        mockMvc.perform(post("/main/login")
                .param("username", "username1")
                .param("password", "pass123"))
                .andExpect(redirectedUrl("/acp/home"));
    }
}
