package tqs.project.backend.boundary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import io.restassured.RestAssured;
import tqs.project.backend.data.admin.Admin;
import tqs.project.backend.data.admin.AdminRepository;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;

import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "application-integrationtest.properties")
public class AdminRestControllerTestIT {

    public CollectionPoint cp1, cp2;
    public Parcel p, p1, p2, p3;
    public Admin admin;
    public Partner partner;

    @LocalServerPort
    int localPort;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private CollectionPointRepository collectionPointRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private AdminRepository adminRepository;

    
    @BeforeEach
    void setUp(){
        collectionPointRepository.deleteAll();
        adminRepository.deleteAll();
        partnerRepository.deleteAll();
        parcelRepository.deleteAll();

        cp1 = new CollectionPoint();
        cp1.setName("Collection Point 1");
        cp1.setType("Collection Point");
        cp1.setCapacity(100);
        cp1.setAddress("Rua do ISEP");
        cp1.setLatitude(41.178);
        cp1.setLongitude(-8.608);
        cp1.setOwnerName("João");
        cp1.setOwnerEmail("joao@ua.pt");
        cp1.setOwnerGender("M");
        cp1.setOwnerPhone(123456789);
        cp1.setOwnerMobilePhone(987654321);
        cp1.setStatus(true);

        cp2 = new CollectionPoint();
        cp2.setId(2);
        cp2.setName("Collection Point 2");
        cp2.setType("Collection Point");
        cp2.setCapacity(100);
        cp2.setAddress("Rua do Prof António");
        cp2.setLatitude(41.174660);
        cp2.setLongitude(-8.588069);
        cp2.setOwnerName("João");
        cp2.setOwnerEmail("joao@ua.pt");
        cp2.setOwnerGender("M");
        cp2.setOwnerPhone(123456789);
        cp2.setOwnerMobilePhone(987654321);
        cp2.setStatus(false);


        Parcel parcel = new Parcel();
        parcel.setToken(123456);
        parcel.setClientName("João");
        parcel.setClientEmail("joao@ua.pt");
        parcel.setClientPhone(123456789);
        parcel.setClientMobilePhone(987654321);
        parcel.setExpectedArrival(LocalDate.now().plusDays(5));
        parcel.setStatus(ParcelStatus.IN_TRANSIT);
        parcel.setCollectionPoint(cp1);

        Parcel parcel2 = new Parcel();
        parcel2.setToken(123456);
        parcel2.setClientName("Jorge");
        parcel2.setClientEmail("jorge@ua.pt");
        parcel2.setClientPhone(123456789);
        parcel2.setClientMobilePhone(987654321);
        parcel2.setExpectedArrival(LocalDate.now().plusDays(7));
        parcel2.setStatus(ParcelStatus.DELIVERED);
        parcel2.setCollectionPoint(cp1);

        Parcel parcel3 = new Parcel();
        parcel3.setToken(123456);
        parcel3.setClientName("Gabriel");
        parcel3.setClientEmail("gabriel@ua.pt");
        parcel3.setClientPhone(123456789);
        parcel3.setClientMobilePhone(987654321);
        parcel3.setExpectedArrival(LocalDate.now());
        parcel3.setStatus(ParcelStatus.COLLECTED);
        parcel3.setCollectionPoint(cp1);

        Parcel parcel4 = new Parcel();

        List<Parcel> parcels = new ArrayList<>();
        parcels.add(parcel);
        parcels.add(parcel2);
        parcels.add(parcel3);

        cp1.setParcels(parcels);

        Partner partner1 = new Partner();
        partner1.setPassword("pass123");
        partner1.setUsername("username1");
        partner1.setCollectionPoint(cp1);

        Partner partner2 = new Partner();
        partner2.setPassword("pass123");
        partner2.setUsername("username1");
        partner2.setCollectionPoint(cp2);
    
        cp1.setPartner(partner1);
        cp2.setPartner(partner2);

        cp1 = collectionPointRepository.save(cp1);
        cp2 = collectionPointRepository.save(cp2);

        partnerRepository.save(partner1);
        partnerRepository.save(partner2);

        p = parcelRepository.save(parcel);
        p1 = parcelRepository.save(parcel2);
        p2 = parcelRepository.save(parcel3);
        p3 = parcelRepository.save(parcel4);
    }
    

    @Test
    void testGetListAcpsRegistered() throws Exception {

        String endpoint = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(localPort)
            .pathSegment("api","admin", "getListACPs", "accp")
            .build()
            .toUriString();

        RestAssured.given()
            .when()
                .get(endpoint)
            .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(cp1.getId()));
    }

    @Test
    void testDeleteACP() throws Exception {

        String endpoint = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(localPort)
            .pathSegment("api","admin", "deleteACP", cp1.getId().toString())
            .build()
            .toUriString();

        RestAssured.given()
            .when()
                .delete(endpoint)
            .then()
                .statusCode(200);

    }

    @Test
    void detailsACP() throws Exception {

        String endpoint = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(localPort)
            .pathSegment("api","admin", "acp", "details", cp1.getId().toString())
            .build()
            .toUriString();

        RestAssured.given()
            .when()
                .get(endpoint)
            .then()
                .statusCode(200);

    }

    @Test
    void deleteParcel() throws Exception {

        String endpoint = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(localPort)
            .pathSegment("api","admin", "acp", "delete", cp1.getParcels().get(0).getId().toString())
            .build()
            .toUriString();

        RestAssured.given()
            .when()
                .delete(endpoint)
            .then()
                .statusCode(200);
    }

    @Test
    void acceptApplication() throws Exception {

        String endpoint = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(localPort)
            .pathSegment("api","admin", "acp", cp1.getId().toString(), "application", "accept")
            .build()
            .toUriString();

        RestAssured.given()
            .when()
                .put(endpoint)
            .then()
                .statusCode(200);

    }

    @Test
    void refuseApplication() throws Exception {

        String endpoint = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(localPort)
            .pathSegment("api","admin", "acp", cp1.getId().toString(), "application", "refuse")
            .build()
            .toUriString();

        RestAssured.given()
            .when()
                .delete(endpoint)
            .then()
                .statusCode(200);

    }

    @Test
    void getApplications() throws Exception {

        String endpoint = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(localPort)
            .pathSegment("api","admin", "getListACPs", "naccp")
            .build()
            .toUriString();

        RestAssured.given()
            .when()
                .get(endpoint)
            .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(cp2.getId()));

    }

    @Test
    void getACPInfo() throws Exception {

        String endpoint = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(localPort)
            .pathSegment("api","admin", "acp", "info", cp2.getId().toString())
            .build()
            .toUriString();

        RestAssured.given()
            .when()
                .get(endpoint)
            .then()
                .statusCode(200);
    }
}
