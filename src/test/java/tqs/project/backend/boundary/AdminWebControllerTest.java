package tqs.project.backend.boundary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDDto;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.service.AdminService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminWebController.class)
class AdminWebControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AdminService adminService;
    
    @Test
    void testGetAcpPages() throws Exception {
        
        List<CollectionPointDDto> cps = new ArrayList<>();

        CollectionPointDDto cp1 = new CollectionPointDDto();
        cp1.setName("nome1");
        cp1.setType("library");
        cp1.setEmail("email1@ua.pt");

        CollectionPointDDto cp2 = new CollectionPointDDto();
        cp2.setName("nome2");
        cp2.setType("cafe");
        cp2.setEmail("email2@ua.pt");

        CollectionPointDDto cp3 = new CollectionPointDDto();
        cp3.setName("nome3");
        cp3.setType("florist");
        cp3.setEmail("email3@ua.pt");

        cps.add(cp1);
        cps.add(cp2);
        cps.add(cp3);
        
        when(adminService.getCollectionPointsDDto(true)).thenReturn(cps);

        mockMvc.perform(get("/admin/acp-pages"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-dashboard"))
            .andExpect(model().attributeExists("cps"))
            .andExpect(model().attribute("cps", Matchers.is(cps)));
        
        verify(adminService, times(1)).getCollectionPointsDDto(true);
    }
    
    @Test
    void testDeleteACP() throws Exception {
        mockMvc.perform(get("/admin/acp-candidates/{idACP}/delete", 1))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/acp-pages"));
        
        verify(adminService, times(1)).deleteCollectionPointAndParcels(1);
    }

    @Test
    void testGetAcpPagesById() throws Exception {

        Integer idACP = 1;
        CollectionPoint cp1 = new CollectionPoint();
        cp1.setName("nome1");
        cp1.setType("library");
        cp1.setCapacity(100);
        cp1.setAddress("Some address 1");
        cp1.setLatitude(-8.1);
        cp1.setLongitude(42.0);
        cp1.setOwnerName("Matilde");
        cp1.setOwnerEmail("email1@ua.pt");
        cp1.setOwnerGender("Female");
        cp1.setOwnerPhone(910000000);
        cp1.setStatus(true);

        List<Parcel> parcels = new ArrayList<>();
        Parcel parcel1 = new Parcel();
        parcel1.setToken(123);
        parcel1.setClientName("John Doe");
        parcel1.setClientEmail("john@example.com");
        parcel1.setClientPhone(123456789);
        parcel1.setClientMobilePhone(987654321);
        parcel1.setExpectedArrival(LocalDate.now().plusDays(2));
        parcel1.setStatus(ParcelStatus.PLACED);
        parcel1.setCollectionPoint(cp1); 

        Parcel parcel2 = new Parcel();
        parcel2.setToken(456);
        parcel2.setClientName("Jane Smith");
        parcel2.setClientEmail("jane@example.com");
        parcel2.setClientPhone(987654321);
        parcel2.setClientMobilePhone(123456789);
        parcel2.setExpectedArrival(LocalDate.now().plusDays(3));
        parcel2.setStatus(ParcelStatus.IN_TRANSIT);
        parcel2.setCollectionPoint(cp1); 

        Parcel parcel3 = new Parcel();
        parcel3.setToken(789);
        parcel3.setClientName("Alice Johnson");
        parcel3.setClientEmail("alice@example.com");
        parcel3.setClientPhone(456789123);
        parcel3.setClientMobilePhone(321654987);
        parcel3.setStatus(ParcelStatus.DELIVERED);
        parcel3.setCollectionPoint(cp1);

        Parcel parcel4 = new Parcel();
        parcel4.setToken(987);
        parcel4.setClientName("Bob Wilson");
        parcel4.setClientEmail("bob@example.com");
        parcel4.setClientPhone(654789123);
        parcel4.setClientMobilePhone(987321654);
        parcel4.setStatus(ParcelStatus.COLLECTED);
        parcel4.setCollectionPoint(cp1);
        
        parcels.add(parcel1);
        parcels.add(parcel2);
        parcels.add(parcel3);
        parcels.add(parcel4);

            
        Partner partner1 = new Partner();
        partner1.setPassword("pass123");
        partner1.setUsername("username1");
        partner1.setCollectionPoint(cp1);
    
        cp1.setPartner(partner1);

        Map<String, Long> charData = parcels.stream().collect(Collectors.groupingBy(parcel -> parcel.getStatus().toString(), Collectors.counting()));
        

        when(adminService.getCollectionPointById(idACP)).thenReturn(cp1);
        when(adminService.getStatusParcels(idACP)).thenReturn(charData);
        when(adminService.getParcelByCollectionPointId(idACP)).thenReturn(parcels);

        mockMvc.perform(get("/admin/acp-pages/{idACP}", idACP))
                .andExpect(status().isOk())
                .andExpect(view().name("acp-info"))
                .andExpect(model().attribute("cp", Matchers.is(cp1)))
                .andExpect(model().attribute("chartData", Matchers.is(charData)))
                .andExpect(model().attribute("parcels", Matchers.is(parcels)));
    }

    @Test
    void testDeleteParcel() throws Exception {
        Integer idACP = 1;
        Integer idParcel = 2;

        mockMvc.perform(get("/admin/acp-pages/{idACP}/delete/{idParcel}", idACP, idParcel))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/acp-pages/" + idACP));

        verify(adminService, times(1)).deleteParcel(idParcel);
    }

    @Test
    void testGetCandidateAcp() throws Exception {
        List<CollectionPointDDto> cps = new ArrayList<>();
        CollectionPointDDto cp1 = new CollectionPointDDto();
        cp1.setName("nome1");
        cp1.setType("library");
        cp1.setEmail("email1@ua.pt");

        CollectionPointDDto cp2 = new CollectionPointDDto();
        cp2.setName("nome2");
        cp2.setType("cafe");
        cp2.setEmail("email2@ua.pt");

        CollectionPointDDto cp3 = new CollectionPointDDto();
        cp3.setName("nome3");
        cp3.setType("florist");
        cp3.setEmail("email3@ua.pt");

        cps.add(cp1);
        cps.add(cp2);
        cps.add(cp3);

        when(adminService.getCollectionPointsDDto(false)).thenReturn(cps);

        mockMvc.perform(get("/admin/acp-candidates"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-applications"))
                .andExpect(model().attribute("cps", Matchers.is(cps)));
    }
    

    
}