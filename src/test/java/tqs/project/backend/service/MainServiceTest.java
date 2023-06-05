package tqs.project.backend.service;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.project.backend.data.admin.Admin;
import tqs.project.backend.data.admin.AdminRepository;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.user.User;

@ExtendWith(MockitoExtension.class)
public class MainServiceTest {
    
    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private AdminRepository adminRepository;
    
    @Mock
    private CollectionPointRepository collectionPointRepository;

    @InjectMocks
    private MainService mainService;

    @Test
    void testSaveCPPoint_SuccessfulSave() {
        CollectionPoint point = new CollectionPoint();
        String zipCode = "3105-325";

        when(partnerRepository.save(any())).thenReturn(null);
        when(collectionPointRepository.save(any())).thenReturn(point);

        CollectionPoint cp = mainService.saveCPPoint(point, zipCode);

        Assertions.assertNotNull(cp);
        verify(partnerRepository, times(1)).save(any());
        verify(collectionPointRepository, times(1)).save(any());
    }

    @Test
    void testSaveCPPoint_InvalidZipCode() {
        CollectionPoint point = new CollectionPoint();
        String zipCode = "0"; //invalid zipcode

        CollectionPoint cp = mainService.saveCPPoint(point, zipCode);

        Assertions.assertNull(cp);
        verify(partnerRepository, never()).save(any());
        verify(collectionPointRepository, never()).save(any());
    }

    @Test
    void testFindPartnerByUsernamePartnerExists(){
        Partner partner = new Partner();
        partner.setUsername("username1");
        
        when(partnerRepository.findByUsername(partner.getUsername())).thenReturn(partner);
        when(adminRepository.findByUsername(partner.getUsername())).thenReturn(null);

        Partner result = mainService.findPartnerByUsername("username1");

        assertNotNull(result);
        assertEquals(result.getUsername(), partner.getUsername());
        verify(partnerRepository, times(1)).findByUsername(anyString());
        verify(adminRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void testFindPartnerByUsernameAdminExists(){
        Admin admin = new Admin();
        admin.setUsername("admin");
        
        when(adminRepository.findByUsername(admin.getUsername())).thenReturn(admin);

        Partner result = mainService.findPartnerByUsername("admin");

        assertNull(result);
        verify(partnerRepository, never()).findByUsername(anyString());
        verify(adminRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void testFindByUsernameAndPasswordAdmin() {
        String username = "admin";
        String password = "admin";
        Admin admin = new Admin();

        when(adminRepository.findByUsernameAndPassword(username, password)).thenReturn(admin);

        User result = mainService.findByUsernameAndPassword(username, password);

        Assertions.assertEquals(admin, result);
        assert(result instanceof Admin);
        verify(adminRepository, times(2)).findByUsernameAndPassword(username, password);
    }

    @Test
    void testFindByUsernameAndPasswordPartner() {
        String username = "someuser";
        String password = "somepass";
        Partner expectedPartner = new Partner();

        when(partnerRepository.findByUsernameAndPassword(username, password)).thenReturn(expectedPartner);

        User result = mainService.findByUsernameAndPassword(username, password);

        Assertions.assertEquals(expectedPartner, result);
        assert(result instanceof Partner);
        verify(partnerRepository, times(1)).findByUsernameAndPassword(username, password);
    }

    @Test
    public void testGetCollectionPointByPartnerId() throws Exception {
        Integer partnerId = 1;
        Integer collectionPointId = 123;

        Partner partner = new Partner();
        CollectionPoint collectionPoint = new CollectionPoint();
        collectionPoint.setId(collectionPointId);
        partner.setCollectionPoint(collectionPoint);

        when(partnerRepository.findById(anyInt())).thenReturn(Optional.of(partner));

        Integer result = mainService.getCollectionPointByPartnerId(partnerId);

        assertEquals(collectionPointId, result);
    }

    @Test
    public void testGetCollectionPointByPartnerId_PartnerNotFound() throws Exception {

        when(partnerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> mainService.getCollectionPointByPartnerId(1));
    }
}
