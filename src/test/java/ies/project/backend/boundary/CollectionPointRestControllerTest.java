package ies.project.backend.boundary;

import ies.project.backend.data.parcel.ParcelAllDto;
import ies.project.backend.data.parcel.ParcelDto;
import ies.project.backend.data.parcel.ParcelStatus;
import ies.project.backend.service.CollectionPointService;
import ies.project.backend.util.CantAccessParcelException;
import ies.project.backend.util.DifferentStateException;
import ies.project.backend.util.IncorrectParcelTokenException;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(CollectionPointRestController.class)
class CollectionPointRestControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CollectionPointService collectionPointService;

    @BeforeEach
    void setUp() throws CantAccessParcelException, DifferentStateException, IncorrectParcelTokenException {
        RestAssuredMockMvc.mockMvc(mvc);

        // All Parcels
        when(collectionPointService.getallParcels(1)).thenReturn(List.of(
                new ParcelAllDto(1, ParcelStatus.IN_TRANSIT),
                new ParcelAllDto(2, ParcelStatus.DELIVERED)
                ));

        // Parcel
        when(collectionPointService.getParcel(1)).thenReturn(new ParcelDto(1, ParcelStatus.IN_TRANSIT, 5L));
        when(collectionPointService.getParcel(2)).thenThrow(new CantAccessParcelException("Parcel not found"));

        // Checkin
        when(collectionPointService.checkIn(1)).thenReturn(new ParcelAllDto(1, ParcelStatus.DELIVERED));
        when(collectionPointService.checkIn(2)).thenThrow(new CantAccessParcelException("Parcel not found"));
        when(collectionPointService.checkIn(3)).thenThrow(new DifferentStateException("Parcel is not in transit"));

        // Checkout
        when(collectionPointService.checkOut(1, 5)).thenReturn(new ParcelAllDto(1, ParcelStatus.COLLECTED));
        when(collectionPointService.checkOut(2, 5)).thenThrow(new CantAccessParcelException("Parcel not found"));
        when(collectionPointService.checkOut(3, 5)).thenThrow(new DifferentStateException("Parcel is not delivered"));
        when(collectionPointService.checkOut(1, 6)).thenThrow(new DifferentStateException("Parcel token is incorrect"));

        // Return
        when(collectionPointService.returnParcel(1)).thenReturn(new ParcelAllDto(1, ParcelStatus.RETURNED));
        when(collectionPointService.returnParcel(2)).thenThrow(new CantAccessParcelException("Parcel not found"));
        when(collectionPointService.returnParcel(3)).thenThrow(new DifferentStateException("Parcel is not collected"));
    }

    @Test
    void getAllParcels() {
        RestAssuredMockMvc.given()
                .when()
                .get("/api/acp?id=1")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[0].status", is("IN_TRANSIT"))
                .body("[1].id", is(2))
                .body("[1].status", is("DELIVERED"));
    }

    @Test
    void getParcel_ifExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .get("/api/acp/parcel?id=1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", is("IN_TRANSIT"))
                .body("eta", is(5));
    }

    @Test
    void getParcel_ifNotExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .get("/api/acp/parcel?id=2")
                .then()
                .statusCode(400);
    }

    @Test
    void checkin_ifExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkin?id=1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", is("DELIVERED"));
    }

    @Test
    void checkin_ifNotExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkin?id=2")
                .then()
                .statusCode(400);
    }

    @Test
    void checkin_ifNotInTransit() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkin?id=3")
                .then()
                .statusCode(400);
    }

    @Test
    void checkout_ifExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkout?id=1&token=5")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", is("COLLECTED"));
    }

    @Test
    void checkout_ifNotExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkout?id=2&token=5")
                .then()
                .statusCode(400);
    }

    @Test
    void checkout_ifNotDelivered() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkout?id=3&token=5")
                .then()
                .statusCode(400);
    }

    @Test
    void checkout_ifTokenIsIncorrect() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkout?id=1&token=6")
                .then()
                .statusCode(400);
    }

    @Test
    void returnParcel_ifExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/return?id=1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", is("RETURNED"));
    }

    @Test
    void returnParcel_ifNotExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/return?id=2")
                .then()
                .statusCode(400);
    }

    @Test
    void returnParcel_ifNotCollected() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/return?id=3")
                .then()
                .statusCode(400);
    }
}