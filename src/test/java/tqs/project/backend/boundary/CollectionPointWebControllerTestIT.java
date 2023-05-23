package tqs.project.backend.boundary;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.junit.runner.RunWith;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreRepository;
import tqs.project.backend.data.utils.ResolveLocation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;




@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "application-integrationtest.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class CollectionPointWebControllerTestIT {

    private CollectionPoint acp;
    private Parcel p, p1, p2, p3;

    @LocalServerPort
    int localPort;

    @Autowired
    private MockMvc mvc;
  
    @Autowired
    private CollectionPointRepository collectionPointRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private StoreRepository storeRepository;
  
    @BeforeEach
    void setUp() {
        // Empty database
        collectionPointRepository.deleteAll();
        storeRepository.deleteAll();
        parcelRepository.deleteAll();

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
        parcel2.setToken(123456);
        parcel2.setClientName("Jorge");
        parcel2.setClientEmail("jorge@ua.pt");
        parcel2.setClientPhone(123456789);
        parcel2.setClientMobilePhone(987654321);
        parcel2.setExpectedArrival(LocalDate.now().plusDays(7));
        parcel2.setStore(store);
        parcel2.setStatus(ParcelStatus.DELIVERED);
        parcel2.setCollectionPoint(collectionPoint);

        Parcel parcel3 = new Parcel();
        parcel3.setToken(123456);
        parcel3.setClientName("Gabriel");
        parcel3.setClientEmail("gabriel@ua.pt");
        parcel3.setClientPhone(123456789);
        parcel3.setClientMobilePhone(987654321);
        parcel3.setExpectedArrival(LocalDate.now());
        parcel3.setStore(store);
        parcel3.setStatus(ParcelStatus.COLLECTED);
        parcel3.setCollectionPoint(collectionPoint);

        Parcel parcel4 = new Parcel();

        List<Parcel> parcels = new ArrayList<>();
        parcels.add(parcel);
        parcels.add(parcel2);
        parcels.add(parcel3);

        collectionPoint.setParcels(parcels);

        // Save to database
        acp = collectionPointRepository.save(collectionPoint);
        storeRepository.save(store);
        p = parcelRepository.save(parcel);
        p1 = parcelRepository.save(parcel2);
        p2 = parcelRepository.save(parcel3);
        p3 = parcelRepository.save(parcel4);
    }

    @Test
    public void createCollectionPoint_ifCorrectQuery() throws Exception{

        ArrayList<Double> coordinates = ResolveLocation.resolveAddress("3810-193", "Aveiro");

        MvcResult result = mvc.perform(post("/registerACP")
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
            .andExpect(status().isOk())
            .andExpect(view().name("home-picky"))
            .andExpect(model().attributeExists("cp"))
            .andExpect(model().attributeExists("showModal"))
            .andExpect(model().attributeDoesNotExist("error"))
            .andExpect(model().attributeDoesNotExist("errorCoordinates"))
            .andReturn();;

        CollectionPoint createdEntity = (CollectionPoint) result.getModelAndView().getModel().get("cp");


        assertNotNull(createdEntity);
        assertEquals("cp1", createdEntity.getName());
        assertEquals("Library", createdEntity.getType());
        assertEquals("100", createdEntity.getCapacity().toString());
        assertEquals("João", createdEntity.getOwnerName());
        assertEquals("joao@ua.pt", createdEntity.getOwnerEmail());
        assertEquals("910000000", createdEntity.getOwnerPhone().toString());
        assertEquals("pass1", createdEntity.getPartner().getPassword());
        assertEquals("username1", createdEntity.getPartner().getUsername());
        assertEquals(coordinates.get(0), createdEntity.getLatitude());
        assertEquals(coordinates.get(1), createdEntity.getLongitude());
        assertEquals("Rua do ISEP", createdEntity.getAddress());

    }

    @Test
    public void noCreateCollectionPoint_ifIncorrectQuery() throws Exception{

        MvcResult result = mvc.perform(post("/registerACP")
            .param("name", "cp1")
            .param("type", "Library")
            .param("capacity", "100")              
            .param("ownerName", "João")
            .param("ownerEmail", "joao@ua.pt")
            .param("passwordCheck", "pass1")
            .param("ownerPhone", "")
            .param("zipcode", "3810-193")
            .param("city", "Aveiro")
            .param("address", "Rua do ISEP")
            .param("partner.username", "username1")
            .param("partner.password", "pass1"))
            .andExpect(status().isOk())
            .andExpect(view().name("acp-application")) //goes back to the application
            .andExpect(model().attributeExists("cp"))
            .andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.cp");
            
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        assertNotNull(fieldErrors);

    }

    @Test
    public void noCreateCollectionPoint_ifPasswordsDifferent() throws Exception{

        mvc.perform(post("/registerACP")
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
            .param("partner.password", "pass2")) //passwords are different
            .andExpect(status().isOk())
            .andExpect(view().name("acp-application")) //goes back to the application
            .andExpect(model().attributeExists("cp"))
            .andExpect(model().attributeExists("error"));
    }


    @Test
    public void noCreateCollectionPoint_ifNoCoordinatesFound() throws Exception{
        mvc.perform(post("/registerACP")
            .param("name", "cp1")
            .param("type", "Library")
            .param("capacity", "100")              
            .param("ownerName", "João")
            .param("ownerEmail", "joao@ua.pt")
            .param("ownerPhone", "910000000")
            .param("passwordCheck", "pass1")
            .param("zipcode", "")           //no zipcode and city = no found searches on api
            .param("city", "")
            .param("address", "Rua do ISEP")
            .param("partner.username", "username1")
            .param("partner.password", "pass1")) 
            .andExpect(status().isOk())
            .andExpect(view().name("acp-application")) //goes back to the application
            .andExpect(model().attributeExists("cp"))
            .andExpect(model().attributeExists("errorCoordinates"));
    }
    

    @Test
    void getAllParcels() throws Exception {
        String url = "/acp?id=" + acp.getId();
        mvc.perform(get(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("acp"))
                .andExpect(model().attributeExists("parcels"));
    }

    @Test
    void getParcel_ifExistsInCollectionPoint() throws Exception {
        String url = "/acp/parcel?id=" + p.getId();
        mvc.perform(get(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("parcelib"))
                .andExpect(model().attributeExists("parcel"))
                .andExpect(model().attributeExists("collectionPointService"));
    }

    //@Test
    //void getParcel_ifNotExistsInCollectionPoint() throws Exception {
    //    mvc.perform(get("/acp/parcel?id=2").contentType(MediaType.TEXT_HTML))
    //            .andExpect(status().is3xxRedirection())
    //            .andExpect(redirectedUrl("/acp"));
    //}

    @Test
    void checkIn_ifExistsInCollectionPoint() throws Exception {
        String url = "/acp/parcel/checkin?id=" + p.getId();
        mvc.perform(post(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp"));
    }

    //@Test
    //void checkIn_ifNotExistsInCollectionPoint() throws Exception {
    //    mvc.perform(post("/acp/parcel/checkin?id=2").contentType(MediaType.TEXT_HTML))
    //            .andExpect(status().is3xxRedirection())
    //            .andExpect(redirectedUrl("/acp/parcel?id=2"));
    //}

    @Test
    void checkIn_ifNotInTransit() throws Exception {
        String url = "/acp/parcel/checkin?id=" + p1.getId();
        String url2 = "/acp/parcel?id=" + p1.getId();
        mvc.perform(post(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(url2));
    }

    @Test
    void checkOut_ifExistsInCollectionPoint() throws Exception {
        String url = "/acp/parcel/checkout?id=" + p1.getId() + "&token=" + p.getToken();
        mvc.perform(post(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp"));
    }

    //@Test
    //void checkOut_ifNotExistsInCollectionPoint() throws Exception {
    //    mvc.perform(post("/acp/parcel/checkout?id=2&token=5").contentType(MediaType.TEXT_HTML))
    //            .andExpect(status().is3xxRedirection())
    //            .andExpect(redirectedUrl("/acp/parcel?id=2"));
    //}

    @Test
    void checkOut_ifNotDelivered() throws Exception {
        String url = "/acp/parcel/checkout?id=" + p2.getId() + "&token=" + p2.getToken();
        String url2 = "/acp/parcel?id=" + p2.getId();
        mvc.perform(post(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(url2));
    }

    @Test
    void checkOut_ifTokenIsIncorrect() throws Exception {
        String url = "/acp/parcel/checkout?id=" + p1.getId() + "&token=6";
        String url2 = "/acp/parcel?id=" + p1.getId();
        mvc.perform(post(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(url2));
    }

    @Test
    void returnParcel_ifExistsInCollectionPoint() throws Exception {
        String url = "/acp/parcel/return?id=" + p2.getId();
        mvc.perform(post(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp"));
    }

    //@Test
    //void returnParcel_ifNotExistsInCollectionPoint() throws Exception {
    //    mvc.perform(post("/acp/parcel/return?id=2").contentType(MediaType.TEXT_HTML))
    //            .andExpect(status().is3xxRedirection())
    //            .andExpect(redirectedUrl("/acp/parcel?id=2"));
    //}

    @Test
    void returnParcel_ifNotCollected() throws Exception {
        String url = "/acp/parcel/return?id=" + p.getId();
        String url2 = "/acp/parcel?id=" + p.getId();
        mvc.perform(post(url).contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(url2));
    }

}
