package tqs.project.backend.boundary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import tqs.project.backend.data.admin.Admin;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.service.MainService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainWebController.class)
public class MainWebControllerTest {

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private MainService mainService;

    @Test
    void getAllForms() throws Exception{
        Partner partner = new Partner();
        partner.setUsername("partner");
        partner.setPassword("password");
        mvc.perform(get("/main/registerACP").contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(view().name("acp-application"))
            .andExpect(model().attributeExists("cp"));
    }

    @Test
    void registerACP_ValidForm_Success() throws Exception {

        when(mainService.saveCPPoint(any(), anyString())).thenReturn(true);

        mvc.perform(post("/main/registerACP")
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
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/main/login"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeDoesNotExist("errorCoordinates"));

        verify(mainService).saveCPPoint(any(), eq("12345"));
    }

    @Test
    void registerACP_InvalidForm_ValidationError() throws Exception {
        mvc.perform(post("/main/registerACP")
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

        verifyNoInteractions(mainService);
    }

    @Test
    void registerACP_PasswordMismatch_Error() throws Exception {
        mvc.perform(post("/main/registerACP")
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

        verifyNoInteractions(mainService);
    }

    @Test
    void badAuthentication() throws Exception {
        when(mainService.findByUsernameAndPassword(any(), any())).thenReturn(null);

        mvc.perform(post("/main/login")
                .param("username", "invalid")
                .param("password", "invalid"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("home-picky"));
    }

    @Test
    void adminAuthentication() throws Exception {
        when(mainService.findAdmin(any(), any())).thenReturn(new Admin());
        mvc.perform(post("/main/login")
                .param("username", "admin")
                .param("password", "admin"))
                .andExpect(redirectedUrl("/admin/acp-pages"));
    }

    @Test
    void partnerAuthentication() throws Exception {
        Partner partner = new Partner();
        partner.setUsername("partner");
        partner.setPassword("password");
        when(mainService.findByUsernameAndPassword("partner", "password")).thenReturn(partner);
        mvc.perform(post("/main/login")
                .param("username", "partner")
                .param("password", "password"))
                .andExpect(redirectedUrl("/acp/home"));
    }

}
