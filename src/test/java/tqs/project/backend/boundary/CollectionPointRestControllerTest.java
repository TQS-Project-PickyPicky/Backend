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
    }

    @Test
    void getAllCollectionPoints() {
        RestAssuredMockMvc.given()
                .when()
                .get("/api/acp")
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
                .get("/api/acp?zip=1111-111")
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
                    .post("/api/acp")
                .then()
                    .statusCode(201)
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
                .get("/api/acp/1/parcels")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[0].status", is("IN_TRANSIT"))
                .body("[1].id", is(2))
                .body("[1].status", is("PLACED"));
    }
}