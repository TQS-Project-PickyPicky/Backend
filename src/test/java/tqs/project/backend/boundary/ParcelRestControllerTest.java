package tqs.project.backend.boundary;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.service.ParcelService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(ParcelRestController.class)
class ParcelRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ParcelService parcelService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mvc);
        // Create entities for mock database
        Store store = new Store(1, "Mock Store", new ArrayList<>());

        CollectionPoint collectionPoint = new CollectionPoint(1, "Mock Collection Point", "Mock Type", 100, "Mock Address", 40.6331731, -8.6594933, "Mock Owner Name", "Mock Owner Email", "Mock Owner Gender", 111000111, 111000222, true, null, new ArrayList<>());

        Parcel parcel1 = new Parcel(1, 111111, "Anna", "anna@mail.com", 111000111, 111000222, LocalDate.of(2023, 5, 19), ParcelStatus.DELIVERED, store, collectionPoint);
        Parcel parcel2 = new Parcel(2, 222222, "Bob", "bob@mail.com", 222000111, 222000222, LocalDate.of(2023, 5, 19), ParcelStatus.DELIVERED, store, collectionPoint); // Will be used to test general exceptions

        List<Parcel> allParcels = List.of(parcel1, parcel2);

        // Create expectations
        when(parcelService.getParcel(1))
                .thenReturn(parcel1);
        when(parcelService.getParcel(2))
                .thenThrow(new RuntimeException());
        when(parcelService.getParcel(3))
                .thenThrow(new ParcelNotFoundException(3));
        when(parcelService.getAllParcels())
                .thenReturn(allParcels);
        when(parcelService.createParcel(any(String.class), any(String.class), any(Integer.class), any(Integer.class), eq(1), eq(1)))
                .thenAnswer((Answer<Parcel>) invocation -> new Parcel(3, 333333, invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), LocalDate.now().plusDays(7), ParcelStatus.PLACED, store, collectionPoint));
        when(parcelService.updateParcel(eq(1), any(Parcel.class), eq(null)))
                .thenAnswer((Answer<Parcel>) invocation -> new Parcel(1, 111111, invocation.<Parcel>getArgument(1).getClientName(), invocation.<Parcel>getArgument(1).getClientEmail(), invocation.<Parcel>getArgument(1).getClientPhone(), invocation.<Parcel>getArgument(1).getClientMobilePhone(), invocation.<Parcel>getArgument(1).getExpectedArrival(), invocation.<Parcel>getArgument(1).getStatus(), store, collectionPoint));
        when(parcelService.updateParcel(eq(1), any(Parcel.class), eq(111111)))
                .thenThrow(new InvalidParcelStatusChangeException(ParcelStatus.DELIVERED, ParcelStatus.RETURNED));
        when(parcelService.updateParcel(eq(1), any(Parcel.class), eq(222222)))
                .thenThrow(new IncorrectParcelTokenException(222222, 1));
        when(parcelService.updateParcel(eq(2), any(Parcel.class), eq(null)))
                .thenThrow(new RuntimeException());
        when(parcelService.updateParcel(eq(3), any(Parcel.class), eq(null)))
                .thenThrow(new ParcelNotFoundException(3));
        when(parcelService.deleteParcel(1))
                .thenReturn(parcel1);
        when(parcelService.deleteParcel(2))
                .thenThrow(new RuntimeException());
        when(parcelService.deleteParcel(3))
                .thenThrow(new ParcelNotFoundException(3));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenGetExistingParcel_thenReturn200() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/parcels/1")
            .then()
                .statusCode(200)
                .body("id", is(1))
                .body("token", is(111111))
                .body("clientName", is("Anna"))
                .body("clientEmail", is("anna@mail.com"))
                .body("clientPhone", is(111000111))
                .body("clientMobilePhone", is(111000222))
                .body("expectedArrival", is("2023-05-19"))
                .body("status", is("DELIVERED"))
                .body("storeId", is(1))
                .body("collectionPointId", is(1));
    }

    @Test
    void whenGetProblematicParcel_thenReturn500() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/parcels/2")
            .then()
                .statusCode(500);
    }

    @Test
    void whenGetNonExistingParcel_thenReturn404() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/parcels/3")
            .then()
                .statusCode(404);
    }

    @Test
    void whenGetAllParcels_thenReturn200() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/parcels")
            .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[0].token", is(111111))
                .body("[0].clientName", is("Anna"))
                .body("[0].clientEmail", is("anna@mail.com"))
                .body("[0].clientPhone", is(111000111))
                .body("[0].clientMobilePhone", is(111000222))
                .body("[0].expectedArrival", is("2023-05-19"))
                .body("[0].status", is("DELIVERED"))
                .body("[0].storeId", is(1))
                .body("[0].collectionPointId", is(1))
                .body("[1].id", is(2))
                .body("[1].token", is(222222))
                .body("[1].clientName", is("Bob"))
                .body("[1].clientEmail", is("bob@mail.com"))
                .body("[1].clientPhone", is(222000111))
                .body("[1].clientMobilePhone", is(222000222))
                .body("[1].expectedArrival", is("2023-05-19"))
                .body("[1].status", is("DELIVERED"))
                .body("[1].storeId", is(1))
                .body("[1].collectionPointId", is(1));
    }

    @Test
    void givenNewParcel_whenPostParcel_thenReturn201() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"clientName\": \"Charlie\", \"clientEmail\": \"charlie@mail.com\", " +
                        "\"clientPhone\": 333000111, \"clientMobilePhone\": 333000222, \"storeId\": 1, " +
                        "\"collectionPointId\": 1}")
            .when()
                .post("/api/parcels")
            .then()
                .statusCode(201)
                .body("id", instanceOf(Integer.class))
                .body("token", instanceOf(Integer.class))
                .body("clientName", is("Charlie"))
                .body("clientEmail", is("charlie@mail.com"))
                .body("clientPhone", is(333000111))
                .body("clientMobilePhone", is(333000222))
                .body("expectedArrival", instanceOf(String.class))
                .body("status", is("PLACED"))
                .body("storeId", is(1))
                .body("collectionPointId", is(1));
    }

    @Test
    void givenExistingParcel_whenPutParcel_thenReturn200() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"clientName\": \"Anna 2\", \"clientEmail\": \"anna@mail.com\", " +
                        "\"clientPhone\": 111000111, \"clientMobilePhone\": 111000222, " +
                        "\"expectedArrival\": \"2023-05-19\", \"status\": \"DELIVERED\"}")
            .when()
                .put("/api/parcels/1")
            .then()
                .statusCode(200)
                .body("id", is(1))
                .body("token", is(111111))
                .body("clientName", is("Anna 2"))
                .body("clientEmail", is("anna@mail.com"))
                .body("clientPhone", is(111000111))
                .body("clientMobilePhone", is(111000222))
                .body("expectedArrival", is("2023-05-19"))
                .body("status", is("DELIVERED"))
                .body("storeId", is(1))
                .body("collectionPointId", is(1));
    }

    @Test
    void givenParcelWithStatusX_whenPutParcelToInvalidStatusY_thenReturn400() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"clientName\": \"Anna\", \"clientEmail\": \"anna@mail.com\", " +
                        "\"clientPhone\": 111000111, \"clientMobilePhone\": 111000222, " +
                        "\"expectedArrival\": \"2023-05-19\", \"status\": \"RETURNED\"}")
            .when()
                .put("/api/parcels/1?token=111111")
            .then()
                .statusCode(400);
    }

    @Test
    void givenParcelWithStatusDelivered_whenPutParcelToCollectedWithInvalidToken_thenReturn401() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"clientName\": \"Anna\", \"clientEmail\": \"anna@mail.com\", " +
                        "\"clientPhone\": 111000111, \"clientMobilePhone\": 111000222, " +
                        "\"expectedArrival\": \"2023-05-19\", \"status\": \"COLLECTED\"}")
            .when()
                .put("/api/parcels/1?token=222222")
            .then()
                .statusCode(401);
    }

    @Test
    void givenProblematicParcel_whenPutParcel_thenReturn500() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"clientName\": \"Bob 2\", \"clientEmail\": \"bob@mail.com\", " +
                        "\"clientPhone\": 222000111, \"clientMobilePhone\": 222000222, " +
                        "\"expectedArrival\": \"2023-05-19\", \"status\": \"DELIVERED\"}")
            .when()
                .put("/api/parcels/2")
            .then()
                .statusCode(500);
    }

    @Test
    void givenNonExistingParcel_whenPutParcel_thenReturn404() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"clientName\": \"Charlie 3\", \"clientEmail\": \"charlie@mail.com\", " +
                        "\"clientPhone\": 333000111, \"clientMobilePhone\": 333000222, " +
                        "\"expectedArrival\": \"2023-05-19\", \"status\": \"IN_TRANSIT\"}")
            .when()
                .put("/api/parcels/3")
            .then()
                .statusCode(404);
    }

    @Test
    void whenDeleteExistingParcel_thenReturn200() {
        RestAssuredMockMvc
            .given()
            .when()
                .delete("/api/parcels/1")
            .then()
                .statusCode(200)
                .body("id", is(1))
                .body("token", is(111111))
                .body("clientName", is("Anna"))
                .body("clientEmail", is("anna@mail.com"))
                .body("clientPhone", is(111000111))
                .body("clientMobilePhone", is(111000222))
                .body("expectedArrival", is("2023-05-19"))
                .body("status", is("DELIVERED"))
                .body("storeId", is(1))
                .body("collectionPointId", is(1));
    }

    @Test
    void whenDeleteProblematicParcel_thenReturn500() {
        RestAssuredMockMvc
            .given()
            .when()
                .delete("/api/parcels/2")
            .then()
                .statusCode(500);
    }

    @Test
    void whenDeleteNonExistingParcel_thenReturn404() {
        RestAssuredMockMvc
            .given()
            .when()
                .delete("/api/parcels/3")
            .then()
                .statusCode(404);
    }
}