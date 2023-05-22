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

@Controller
public class CollectionPointWebController {

    @Autowired
    private CollectionPointService cpService;

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

        if (!cpService.saveCPPoint(cp, zipcode, city)){ //was able to retreive data
            model.addAttribute("errorCoordinates", "Couldn't get that address... Try again.");
            return "acp-application";
        }

        
        model.addAttribute("showModal", true);
        return "home-picky";
        
    }
}
