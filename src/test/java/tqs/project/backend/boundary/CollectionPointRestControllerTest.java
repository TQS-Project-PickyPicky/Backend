package tqs.project.backend.boundary;

import org.apache.tomcat.util.net.TLSClientHelloExtractor;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRDto;
import tqs.project.backend.data.parcel.ParcelMinimal;
import tqs.project.backend.data.parcel.ParcelMinimalEta;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.exception.CollectionPointNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        // All CollectionPoints
        when(collectionPointService.getAll()).thenReturn(List.of(
                new CollectionPointRDto(1, "CP1", "Rua 1", 100, "Porto", true),
                new CollectionPointRDto(2, "CP2", "Rua 2", 100, "Lisboa", true)
                ));

        when(collectionPointService.getAll("1111-111")).thenReturn(List.of(
                new CollectionPointRDto(2, "CP2", "Rua 2", 100, "Lisboa", true),
                new CollectionPointRDto(1, "CP1", "Rua 1", 100, "Porto", true)
                ));

        // Add CollectionPoint

        when(collectionPointService.saveCPPoint(any(CollectionPoint.class), any(String.class)))
                .thenReturn(true);

        // Update CollectionPoint

        when(collectionPointService.updateCPPoint(eq(2),any(CollectionPoint.class))).thenThrow(new CollectionPointNotFoundException(2));
        when(collectionPointService.updateCPPoint(eq(1),any(CollectionPoint.class))).thenReturn(new CollectionPointRDto(1, "CP1", "Rua 1", 100, "Porto", true));

        // Delete CollectionPoint
        CollectionPointRDto cp1 = new CollectionPointRDto(1, "CP1", "Rua 1", 100, "Porto", true);
        when(collectionPointService.deleteCPPoint(1)).thenReturn(cp1);
        when(collectionPointService.deleteCPPoint(2)).thenThrow(new CollectionPointNotFoundException(2));

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
    void getAllCollectionPoints() {
        RestAssuredMockMvc.given()
                .when()
                .get("/api/acp/all")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[0].name", is("CP1"))
                .body("[1].id", is(2))
                .body("[1].name", is("CP2"));
    }

    @Test
    void getAllCollectionPointsByLocation() {
        RestAssuredMockMvc.given()
                .when()
                .get("/api/acp/all?zip=1111-111")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", is(2))
                .body("[0].name", is("CP2"))
                .body("[1].id", is(1))
                .body("[1].name", is("CP1"));
    }

    @Test
    void createCollectionPoint() {
        RestAssuredMockMvc.given()
                    .contentType("application/json")
                    .body("{\"name\":\"CP3\",\"type\":\"Jogo\",\"capacity\":100,\"address\":\"Rua 3\",\"ownerName\":\"Diogo\",\"ownerEmail\":\"d@ua.pt\",\"ownerGender\":\"Male\",\"ownerPhone\":965833174,\"ownerMobilePhone\":965833174,\"zipcode\":\"1111-111\",\"partner\":{\"username\":\"DiogoPaiva\",\"password\":\"diogo\"}}")
                .when()
                    .post("/api/acp/add")
                .then()
                    .statusCode(200)
                    .body("name", is("CP3"));
    }

    @Test
    void updateCollectionPoint() {
        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body("{\"name\":\"CP3\",\"type\":\"Jogo\",\"capacity\":100,\"address\":\"Rua 3\",\"ownerName\":\"Diogo\"}")
                .when()
                .put("/api/acp/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("CP1"));
    }

    @Test
    void updateCollectionPointNotFound() {
        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body("{\"name\":\"CP3\",\"type\":\"Jogo\",\"capacity\":100,\"address\":\"Rua 3\",\"ownerName\":\"Diogo\"}")
                .when()
                .put("/api/acp/2")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .delete("/api/acp/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("CP1"));
    }

    @Test
    void deleteCollectionPointNotFound() {
        RestAssuredMockMvc.given()
                .when()
                .delete("/api/acp/2")
                .then()
                .statusCode(404);
    }

    @Test
    void getAllParcels() {
        RestAssuredMockMvc.given()
                .when()
                .get("/api/acp/1")
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
                .get("/api/acp/parcel/1")
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
                .get("/api/acp/parcel/2")
                .then()
                .statusCode(400);
    }

    @Test
    void checkin_ifExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkin/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", is("DELIVERED"));
    }

    @Test
    void checkin_ifNotExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkin/2")
                .then()
                .statusCode(400);
    }

    @Test
    void checkin_ifNotInTransit() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkin/3")
                .then()
                .statusCode(400);
    }

    @Test
    void checkout_ifExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkout/1?token=5")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", is("COLLECTED"));
    }

    @Test
    void checkout_ifNotExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkout/2?token=5")
                .then()
                .statusCode(400);
    }

    @Test
    void checkout_ifNotDelivered() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkout/3?token=5")
                .then()
                .statusCode(400);
    }

    @Test
    void checkout_ifTokenIsIncorrect() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/checkout/1?token=6")
                .then()
                .statusCode(400);
    }

    @Test
    void returnParcel_ifExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/return/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", is("RETURNED"));
    }

    @Test
    void returnParcel_ifNotExistsInCollectionPoint() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/return/2")
                .then()
                .statusCode(400);
    }

    @Test
    void returnParcel_ifNotCollected() {
        RestAssuredMockMvc.given()
                .when()
                .post("/api/acp/parcel/return/3")
                .then()
                .statusCode(400);
    }
}