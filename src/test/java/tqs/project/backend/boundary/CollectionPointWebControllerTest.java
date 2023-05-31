package tqs.project.backend.boundary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tqs.project.backend.service.CollectionPointService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import tqs.project.backend.data.parcel.ParcelMinimal;
import tqs.project.backend.data.parcel.ParcelMinimalEta;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.IncorrectParcelTokenException;

import java.util.List;

@WebMvcTest(CollectionPointWebController.class)
class CollectionPointWebControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CollectionPointService collectionPointService;

    @BeforeEach
    void setUp() throws InvalidParcelStatusChangeException, IncorrectParcelTokenException, ParcelNotFoundException {

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
    void getAllParcels() throws Exception {
        mvc.perform(get("/acp-page/acp?id=1").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("acp"))
                .andExpect(model().attributeExists("parcels"));
    }

    @Test
    void getParcel_ifExistsInCollectionPoint() throws Exception {
        mvc.perform(get("/acp-page/acp/parcel?id=1").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("parcelib"))
                .andExpect(model().attributeExists("parcel"))
                .andExpect(model().attributeExists("collectionPointService"));
    }

    @Test
    void getParcel_ifNotExistsInCollectionPoint() throws Exception {
        mvc.perform(get("/acp-page/acp/parcel?id=2").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp"));
    }

    @Test
    void checkIn_ifExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/checkin?id=1").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp"));
    }

    @Test
    void checkIn_ifNotExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/checkin?id=2").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp/parcel?id=2"));
    }

    @Test
    void checkIn_ifNotInTransit() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/checkin?id=3").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp/parcel?id=3"));
    }

    @Test
    void checkOut_ifExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/checkout?id=1&token=5").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp"));
    }

    @Test
    void checkOut_ifNotExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/checkout?id=2&token=5").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp/parcel?id=2"));
    }

    @Test
    void checkOut_ifNotDelivered() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/checkout?id=3&token=5").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp/parcel?id=3"));
    }

    @Test
    void checkOut_ifTokenIsIncorrect() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/checkout?id=1&token=6").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp/parcel?id=1"));
    }

    @Test
    void returnParcel_ifExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/return?id=1").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp"));
    }

    @Test
    void returnParcel_ifNotExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/return?id=2").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp/parcel?id=2"));
    }

    @Test
    void returnParcel_ifNotCollected() throws Exception {
        mvc.perform(post("/acp-page/acp/parcel/return?id=3").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp-page/acp/parcel?id=3"));
    }
}