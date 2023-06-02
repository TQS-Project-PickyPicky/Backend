package tqs.project.backend.boundary;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDDto;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.service.AdminService;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminWebController {

    private final AdminService adminService;

    public AdminWebController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    //get list of acps
    @GetMapping("/acp-pages")
    public String getAcpPages(Model model){    
        List<CollectionPointDDto> cps = adminService.getCollectionPointsDDto(true);
        if (!cps.isEmpty()){
            model.addAttribute("cps", cps);
            log.info(cps.get(0).getName() + " ");
        }
        return "admin-dashboard";
    }

    //delete an acp
    @GetMapping("/acp-pages/{idACP}/delete")
    public String deleteACP(@PathVariable Integer idACP) throws Exception{
        //must delete all associated parcels
        adminService.deleteCollectionPointAndParcels(idACP);
        return "redirect:/admin/acp-pages";
    }

    //details about an acp -> includes graphic
    @GetMapping("/acp-pages/{idACP}")
    public String getAcpPages(@PathVariable Integer idACP, Model model) throws Exception{    
        CollectionPoint cp = adminService.getCollectionPointById(idACP);
        Map<String, Long> charData = adminService.getStatusParcels(idACP);
        List<Parcel> parcels = adminService.getParcelByCollectionPointId(idACP);
        model.addAttribute("cp", cp);
        model.addAttribute("chartData", charData);
        model.addAttribute("parcels", parcels);
        return "acp-info";
    }

    //delete a parcel
    @GetMapping("/acp-pages/{idACP}/delete/{idParcel}")
    public String deleteParcel(@PathVariable(value="idACP") Integer idACP, @PathVariable(value="idParcel") Integer idParcel, Model model) throws Exception{
        adminService.deleteParcel(idParcel);
        return "redirect:/admin/acp-pages/" + idACP;
    }

     //get acps candidate
    @GetMapping("/acp-candidates")
    public String getCandidateAcp(Model model){
        List<CollectionPointDDto> cps = adminService.getCollectionPointsDDto(false);//not yet accepted
        log.info(""+cps);
        model.addAttribute("cps", cps);
        return "admin-applications";
    }

    //accept/refuse application
    @GetMapping("/acp-candidates/{idACP}/{bool}")
    public String acceptCandidateAcp(@PathVariable(value="idACP") Integer idACP,@PathVariable(value="bool") Boolean bool, Model model) throws Exception{
        if (bool){ //acp candidate accepted
            CollectionPoint point = adminService.getCollectionPointById(idACP);
            point.setStatus(true);
            adminService.saveACPoint(point);
        } else{
            adminService.deleteCPPoint(idACP); //remove everything from db
        }
        return "redirect:/admin/acp-candidates"; //go back to where we were
    }

    //get information acp candidate
    @GetMapping("/acp-candidates/{id}")
    public String getInformationAcp(@PathVariable(value="id") Integer idACP, Model model) throws Exception{
        CollectionPoint cp = adminService.getCollectionPointById(idACP);
        model.addAttribute("cp", cp);
        return "admin-acpdetails-cand";
    }


    
}
