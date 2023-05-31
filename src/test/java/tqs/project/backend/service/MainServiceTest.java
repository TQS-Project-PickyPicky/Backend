package tqs.project.backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;

@ExtendWith(MockitoExtension.class)
public class MainServiceTest {
    
    @Mock
    private PartnerRepository partnerRepository;
    
    @Mock
    private CollectionPointRepository collectionPointRepository;

    @InjectMocks
    private MainService mainService;

    @Test
    void testSaveCPPoint_SuccessfulSave() {
        CollectionPoint point = new CollectionPoint();
        String zipCode = "3105-325";

        when(partnerRepository.save(any())).thenReturn(null);
        when(collectionPointRepository.save(any())).thenReturn(null);

        boolean result = mainService.saveCPPoint(point, zipCode);

        Assertions.assertTrue(result);
        verify(partnerRepository, times(1)).save(any());
        verify(collectionPointRepository, times(1)).save(any());
    }

    @Test
    void testSaveCPPoint_InvalidZipCode() {
        CollectionPoint point = new CollectionPoint();
        String zipCode = "0"; //invalid zipcode

        boolean result = mainService.saveCPPoint(point, zipCode);

        Assertions.assertFalse(result);
        verify(partnerRepository, never()).save(any());
        verify(collectionPointRepository, never()).save(any());
    }

    @Test
    void testFindByUsernameAndPassword() {
        String username = "admin";
        String password = "admin";
        Partner expectedPartner = new Partner();

        when(partnerRepository.findByUsernameAndPassword(username, password)).thenReturn(expectedPartner);

        Partner result = mainService.findByUsernameAndPassword(username, password);

        Assertions.assertEquals(expectedPartner, result);
        verify(partnerRepository, times(1)).findByUsernameAndPassword(username, password);
    }
}
