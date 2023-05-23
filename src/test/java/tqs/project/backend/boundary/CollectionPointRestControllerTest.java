package tqs.project.backend.boundary;

import tqs.project.backend.data.parcel.ParcelMinimal;
import tqs.project.backend.data.parcel.ParcelMinimalEta;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.service.CollectionPointService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(CollectionPointRestController.class)
class CollectionPointRestControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CollectionPointService collectionPointService;

    @BeforeEach
    void setUp() throws ParcelNotFoundException, InvalidParcelStatusChangeException, IncorrectParcelTokenException {
        RestAssuredMockMvc.mockMvc(mvc);

        // All Parcels
        when(collectionPointService.getAllParcels(1)).thenReturn(List.of(
                new ParcelMinimal(1, ParcelStatus.IN_TRANSIT),
                new ParcelMinimal(2, ParcelStatus.PLACED)
                ));

        // Parcel
        when(collectionPointService.getParcel(1)).thenReturn(new ParcelMinimalEta(1, ParcelStatus.IN_TRANSIT, 5L));
        when(collectionPointService.getParcel(2)).thenThrow(new ParcelNotFoundException(2));

        // Checkin
        when(collectionPointService.checkIn(1)).thenReturn(new ParcelMinimal(1, ParcelStatus.DELIVERED));
        when(collectionPointService.checkIn(2)).thenThrow(new InvalidParcelStatusChangeException(ParcelStatus.PLACED, ParcelStatus.DELIVERED));
        when(collectionPointService.checkIn(3)).thenThrow(new ParcelNotFoundException(3));

        // Checkout
        when(collectionPointService.checkOut(1, 5)).thenReturn(new ParcelMinimal(1, ParcelStatus.COLLECTED));
        when(collectionPointService.checkOut(2, 5)).thenThrow(new InvalidParcelStatusChangeException(ParcelStatus.PLACED, ParcelStatus.COLLECTED));
        when(collectionPointService.checkOut(3, 5)).thenThrow(new ParcelNotFoundException(3));
        when(collectionPointService.checkOut(1, 6)).thenThrow(new IncorrectParcelTokenException(6, 1));

        // Return
        when(collectionPointService.returnParcel(1)).thenReturn(new ParcelMinimal(1, ParcelStatus.RETURNED));
        when(collectionPointService.returnParcel(2)).thenThrow(new InvalidParcelStatusChangeException(ParcelStatus.PLACED, ParcelStatus.RETURNED));
        when(collectionPointService.returnParcel(3)).thenThrow(new ParcelNotFoundException(3));
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
                .body("[1].status", is("PLACED"));
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