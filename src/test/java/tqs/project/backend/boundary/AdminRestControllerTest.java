package tqs.project.backend.boundary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDDto;
import tqs.project.backend.service.AdminService;

@WebMvcTest(AdminRestController.class)
class AdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @BeforeEach
    void setUp() throws Exception{
        CollectionPoint cp = new CollectionPoint();
        List<CollectionPointDDto> acpList = new ArrayList<>();
        acpList.add(new CollectionPointDDto());
        when(adminService.getCollectionPointById(anyInt())).thenReturn(cp);
        when(adminService.getCollectionPointsDDto(anyBoolean())).thenReturn(acpList);
        when(adminService.getStatusParcels(anyInt())).thenReturn(new  HashMap<String, Long>());
    }


    @Test
    void testGetListAcpsRegistered() throws Exception {

        mockMvc.perform(get("/api/admin/getListACPs/accp"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        verify(adminService, times(1)).getCollectionPointsDDto(true);

    }

    @Test
    void testDeleteACP() throws Exception {

        mockMvc.perform(delete("/api/admin/deleteACP/{idACP}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted with success"));

        verify(adminService, times(1)).deleteCollectionPointAndParcels(1);
    }

    @Test
    void detailsACP() throws Exception {

        mockMvc.perform(get("/api/admin/acp/details/" + 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap());

        verify(adminService, times(1)).getStatusParcels(1);
    }

    @Test
    void deleteParcel() throws Exception {

        mockMvc.perform(delete("/api/admin/acp/delete/"+ 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted with success"));

        verify(adminService, times(1)).deleteParcel(1);
    }

    @Test
    void acceptApplication() throws Exception {

        mockMvc.perform(put("/api/admin/acp/" + 1 +"/application/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(adminService, times(1)).getCollectionPointById(1);
        verify(adminService, times(1)).saveACPoint(any(CollectionPoint.class));
    }

    @Test
    void refuseApplication() throws Exception {

        mockMvc.perform(delete("/api/admin/acp/" + 1 +"/application/refuse"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted with success"));

        verify(adminService, times(1)).deleteCPPoint(1);
    }

    @Test
    void getApplications() throws Exception {

        mockMvc.perform(get("/api/admin/getListACPs/naccp"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());

        verify(adminService, times(1)).getCollectionPointsDDto(false);
    }

    @Test
    void getACPInfo() throws Exception {

        mockMvc.perform(get("/api/admin/acp/info/" + 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists());

        verify(adminService, times(1)).getCollectionPointById(1);
    }


}