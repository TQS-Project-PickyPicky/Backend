package tqs.project.backend.boundary;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.service.CollectionPointService;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.parcel.*;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.service.CollectionPointService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CollectionPointWebController {

    @Autowired
    private final CollectionPointService collectionPointService;

    public CollectionPointWebController(CollectionPointService collectionPointService) {
        this.collectionPointService = collectionPointService;
    }

    //get ACP application page -> fucntional
    @GetMapping("/registerACP")
    public String registerACPPage(Model model){
        model.addAttribute("showModal", false);
        
        CollectionPoint cp = new CollectionPoint();
        cp.setPartner(new Partner());

        model.addAttribute("cp", cp);

        return "acp-application";
    }

    //put information on the database -> ACP application
    @PostMapping("/registerACP")
    public String registerACP(@Valid @ModelAttribute("cp") CollectionPoint cp,
                            BindingResult result,
                            @RequestParam("passwordCheck") String passwordCheck, 
                            @RequestParam("zipcode") String zipcode, 
                            @RequestParam("city") String city, 
                            Model model){

        if (result.hasErrors()) {
            model.addAttribute("cp", cp);
            return "acp-application"; 
        }

        model.addAttribute("cp", cp);

        if (!cp.getPartner().getPassword().equals(passwordCheck)) {
            System.out.println("Password: " + cp.getPartner().getPassword());
            System.out.println("Password Check: " + passwordCheck);
            model.addAttribute("error", "Passwords do not match");
            return "acp-application";
        }

        if (!collectionPointService.saveCPPoint(cp, zipcode, city)){ //was able to retreive data
            model.addAttribute("errorCoordinates", "Couldn't get that address... Try again.");
            return "acp-application";
        }

        
        model.addAttribute("showModal", true);
        return "home-picky";
        


    @GetMapping("/acp")
    public String acp(@RequestParam(value="id") Integer id,Model model) {
        // TODO - Change to ask for id of logged in user
        List<ParcelMinimal> parcels = collectionPointService.getAllParcels(id);
        model.addAttribute("parcels", parcels);
        return "acp";
    }

    @GetMapping("/acp/parcel")
    public String parcel(@RequestParam(value="id") Integer id, Model model) throws ParcelNotFoundException {
        try{
            ParcelMinimalEta parcel = collectionPointService.getParcel(id);
            model.addAttribute("parcel", parcel);
            model.addAttribute("collectionPointService", collectionPointService);
            return "parcelib";
        } catch (ParcelNotFoundException e) {
            return "redirect:/acp";
        }

    }

    @PostMapping("/acp/parcel/checkin")
    public String parcelCheckIn(@RequestParam(value="id") Integer id, Model model) {
        try{
            collectionPointService.checkIn(id);
            return "redirect:/acp";
        } catch (ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return "redirect:/acp/parcel?id=" + id;
        }
    }

    @PostMapping("/acp/parcel/checkout")
    public String parcelCheckOut(@RequestParam(value="id") Integer id, @RequestParam(value="token") Integer token, Model model) {
        try {
            collectionPointService.checkOut(id, token);
            return "redirect:/acp";
        } catch (IncorrectParcelTokenException | ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return "redirect:/acp/parcel?id=" + id;
        }
    }

    @PostMapping("/acp/parcel/return")
    public String parcelReturn(@RequestParam(value="id") Integer id, Model model) {
        try {
            collectionPointService.returnParcel(id);
            return "redirect:/acp";
        } catch (ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return "redirect:/acp/parcel?id=" + id;
        }
    }
}
