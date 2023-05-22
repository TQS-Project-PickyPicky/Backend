package tqs.project.backend.boundary;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.utils.ResolveLocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "application-integrationtest.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class CollectionPointWebControllerTestIT {

    @LocalServerPort
    int localPort;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createCollectionPoint_ifCorrectQuery() throws Exception{

        ArrayList<Double> coordinates = ResolveLocation.resolveAddress("3810-193", "Aveiro");

        MvcResult result = mvc.perform(post("/registerACP")
            .param("name", "cp1")
            .param("type", "Library")
            .param("capacity", "100")              
            .param("ownerName", "João")
            .param("ownerEmail", "joao@ua.pt")
            .param("ownerPhone", "910000000")
            .param("passwordCheck", "pass1")
            .param("zipcode", "3810-193")
            .param("city", "Aveiro")
            .param("address", "Rua do ISEP")
            .param("partner.username", "username1")
            .param("partner.password", "pass1"))
            .andExpect(status().isOk())
            .andExpect(view().name("home-picky"))
            .andExpect(model().attributeExists("cp"))
            .andExpect(model().attributeExists("showModal"))
            .andExpect(model().attributeDoesNotExist("error"))
            .andExpect(model().attributeDoesNotExist("errorCoordinates"))
            .andReturn();;

        CollectionPoint createdEntity = (CollectionPoint) result.getModelAndView().getModel().get("cp");


        assertNotNull(createdEntity);
        assertEquals("cp1", createdEntity.getName());
        assertEquals("Library", createdEntity.getType());
        assertEquals("100", createdEntity.getCapacity().toString());
        assertEquals("João", createdEntity.getOwnerName());
        assertEquals("joao@ua.pt", createdEntity.getOwnerEmail());
        assertEquals("910000000", createdEntity.getOwnerPhone().toString());
        assertEquals("pass1", createdEntity.getPartner().getPassword());
        assertEquals("username1", createdEntity.getPartner().getUsername());
        assertEquals(coordinates.get(0), createdEntity.getLatitude());
        assertEquals(coordinates.get(1), createdEntity.getLongitude());
        assertEquals("Rua do ISEP", createdEntity.getAddress());

    }

    @Test
    public void noCreateCollectionPoint_ifIncorrectQuery() throws Exception{

        MvcResult result = mvc.perform(post("/registerACP")
            .param("name", "cp1")
            .param("type", "Library")
            .param("capacity", "100")              
            .param("ownerName", "João")
            .param("ownerEmail", "joao@ua.pt")
            .param("passwordCheck", "pass1")
            .param("ownerPhone", "")
            .param("zipcode", "3810-193")
            .param("city", "Aveiro")
            .param("address", "Rua do ISEP")
            .param("partner.username", "username1")
            .param("partner.password", "pass1"))
            .andExpect(status().isOk())
            .andExpect(view().name("acp-application")) //goes back to the application
            .andExpect(model().attributeExists("cp"))
            .andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.cp");
            
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        assertNotNull(fieldErrors);

    }

    @Test
    public void noCreateCollectionPoint_ifPasswordsDifferent() throws Exception{

        mvc.perform(post("/registerACP")
            .param("name", "cp1")
            .param("type", "Library")
            .param("capacity", "100")              
            .param("ownerName", "João")
            .param("ownerEmail", "joao@ua.pt")
            .param("ownerPhone", "910000000")
            .param("passwordCheck", "pass1")
            .param("zipcode", "3810-193")
            .param("city", "Aveiro")
            .param("address", "Rua do ISEP")
            .param("partner.username", "username1")
            .param("partner.password", "pass2")) //passwords are different
            .andExpect(status().isOk())
            .andExpect(view().name("acp-application")) //goes back to the application
            .andExpect(model().attributeExists("cp"))
            .andExpect(model().attributeExists("error"));
    }


    @Test
    public void noCreateCollectionPoint_ifNoCoordinatesFound() throws Exception{
        mvc.perform(post("/registerACP")
            .param("name", "cp1")
            .param("type", "Library")
            .param("capacity", "100")              
            .param("ownerName", "João")
            .param("ownerEmail", "joao@ua.pt")
            .param("ownerPhone", "910000000")
            .param("passwordCheck", "pass1")
            .param("zipcode", "")           //no zipcode and city = no found searches on api
            .param("city", "")
            .param("address", "Rua do ISEP")
            .param("partner.username", "username1")
            .param("partner.password", "pass1")) 
            .andExpect(status().isOk())
            .andExpect(view().name("acp-application")) //goes back to the application
            .andExpect(model().attributeExists("cp"))
            .andExpect(model().attributeExists("errorCoordinates"));
    }
    
}
