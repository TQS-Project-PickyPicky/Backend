package ies.project.backend.boundary;

import ies.project.backend.data.parcel.ParcelAllDto;
import ies.project.backend.data.parcel.ParcelDto;
import ies.project.backend.data.parcel.ParcelStatus;
import ies.project.backend.service.CollectionPointService;
import ies.project.backend.util.CantAccessParcelException;
import ies.project.backend.util.DifferentStateException;
import ies.project.backend.util.IncorrectParcelTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(CollectionPointWebController.class)
class CollectionPointWebControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CollectionPointService collectionPointService;

    @BeforeEach
    void setUp() throws DifferentStateException, IncorrectParcelTokenException, CantAccessParcelException {

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
    void getAllParcels() throws Exception {
        mvc.perform(get("/acp?id=1").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("acp"))
                .andExpect(model().attributeExists("parcels"));
    }

    @Test
    void getParcel_ifExistsInCollectionPoint() throws Exception {
        mvc.perform(get("/acp/parcel?id=1").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("parcelib"))
                .andExpect(model().attributeExists("parcel"))
                .andExpect(model().attributeExists("collectionPointService"));
    }

    @Test
    void getParcel_ifNotExistsInCollectionPoint() throws Exception {
        mvc.perform(get("/acp/parcel?id=2").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp"));
    }

    @Test
    void checkIn_ifExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp/parcel/checkin?id=1").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp"));
    }

    @Test
    void checkIn_ifNotExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp/parcel/checkin?id=2").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp/parcel?id=2"));
    }

    @Test
    void checkIn_ifNotInTransit() throws Exception {
        mvc.perform(post("/acp/parcel/checkin?id=3").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp/parcel?id=3"));
    }

    @Test
    void checkOut_ifExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp/parcel/checkout?id=1&token=5").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp"));
    }

    @Test
    void checkOut_ifNotExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp/parcel/checkout?id=2&token=5").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp/parcel?id=2"));
    }

    @Test
    void checkOut_ifNotDelivered() throws Exception {
        mvc.perform(post("/acp/parcel/checkout?id=3&token=5").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp/parcel?id=3"));
    }

    @Test
    void checkOut_ifTokenIsIncorrect() throws Exception {
        mvc.perform(post("/acp/parcel/checkout?id=1&token=6").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp/parcel?id=1"));
    }

    @Test
    void returnParcel_ifExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp/parcel/return?id=1").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp"));
    }

    @Test
    void returnParcel_ifNotExistsInCollectionPoint() throws Exception {
        mvc.perform(post("/acp/parcel/return?id=2").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp/parcel?id=2"));
    }

    @Test
    void returnParcel_ifNotCollected() throws Exception {
        mvc.perform(post("/acp/parcel/return?id=3").contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acp/parcel?id=3"));
    }
}