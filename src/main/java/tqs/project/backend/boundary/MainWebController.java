package tqs.project.backend.boundary;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDto;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.service.CollectionPointService;
import tqs.project.backend.util.ConverterUtils;

@Controller
@RequestMapping("/main")
public class MainWebController {

    private final CollectionPointService collectionPointService;

    public MainWebController(CollectionPointService collectionPointService) {
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
    public String registerACP(@Valid @ModelAttribute("cp") CollectionPointDto cpDto,
                            BindingResult result,
                            @RequestParam("passwordCheck") String passwordCheck, 
                            @RequestParam("zipcode") String zipcode, 
                            @RequestParam("city") String city, 
                            Model model){

        if (result.hasErrors()) {
            model.addAttribute("cp", cpDto);
            return "acp-application"; 
        }

        model.addAttribute("cp", cpDto);

        if (!cpDto.getPartner().getPassword().equals(passwordCheck)) {
            model.addAttribute("error", "Passwords do not match");
            return "acp-application";
        }

        CollectionPoint cp = ConverterUtils.fromCollectionPointDTOToCollectionPoint(cpDto);

        if (!collectionPointService.saveCPPoint(cp, zipcode)){ //was able to retreive data
            model.addAttribute("errorCoordinates", "Couldn't get that address... Try again.");
            return "acp-application";
        }

        
        model.addAttribute("showModal", true);
        return "redirect:/main/login";
    
    }
    // Login form
    @GetMapping("/login")
    public String login() {
        return "home-picky.html";
    }

    // Login form with error
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login.html";
    }
}
