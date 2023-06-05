package tqs.project.backend.boundary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDto;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.user.User;
import tqs.project.backend.service.MainService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MainRestController.class)
public class MainWebRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MainService mainService;

    @Test
    public void testRegisterACP() throws Exception {
        Partner partner = new Partner();
        partner.setUsername("username");
        partner.setPassword("password");
        String passwordCheck = "password";
        String zipcode = "12345";
        String city = "city123";

        CollectionPointDto cpDto = new CollectionPointDto();
        cpDto.setPartner(partner);

        CollectionPoint cp = new CollectionPoint();
        cp.setPartner(partner);

        when(mainService.saveCPPoint(any(CollectionPoint.class), anyString())).thenReturn(cp);

        mockMvc.perform(post("/api/main/registerACP")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(cpDto))
                .param("passwordCheck", passwordCheck)
                .param("zipcode", zipcode)
                .param("city", city))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(cp.getName()))
                .andExpect(jsonPath("$.address").value(cp.getAddress()));
    }

    @Test
    public void testRegisterACPPasswordMissmatch() throws Exception {
        Partner partner = new Partner();
        partner.setUsername("username");
        partner.setPassword("password");
        String passwordCheck = "password1";
        String zipcode = "12345";
        String city = "city123";

        CollectionPointDto cpDto = new CollectionPointDto();
        cpDto.setPartner(partner);

        CollectionPoint cp = new CollectionPoint();
        cp.setPartner(partner);

        when(mainService.saveCPPoint(any(CollectionPoint.class), anyString())).thenReturn(cp);

        mockMvc.perform(post("/api/main/registerACP")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(cpDto))
                .param("passwordCheck", passwordCheck)
                .param("zipcode", zipcode)
                .param("city", city))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testRegisterACPUserExists() throws Exception {
        Partner partner = new Partner();
        partner.setUsername("username");
        partner.setPassword("password");
        String passwordCheck = "password";
        String zipcode = "12345";
        String city = "city123";

        CollectionPointDto cpDto = new CollectionPointDto();
        cpDto.setPartner(partner);

        CollectionPoint cp = new CollectionPoint();
        cp.setPartner(partner);

        when(mainService.saveCPPoint(any(CollectionPoint.class), anyString())).thenReturn(cp);
        when(mainService.findPartnerByUsername(anyString())).thenReturn(partner); //return something is found -> not null

        mockMvc.perform(post("/api/main/registerACP")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(cpDto))
                .param("passwordCheck", passwordCheck)
                .param("zipcode", zipcode)
                .param("city", city))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testLogin() throws Exception {
        String username = "testuser";
        String password = "testpassword";

        User user = new User();

        when(mainService.findByUsernameAndPassword(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(get("/api/main/login")
                .param("username", username)
                .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.password").value(user.getPassword()));
    }

    // Helper method to convert objects to JSON string
    private static String toJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}