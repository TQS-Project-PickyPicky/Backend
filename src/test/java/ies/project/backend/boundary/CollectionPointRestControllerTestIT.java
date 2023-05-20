package ies.project.backend.boundary;

import ies.project.backend.data.collection_point.CollectionPoint;
import ies.project.backend.data.collection_point.CollectionPointRepository;
import ies.project.backend.data.parcel.Parcel;
import ies.project.backend.data.parcel.ParcelRepository;
import ies.project.backend.data.parcel.ParcelStatus;
import ies.project.backend.data.store.Store;
import ies.project.backend.data.store.StoreRepository;
import ies.project.backend.data.store.StoreStatus;
import io.restassured.RestAssured;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "application-integrationtest.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CollectionPointRestControllerTestIT {

    private CollectionPoint acp;
    private Parcel p, p1, p2, p3;

    @LocalServerPort
    int localPort;

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
        store.setStatus(StoreStatus.ACCEPTED);

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
    @Order(1)
    void getAllParcels(){

        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp")
                .queryParam("id",acp.getId())
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .get(endpoint)
                .then()
                    .statusCode(200)
                    .body("size()", is(3))
                    .body("[0].id", is(p.getId()))
                    .body("[0].status", is("IN_TRANSIT"))
                    .body("[1].id", is(p1.getId()))
                    .body("[1].status", is("DELIVERED"))
                    .body("[2].id", is(p2.getId()))
                    .body("[2].status", is("COLLECTED"));
    }

    @Test
    @Order(2)
    void getParcel_ifExistsInCollectionPoint() {
        System.out.println(p.getId());

        System.out.println(acp.getParcels().get(0).getId());

        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp","parcel")
                .queryParam("id",p.getId())
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .get(endpoint)
                .then()
                    .statusCode(200)
                    .body("id", is(p.getId()))
                    .body("status", is("IN_TRANSIT"))
                    .body("eta", is(5));
    }

    //@Test
    //@Order()
    //void getParcel_ifNotExistsInCollectionPoint() {
    //    String endpoint = UriComponentsBuilder.newInstance()
    //            .scheme("http")
    //            .host("localhost")
    //            .port(localPort)
    //            .pathSegment("api","acp","parcel")
    //            .queryParam("id",p3.getId())
    //            .build()
    //            .toUriString();
//
    //    RestAssured.given()
    //            .when()
    //                .get(endpoint)
    //            .then()
    //                .statusCode(400);
    //}

    @Test
    @Order(3)
    void checkin_ifParcelExistsInCollectionPoint() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp", "parcel","checkin")
                .queryParam("id",p.getId())
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .post(endpoint)
                .then()
                    .statusCode(200)
                    .body("id", is(p.getId()))
                    .body("status", is("DELIVERED"));
    }

    //@Test
    //@Order(5)
    //void checkin_ifParcelNotExistsInCollectionPoint() {
    //    String endpoint = UriComponentsBuilder.newInstance()
    //            .scheme("http")
    //            .host("localhost")
    //            .port(localPort)
    //            .pathSegment("api","acp", "parcel","checkin")
    //            .queryParam("id",5)
    //            .build()
    //            .toUriString();
//
    //    RestAssured.given()
    //            .when()
    //                .post(endpoint)
    //            .then()
    //                .statusCode(400);
    //}

    @Test
    @Order(4)
    void checkin_ifNotInTransit(){
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp", "parcel","checkin")
                .queryParam("id",p1.getId())
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .post(endpoint)
                .then()
                    .statusCode(400);
    }

    @Test
    @Order(5)
    void checkout_ifTokenIsIncorrect() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp", "parcel","checkout")
                .queryParam("id",p1.getId())
                .queryParam("token",1234567)
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .post(endpoint)
                .then()
                    .statusCode(400);
    }

    @Test
    @Order(6)
    void checkout_ifParcelExistsInCollectionPoint() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp", "parcel","checkout")
                .queryParam("id",p1.getId())
                .queryParam("token",123456)
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .post(endpoint)
                .then()
                    .statusCode(200)
                    .body("id", is(p1.getId()))
                    .body("status", is("COLLECTED"));
    }

    //@Test
    //@Order(9)
    //void checkout_ifParcelNotExistsInCollectionPoint() {
    //    String endpoint = UriComponentsBuilder.newInstance()
    //            .scheme("http")
    //            .host("localhost")
    //            .port(localPort)
    //            .pathSegment("api","acp", "parcel","checkout")
    //            .queryParam("id",5)
    //            .queryParam("token",123456)
    //            .build()
    //            .toUriString();
//
    //    RestAssured.given()
    //            .when()
    //                .post(endpoint)
    //            .then()
    //                .statusCode(400);
    //}

    @Test
    @Order(7)
    void checkout_ifNotDelivered() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", "parcel","checkout")
                .queryParam("id", p.getId())
                .queryParam("token",123456)
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                .post(endpoint)
                .then()
                .statusCode(400);
    }

    @Test
    @Order(8)
    void returnParcel_ifExistsInCollectionPoint() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", "parcel", "return")
                .queryParam("id", p2.getId())
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                .post(endpoint)
                .then()
                .statusCode(200)
                .body("id", is(p2.getId()))
                .body("status", is("RETURNED"));
    }

    //@Test
    //@Order(12)
    //void returnParcel_ifNotExistsInCollectionPoint() {
    //    String endpoint = UriComponentsBuilder.newInstance()
    //            .scheme("http")
    //            .host("localhost")
    //            .port(localPort)
    //            .pathSegment("api", "acp", "parcel","return")
    //            .queryParam("id", 5)
    //            .build()
    //            .toUriString();
//
    //    RestAssured.given()
    //            .when()
    //            .post(endpoint)
    //            .then()
    //            .statusCode(400);
    //}

    @Test
    @Order(9)
    void returnParcel_ifNotCollected() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", "parcel", "return")
                .queryParam("id", p.getId())
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                .post(endpoint)
                .then()
                .statusCode(400);
    }

}