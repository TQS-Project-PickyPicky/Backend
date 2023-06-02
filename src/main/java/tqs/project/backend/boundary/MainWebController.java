package tqs.project.backend.boundary;

import javax.validation.Valid;

import org.hamcrest.core.IsInstanceOf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;
import tqs.project.backend.data.admin.Admin;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDto;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.user.User;
import tqs.project.backend.service.MainService;
import tqs.project.backend.util.ConverterUtils;

@Controller
@RequestMapping("/main")
@Slf4j
public class MainWebController {

    private final MainService mainService;

    public MainWebController(MainService mainService) {
        this.mainService = mainService;
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
                            RedirectAttributes redirectAttributes,
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

        if (mainService.saveCPPoint(cp, zipcode) == null){ //was able to retreive data
            model.addAttribute("errorCoordinates", "Couldn't get that address... Try again.");
            return "acp-application";
        }
        redirectAttributes.addFlashAttribute("message", "Success");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/main/login";
    
    }

    @GetMapping("/login")
    public String login() {
        return "home-picky";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam String username, @RequestParam String password, Model model) throws Exception{
        User user = mainService.findByUsernameAndPassword(username, password);
        if (user instanceof Admin) {
            return "redirect:/admin/acp-pages";
        } else {
            if (user instanceof Partner) {
                return "redirect:/acp-page/acp?id=" + mainService.getCollectionPointByPartnerId(user.getId());
            } else {
                model.addAttribute("error", "Username or password incorrect");
                return "home-picky";
            }
        }
       
    }

}
