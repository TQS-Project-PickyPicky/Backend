package tqs.project.backend.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDDto;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.exception.ParcelNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    
    @Mock(lenient = true)
    private CollectionPointRepository collectionPointRepository;

    @Mock(lenient = true)
    private ParcelRepository parcelRepository;

    @Mock(lenient = true)
    private PartnerRepository partnerRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void testGetCollectionPointById_ExistingId_ReturnsCollectionPoint() throws Exception {

        Integer id = 1;
        CollectionPoint collectionPoint = new CollectionPoint();
        when(collectionPointRepository.findById(id)).thenReturn(Optional.of(collectionPoint));

        CollectionPoint result = adminService.getCollectionPointById(id);

        assertEquals(collectionPoint, result);
    }

    @Test
    void testGetCollectionPointById_NonExistingId_ThrowsException() {

        Integer id = 1;
        when(collectionPointRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> adminService.getCollectionPointById(id));
    }

    @Test
    public void testGetCollectionPointsDDto_ValidStatus_ReturnsCollectionPointDDtos() {

        Boolean status = true;
        List<CollectionPoint> collectionPoints = new ArrayList<>();
        when(collectionPointRepository.findByStatus(status)).thenReturn(collectionPoints);

        List<CollectionPointDDto> result = adminService.getCollectionPointsDDto(status);

        assertEquals(collectionPoints.size(), result.size());
    }

    @Test
    public void testDeleteCollectionPointAndParcels_ExistingId_DeletesAssociatedPartnerAndParcels() throws Exception {

        CollectionPoint collectionPoint = new CollectionPoint();
        Integer idACP = collectionPoint.getId();
        Partner partner = new Partner();
        List<Parcel> parcels = new ArrayList<>();
        collectionPoint.setPartner(partner);
        collectionPoint.setParcels(parcels);

        when(collectionPointRepository.findById(idACP)).thenReturn(Optional.of(collectionPoint));

        adminService.deleteCollectionPointAndParcels(idACP);

        verify(partnerRepository, times(1)).delete(partner);
        verify(parcelRepository, times(1)).deleteAll(parcels);
        verify(collectionPointRepository, times(1)).delete(collectionPoint);
    }

    @Test
    public void testSaveACPoint_ValidPoint_CallsSaveMethodInRepository() {
        CollectionPoint point = new CollectionPoint();

        adminService.saveACPoint(point);

        verify(collectionPointRepository, times(1)).save(point);
    }

    @Test
    public void testDeletePartnerById_ValidId_CallsDeleteByIdMethodInRepository() {

        Integer id = 1;

        adminService.deletePartnerById(id);

        verify(partnerRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteCPPoint_ValidId_CallsDeleteMethodInRepository() {
        // Set up
        Integer id = 1;
        CollectionPoint cp = new CollectionPoint();
        when(collectionPointRepository.findById(id)).thenReturn(Optional.of(cp));

        // Execute
        adminService.deleteCPPoint(id);

        // Verify
        verify(collectionPointRepository, times(1)).delete(cp);
    }

    @Test
    public void testGetStatusParcels_ExistingCollectionPointId_ReturnsStatusCountMap() throws Exception {

        Integer collectionPointId = 1;
        List<Parcel> parcels = new ArrayList<>();
        CollectionPoint collectionPoint = new CollectionPoint();
        collectionPoint.setParcels(parcels);
        when(collectionPointRepository.findById(collectionPointId)).thenReturn(Optional.of(collectionPoint));

        Map<String, Long> result = adminService.getStatusParcels(collectionPointId);

        assertNotNull(result);
    }

    @Test
    public void testGetParcelByCollectionPointId_ExistingId_ReturnsParcels() throws Exception {

        Integer idACP = 1;
        List<Parcel> parcels = new ArrayList<>();
        CollectionPoint cp = new CollectionPoint();
        cp.setParcels(parcels);

        when(collectionPointRepository.findById(idACP)).thenReturn(Optional.of(cp));

        List<Parcel> result = adminService.getParcelByCollectionPointId(idACP);

        assertEquals(parcels, result);
    }

    @Test
    public void testDeleteParcel_ValidId_DeletesParcelAndRemovesCollectionPointAssociation() throws ParcelNotFoundException {

        Integer id = 1;
        Parcel parcel = new Parcel();
        parcel.setCollectionPoint(new CollectionPoint());
        when(parcelRepository.findById(id)).thenReturn(Optional.of(parcel));

        Parcel result = adminService.deleteParcel(id);

        verify(parcelRepository, times(1)).delete(parcel);
        assertNull(result.getCollectionPoint());
    }

}
