package tqs.project.backend.boundary;

import tqs.project.backend.data.parcel.*;
import tqs.project.backend.exception.IncorrectParcelTokenException;
import tqs.project.backend.exception.InvalidParcelStatusChangeException;
import tqs.project.backend.exception.ParcelNotFoundException;
import tqs.project.backend.service.CollectionPointService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tqs.project.backend.service.ParcelService;
import java.util.List;


@Controller
@RequestMapping("/acp-page")
public class CollectionPointWebController {

    private static final String URL1 = "redirect:/acp-page/acp/parcel?id=";
    private static final String URL2 = "redirect:/acp-page/acp?id=";
    private static final String URL3 = "&acp=";
    private final CollectionPointService collectionPointService;
    private final ParcelService parcelService;

    public CollectionPointWebController(CollectionPointService collectionPointService, ParcelService parcelService) {
        this.collectionPointService = collectionPointService;
        this.parcelService = parcelService;
    }

    @GetMapping("/acp")
    public String acp(@RequestParam(value="id") Integer id,Model model) {
        // TODO - Change to ask for id of logged in user
        List<ParcelMinimal> parcels = collectionPointService.getAllParcels(id);
        model.addAttribute("parcels", parcels);
        model.addAttribute("acp", id);
        return "acp";
    }

    @GetMapping("/acp/parcel")
    public String parcel(@RequestParam(value="id") Integer id, @RequestParam(value = "acp") Integer acp,Model model) throws ParcelNotFoundException {
        try{
            ParcelMinimalEta parcel = collectionPointService.getParcel(id);
            model.addAttribute("parcel", parcel);
            model.addAttribute("collectionPointService", collectionPointService);
            model.addAttribute("acp", acp);
            return "parcelib";
        } catch (ParcelNotFoundException e) {
            return "redirect:/acp-page/acp";
        }

    }

    @PostMapping("/acp/parcel/checkin")
    public String parcelCheckIn(@RequestParam(value="id") Integer id, @RequestParam(value="acp") Integer acp, Model model) {
        try{
            parcelService.checkIn(id);
<<<<<<< HEAD
            return "redirect:/acp-page/acp";
=======
            return URL2 + acp;
>>>>>>> d19509d792dc30898f61775b7a08ead04eb47b97
        } catch (ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return URL1 + id + URL3 + acp;
        }
    }

    @PostMapping("/acp/parcel/checkout")
    public String parcelCheckOut(@RequestParam(value="id") Integer id, @RequestParam(value="token") Integer token, @RequestParam(value="acp") Integer acp, Model model) {
        try {
            parcelService.checkOut(id, token);
<<<<<<< HEAD
            return "redirect:/acp-page/acp";
=======
            return URL2 + acp;
>>>>>>> d19509d792dc30898f61775b7a08ead04eb47b97
        } catch (IncorrectParcelTokenException | ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return URL1 + id + URL3 + acp;
        }
    }

    @PostMapping("/acp/parcel/return")
    public String parcelReturn(@RequestParam(value="id") Integer id, @RequestParam(value="acp") Integer acp,Model model) {
        try {
            parcelService.returnParcel(id);
<<<<<<< HEAD
            return "redirect:/acp-page/acp";
=======
            return URL2 + acp;
>>>>>>> d19509d792dc30898f61775b7a08ead04eb47b97
        } catch (ParcelNotFoundException | InvalidParcelStatusChangeException e) {
            return URL1 + id + URL3 + acp;
        }
    }
}
