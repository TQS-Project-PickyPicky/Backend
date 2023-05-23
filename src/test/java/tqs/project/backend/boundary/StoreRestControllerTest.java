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
import tqs.project.backend.exception.StoreNotFoundException;
import tqs.project.backend.service.StoreService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(StoreRestController.class)
class StoreRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mvc);
        // Create entities for mock database
        Store store1 = new Store(1, "Mock Store 1", new ArrayList<>());
        Store store2 = new Store(2, "Mock Store 2", new ArrayList<>()); // Will be used to test general exceptions

        List<Store> allStores = List.of(store1, store2);

        CollectionPoint collectionPoint = new CollectionPoint(1, "Mock Collection Point", "Mock Type", 100, "Mock Address", 40.6331731, -8.6594933, "Mock Owner Name", "Mock Owner Email", "Mock Owner Gender", 111000111, 111000222, true, null, new ArrayList<>());

        Parcel parcel1 = new Parcel(1, 111111, "Anna", "anna@mail.com", 111000111, 111000222, LocalDate.of(2023, 5, 19), ParcelStatus.PLACED, store1, collectionPoint);
        Parcel parcel2 = new Parcel(2, 222222, "Bob", "bob@mail.com", 222000111, 222000222, LocalDate.of(2023, 5, 19), ParcelStatus.DELIVERED, store1, collectionPoint);

        List<Parcel> allParcels = List.of(parcel1, parcel2);

        store1.setParcels(allParcels);

        collectionPoint.setParcels(allParcels);

        // Create expectations
        when(storeService.getStore(1))
                .thenReturn(store1);
        when(storeService.getStore(2))
                .thenThrow(new RuntimeException());
        when(storeService.getStore(3))
                .thenThrow(new StoreNotFoundException(3));
        when(storeService.getAllStores())
                .thenReturn(allStores);
        when(storeService.createStore(any(String.class)))
                .thenAnswer((Answer<Store>) invocation -> new Store(3, invocation.getArgument(0), new ArrayList<>()));
        when(storeService.updateStore(eq(1), any(Store.class)))
                .thenAnswer((Answer<Store>) invocation -> new Store(1, invocation.<Store>getArgument(1).getName(), store1.getParcels()));
        when(storeService.updateStore(eq(2), any(Store.class)))
                .thenThrow(new RuntimeException());
        when(storeService.updateStore(eq(3), any(Store.class)))
                .thenThrow(new StoreNotFoundException(3));
        when(storeService.deleteStore(1))
                .thenReturn(store1);
        when(storeService.deleteStore(2))
                .thenThrow(new RuntimeException());
        when(storeService.deleteStore(3))
                .thenThrow(new StoreNotFoundException(3));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenGetExistingStore_thenReturn200() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/stores/1")
            .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Mock Store 1"))
                .body("parcelsId.size()", is(2))
                .body("parcelsId[0]", is(1))
                .body("parcelsId[1]", is(2));
    }

    @Test
    void whenGetProblematicStore_thenReturn500() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/stores/2")
            .then()
                .statusCode(500);
    }

    @Test
    void whenGetNonExistingStore_thenReturn404() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/stores/3")
            .then()
                .statusCode(404);
    }

    @Test
    void whenGetAllStores_thenReturn200() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/stores")
            .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[0].name", is("Mock Store 1"))
                .body("[0].parcelsId.size()", is(2))
                .body("[0].parcelsId[0]", is(1))
                .body("[0].parcelsId[1]", is(2))
                .body("[1].id", is(2))
                .body("[1].name", is("Mock Store 2"))
                .body("[1].parcelsId.size()", is(0));
    }

    @Test
    void givenNewStore_whenPostStore_thenReturn201() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"name\": \"Mock Store 3\"}")
            .when()
                .post("/api/stores")
            .then()
                .statusCode(201)
                .body("id", is(3))
                .body("name", is("Mock Store 3"))
                .body("parcelsId.size()", is(0));
    }

    @Test
    void givenExistingStore_whenPutStore_thenReturn200() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"name\": \"Mock Store 1.1\"}")
            .when()
                .put("/api/stores/1")
            .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Mock Store 1.1"))
                .body("parcelsId.size()", is(2))
                .body("parcelsId[0]", is(1))
                .body("parcelsId[1]", is(2));
    }

    @Test
    void givenProblematicStore_whenPutStore_thenReturn500() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"name\": \"Mock Store 2.1\"}")
            .when()
                .put("/api/stores/2")
            .then()
                .statusCode(500);
    }

    @Test
    void givenNonExistingStore_whenPutStore_thenReturn404() {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
                .body("{\"name\": \"Mock Store 3.1\"}")
            .when()
                .put("/api/stores/3")
            .then()
                .statusCode(404);
    }

    @Test
    void whenDeleteExistingStore_thenReturn200() {
        RestAssuredMockMvc
            .given()
            .when()
                .delete("/api/stores/1")
            .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Mock Store 1"))
                .body("parcelsId.size()", is(2))
                .body("parcelsId[0]", is(1))
                .body("parcelsId[1]", is(2));
    }

    @Test
    void whenDeleteProblematicStore_thenReturn500() {
        RestAssuredMockMvc
            .given()
            .when()
                .delete("/api/stores/2")
            .then()
                .statusCode(500);
    }

    @Test
    void whenDeleteNonExistingStore_thenReturn404() {
        RestAssuredMockMvc
            .given()
            .when()
                .delete("/api/stores/3")
            .then()
                .statusCode(404);
    }

    @Test
    void whenGetAllExistingStoreParcels_thenReturn200() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/stores/1/parcels")
            .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[1].id", is(2));
    }

    @Test
    void whenGetAllProblematicStoreParcels_thenReturn500() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/stores/2/parcels")
            .then()
                .statusCode(500);
    }

    @Test
    void whenGetAllNonExistingStoreParcels_thenReturn404() {
        RestAssuredMockMvc
            .given()
            .when()
                .get("/api/stores/3/parcels")
            .then()
                .statusCode(404);
    }
}