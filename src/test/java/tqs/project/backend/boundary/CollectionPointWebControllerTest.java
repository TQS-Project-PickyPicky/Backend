package tqs.project.backend.boundary;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import tqs.project.backend.service.CollectionPointService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CollectionPointWebController.class)
public class CollectionPointWebControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CollectionPointService collectionPointService;

    @BeforeEach
    public void setUp(){
        
    }

    @Test
    public void getAllForms() throws Exception{
        mvc.perform(get("/registerACP").contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(view().name("acp-application"))
            .andExpect(model().attributeExists("cp"));
    }

    @Test
    public void registerACP_ValidForm_Success() throws Exception {

        when(collectionPointService.saveCPPoint(any(), anyString(), anyString())).thenReturn(true);

        mvc.perform(post("/registerACP")
                .param("name", "cp1")
                .param("type", "Library")
                .param("capacity", "100")              
                .param("ownerName", "João")
                .param("ownerEmail", "joao@ua.pt")
                .param("ownerPhone", "910000000")
                .param("passwordCheck", "pass1")
                .param("zipcode", "12345")
                .param("city", "Aveiro")
                .param("address", "Rua do ISEP")
                .param("partner.username", "username1")
                .param("partner.password", "pass1"))
                .andExpect(status().isOk())
                .andExpect(view().name("home-picky"))
                .andExpect(model().attributeExists("cp"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeDoesNotExist("errorCoordinates"));

        verify(collectionPointService).saveCPPoint(any(), eq("12345"), eq("Aveiro"));
    }

    @Test
    public void registerACP_InvalidForm_ValidationError() throws Exception {
        mvc.perform(post("/registerACP")
                .param("name", "")
                .param("type", "")
                .param("capacity", "0")
                .param("address", "")
                .param("ownerName", "")
                .param("ownerEmail", "invalid_email")
                .param("ownerPhone", "invalid_phone")
                .param("passwordCheck", "password")
                .param("zipcode", "12345")
                .param("city", "Aveiro"))
                .andExpect(status().isOk())
                .andExpect(view().name("acp-application"))
                .andExpect(model().attributeExists("cp"))
                .andExpect(model().hasErrors());

        verifyNoInteractions(collectionPointService);
    }

    @Test
    public void registerACP_PasswordMismatch_Error() throws Exception {
        mvc.perform(post("/registerACP")
                .param("name", "cp1")
                .param("type", "Library")
                .param("capacity", "100")
                .param("address", "Rua do ISEP")
                .param("ownerName", "João")
                .param("ownerEmail", "joao@ua.pt")
                .param("ownerPhone", "910000000")
                .param("passwordCheck", "mismatched_password")
                .param("partner.username", "username1")
                .param("partner.password", "pass1")
                .param("zipcode", "12345")
                .param("city", "Aveiro"))
                .andExpect(status().isOk())
                .andExpect(view().name("acp-application"))
                .andExpect(model().attributeExists("cp"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeDoesNotExist("errorCoordinates"));

        verifyNoInteractions(collectionPointService);
    }

}
