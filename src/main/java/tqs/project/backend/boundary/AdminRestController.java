package tqs.project.backend.boundary;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDDto;
import tqs.project.backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    
    @Autowired
    private AdminService adminService;

    //list of acps registered
    @GetMapping("/getListACPs/accp")
    public ResponseEntity<List<CollectionPointDDto>> getListAcpsRegistered(){
        return new ResponseEntity<List<CollectionPointDDto>> (adminService.getCollectionPointsDDto(true), HttpStatus.OK);
    }

    //delete an ACP
    @DeleteMapping("/deleteACP/{idACP}")
    public ResponseEntity<String> deleteACP(@PathVariable Integer idACP) throws Exception{
        adminService.deleteCollectionPointAndParcels(idACP);
        return ResponseEntity.ok().body("Deleted with success");
    }

    //details of an ACP -> how many of each type 
    @GetMapping("/acp/details/{idACP}")
    public ResponseEntity<Map<String, Long>> detailsACP(@PathVariable Integer idACP) throws Exception{
        return new ResponseEntity<Map<String, Long>> (adminService.getStatusParcels(idACP), HttpStatus.OK);
    }

    //delete a parcel
    @DeleteMapping("/acp/delete/{idParcel}")
    public ResponseEntity<String> deleteParcel(@PathVariable Integer idParcel) throws Exception{
        adminService.deleteParcel(idParcel);
        return new ResponseEntity<String>("Deleted with success", HttpStatus.OK);
    }

    //accept application
    @PutMapping("/acp/{idAcp}/application/accept")
    public ResponseEntity<CollectionPoint> acceptApplication(@PathVariable Integer idAcp) throws Exception{
        CollectionPoint cp = adminService.getCollectionPointById(idAcp);
        cp.setStatus(true);
        adminService.saveACPoint(cp);
        return new ResponseEntity<CollectionPoint>(cp, HttpStatus.OK);
    }

    //refuseapplication
    @DeleteMapping("/acp/{idAcp}/application/refuse")
    public ResponseEntity<String> denyApplication(@PathVariable Integer idAcp) throws Exception{
        adminService.deleteCPPoint(idAcp);
        return new ResponseEntity<String>("Deleted with success", HttpStatus.OK);
    }

    //list of acps candidatadas
    @GetMapping("/getListACPs/naccp")
    public ResponseEntity<List<CollectionPointDDto>> getListAcpsCandidates(){
        return new ResponseEntity<List<CollectionPointDDto>> (adminService.getCollectionPointsDDto(false), HttpStatus.OK);
    }

    //getACP information
    @GetMapping("/acp/info/{idACP}")
    public ResponseEntity<CollectionPoint> getInfoACP(@PathVariable Integer idACP) throws Exception{
        return new ResponseEntity<CollectionPoint> (adminService.getCollectionPointById(idACP), HttpStatus.OK);
    }


}
