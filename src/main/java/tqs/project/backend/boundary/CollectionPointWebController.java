package tqs.project.backend.boundary;

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

    private final CollectionPointService collectionPointService;

    public CollectionPointWebController(CollectionPointService collectionPointService) {
        this.collectionPointService = collectionPointService;
    }

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
