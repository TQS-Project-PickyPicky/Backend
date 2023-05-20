package tqs.project.backend.boundary;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.exception.StoreNotFoundException;
import tqs.project.backend.service.StoreService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
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
        Store store2 = new Store(2, "Mock Store 2", new ArrayList<>());

        List<Store> allStores = List.of(store1, store2);

        Parcel parcel1 = new Parcel(1, 111111, "Anna", "anna@mail.com", 111000111, 111000222, LocalDate.of(2023, 5, 19), ParcelStatus.PLACED, store1, null);
        Parcel parcel2 = new Parcel(2, 222222, "Bob", "bob@mail.com", 222000111, 222000222, LocalDate.of(2023, 5, 19), ParcelStatus.DELIVERED, store1, null);

        List<Parcel> allParcels = List.of(parcel1, parcel2);

        store1.setParcels(allParcels);

        // Create expectations
        when(storeService.getStore(1))
                .thenReturn(store1);
        when(storeService.getStore(2))
                .thenReturn(store2);
        when(storeService.getStore(3))
                .thenThrow(new StoreNotFoundException(3));
        when(storeService.getAllStores())
                .thenReturn(allStores);
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
                .body("parcels.size()", is(2))
                .body("parcels[0]", is(1))
                .body("parcels[1]", is(2));
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
                .body("[0].parcels.size()", is(2))
                .body("[0].parcels[0]", is(1))
                .body("[0].parcels[1]", is(2))
                .body("[1].id", is(2))
                .body("[1].name", is("Mock Store 2"))
                .body("[1].parcels.size()", is(0));
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
                .body("id", is(Integer.class))
                .body("name", is("Mock Store 3"))
                .body("parcels.size()", is(0));
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
                .body("parcels.size()", is(2))
                .body("parcels[0]", is(1))
                .body("parcels[1]", is(2));
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
    void givenExistingStore_whenDeleteStore_thenReturn200() {
        RestAssuredMockMvc
            .given()
            .when()
                .delete("/api/stores/1")
            .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Mock Store 1"))
                .body("parcels.size()", is(2))
                .body("parcels[0]", is(1))
                .body("parcels[1]", is(2));
    }

    @Test
    void givenNonExistingStore_whenDeleteStore_thenReturn404() {
        RestAssuredMockMvc
            .given()
            .when()
                .delete("/api/stores/3")
            .then()
                .statusCode(404);
    }
}