package tqs.project.backend.boundary;

import io.restassured.http.ContentType;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointCreateDto;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.collection_point.CollectionPointUpdateDto;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "application-integrationtest.properties")
class CollectionPointRestControllerTestIT {

    private CollectionPoint acp, acp2;
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

        CollectionPoint collectionPoint2 = new CollectionPoint();
        collectionPoint2.setId(2);
        collectionPoint2.setName("Collection Point 2");
        collectionPoint2.setType("Collection Point");
        collectionPoint2.setCapacity(100);
        collectionPoint2.setAddress("Rua do Prof António");
        collectionPoint2.setLatitude(41.174660);
        collectionPoint2.setLongitude(-8.588069);
        collectionPoint2.setOwnerName("João");
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
        acp2 = collectionPointRepository.save(collectionPoint2);
        storeRepository.save(store);
        p = parcelRepository.save(parcel);
        p1 = parcelRepository.save(parcel2);
        p2 = parcelRepository.save(parcel3);
        p3 = parcelRepository.save(parcel4);
    }

    @Test
    void getAllCollectionPoints() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp","all")
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .get(endpoint)
                .then()
                    .statusCode(200)
                    .body("size()", is(2))
                    .body("[0].id", is(acp.getId()))
                    .body("[0].name", is("Collection Point 1"))
                    .body("[1].id", is(acp2.getId()))
                    .body("[1].name", is("Collection Point 2"));
    }

    @Test
    void getCollectionPointsByLocation() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp","all")
                .queryParam("zip", "4435-677")
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .get(endpoint)
                .then()
                    .statusCode(200)
                    .body("size()", is(2))
                    .body("[0].id", is(acp2.getId()))
                    .body("[0].name", is("Collection Point 2"))
                    .body("[1].id", is(acp.getId()))
                    .body("[1].name", is("Collection Point 1"));
    }

    @Test
    void createCollectionPoint() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp","add")
                .build()
                .toUriString();

        CollectionPointCreateDto cp = new CollectionPointCreateDto();
        cp.setName("Collection Point 3");
        cp.setType("Collection Point");
        cp.setCapacity(100);
        cp.setAddress("Rua do Prof António");
        cp.setOwnerName("João");
        cp.setOwnerEmail("j@ua.pt");
        cp.setOwnerGender("M");
        cp.setOwnerPhone(123456789);
        cp.setOwnerMobilePhone(987654321);
        cp.setZipcode("4435-677");
        Partner p = new Partner();
        p.setUsername("partner");
        p.setPassword("password");
        cp.setPartner(p);

        RestAssured.given()
                .when()
                    .contentType(ContentType.JSON)
                    .body(cp)
                    .post(endpoint)
                .then()
                    .statusCode(200)
                    .body("name", is("Collection Point 3"));
    }

    @Test
    void updateCollectionPoint() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", acp.getId().toString())
                .build()
                .toUriString();

        CollectionPointUpdateDto cp = new CollectionPointUpdateDto();
        cp.setName("Collection Point 3");
        cp.setType("Collection Point");
        cp.setCapacity(100);
        cp.setOwnerPhone(965833174);
        cp.setOwnerMobilePhone(965833174);
        cp.setStatus(true);

        RestAssured.given()
                .when()
                    .contentType(ContentType.JSON)
                    .body(cp)
                    .put(endpoint)
                .then()
                    .statusCode(200)
                    .body("name", is("Collection Point 3"));
    }

    @Test
    void updateCollectionPointNotFound() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", "327846")
                .build()
                .toUriString();

        CollectionPointUpdateDto cp = new CollectionPointUpdateDto();
        cp.setName("Collection Point 3");
        cp.setType("Collection Point");
        cp.setCapacity(100);
        cp.setOwnerPhone(965833174);
        cp.setOwnerMobilePhone(965833174);
        cp.setStatus(true);

        RestAssured.given()
                .when()
                    .contentType(ContentType.JSON)
                    .body(cp)
                    .put(endpoint)
                .then()
                    .statusCode(404);
    }

    @Test
    void deleteCollectionPoint() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", acp.getId().toString())
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .delete(endpoint)
                .then()
                    .statusCode(200);
    }

    @Test
    void deleteCollectionPointNotFound() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", "327846")
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .delete(endpoint)
                .then()
                    .statusCode(404);
    }

    @Test
    void getAllParcels(){

        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp",acp.getId().toString())
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
    void getParcel_ifExistsInCollectionPoint() {
        System.out.println(p.getId());

        System.out.println(acp.getParcels().get(0).getId());

        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp","parcel",p.getId().toString())
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
    void checkin_ifParcelExistsInCollectionPoint() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp", "parcel","checkin",p.getId().toString())
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
    void checkin_ifNotInTransit(){
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp", "parcel","checkin",p1.getId().toString())
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                    .post(endpoint)
                .then()
                    .statusCode(400);
    }

    @Test
    void checkout_ifTokenIsIncorrect() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp", "parcel","checkout",p1.getId().toString())
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
    void checkout_ifParcelExistsInCollectionPoint() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api","acp", "parcel","checkout",p1.getId().toString())
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
    void checkout_ifNotDelivered() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", "parcel","checkout",p.getId().toString())
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
    void returnParcel_ifExistsInCollectionPoint() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", "parcel", "return", p2.getId().toString())
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
    void returnParcel_ifNotCollected() {
        String endpoint = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(localPort)
                .pathSegment("api", "acp", "parcel", "return", p.getId().toString())
                .build()
                .toUriString();

        RestAssured.given()
                .when()
                .post(endpoint)
                .then()
                .statusCode(400);
    }

}