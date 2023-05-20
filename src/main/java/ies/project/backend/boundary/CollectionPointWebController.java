package ies.project.backend.boundary;

import ies.project.backend.data.collection_point.CollectionPoint;
import ies.project.backend.data.collection_point.CollectionPointRepository;
import ies.project.backend.data.parcel.*;
import ies.project.backend.data.store.Store;
import ies.project.backend.data.store.StoreRepository;
import ies.project.backend.data.store.StoreStatus;
import ies.project.backend.service.CollectionPointService;
import ies.project.backend.util.CantAccessParcelException;
import ies.project.backend.util.DifferentStateException;
import ies.project.backend.util.IncorrectParcelTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CollectionPointWebController {

    private final CollectionPointService collectionPointService;

    public CollectionPointWebController(CollectionPointService collectionPointService) {
        this.collectionPointService = collectionPointService;
    }

    //@Autowired
    //private CollectionPointRepository collectionPointRepository;
//
    //@Autowired
    //private ParcelRepository parcelRepository;
//
    //@Autowired
    //private StoreRepository storeRepository;

    @GetMapping("/acp")
    public String acp(@RequestParam(value="id") Integer id,Model model) {
        // TODO - Change to ask for id of logged in user
        List<ParcelAllDto> parcels = collectionPointService.getallParcels(id);
        model.addAttribute("parcels", parcels);
        return "acp";
    }

    @GetMapping("/acp/parcel")
    public String parcel(@RequestParam(value="id") Integer id, Model model) throws CantAccessParcelException {
        try{
            ParcelDto parcel = collectionPointService.getParcel(id);
            model.addAttribute("parcel", parcel);
            model.addAttribute("collectionPointService", collectionPointService);
            return "parcelib";
        } catch (CantAccessParcelException e) {
            return "redirect:/acp";
        }

    }

    @PostMapping("/acp/parcel/checkin")
    public String parcelCheckIn(@RequestParam(value="id") Integer id, Model model) {
        try{
            collectionPointService.checkIn(id);
            return "redirect:/acp";
        } catch (CantAccessParcelException | DifferentStateException e) {
            return "redirect:/acp/parcel?id=" + id;
        }
    }

    @PostMapping("/acp/parcel/checkout")
    public String parcelCheckOut(@RequestParam(value="id") Integer id, @RequestParam(value="token") Integer token, Model model) {
        try {
            collectionPointService.checkOut(id, token);
            return "redirect:/acp";
        } catch (IncorrectParcelTokenException | CantAccessParcelException | DifferentStateException e) {
            return "redirect:/acp/parcel?id=" + id;
        }
    }

    @PostMapping("/acp/parcel/return")
    public String parcelReturn(@RequestParam(value="id") Integer id, Model model) {
        try {
            collectionPointService.returnParcel(id);
            return "redirect:/acp";
        } catch (CantAccessParcelException | DifferentStateException e) {
            return "redirect:/acp/parcel?id=" + id;
        }

    }

    @GetMapping("/acp/parcel/add")
    public String parcelReturn(Model model) {
        CollectionPoint collectionPoint = new CollectionPoint();
        collectionPoint.setName("Collection Point 1");
        collectionPoint.setType("Collection Point");
        collectionPoint.setCapacity(100);
        collectionPoint.setAddress("Rua do ISEP");
        collectionPoint.setLatitude(41.178);
        collectionPoint.setLongitude(-8.608);
        collectionPoint.setOwnerName("João");
        collectionPoint.setOwnerEmail("joao@ua.pt");
        collectionPoint.setOwnerGender("M");
        collectionPoint.setOwnerPhone(123456789);
        collectionPoint.setOwnerMobilePhone(987654321);

        Store store = new Store();
        store.setName("Store 1");
        store.setStatus(StoreStatus.ACCEPTED);

        Parcel parcel = new Parcel();
        parcel.setToken(123456);
        parcel.setClientName("João");
        parcel.setClientEmail("joao@ua.pt");
        parcel.setClientPhone(123456789);
        parcel.setClientMobilePhone(987654321);
        parcel.setExpectedArrival(LocalDate.now().plusDays(5));
        parcel.setStore(store);
        parcel.setStatus(ParcelStatus.IN_TRANSIT);
        parcel.setCollectionPoint(collectionPoint);

        Parcel parcel2 = new Parcel();
        parcel2.setToken(123456);
        parcel2.setClientName("Jorge");
        parcel2.setClientEmail("jorge@ua.pt");
        parcel2.setClientPhone(123456789);
        parcel2.setClientMobilePhone(987654321);
        parcel2.setExpectedArrival(LocalDate.now().plusDays(7));
        parcel2.setStore(store);
        parcel2.setStatus(ParcelStatus.IN_TRANSIT);
        parcel2.setCollectionPoint(collectionPoint);

        Parcel parcel3 = new Parcel();
        parcel3.setToken(123456);
        parcel3.setClientName("Gabriel");
        parcel3.setClientEmail("gabriel@ua.pt");
        parcel3.setClientPhone(123456789);
        parcel3.setClientMobilePhone(987654321);
        parcel3.setExpectedArrival(LocalDate.now());
        parcel3.setStore(store);
        parcel3.setStatus(ParcelStatus.COLLECTED);
        parcel3.setCollectionPoint(collectionPoint);

        Parcel parcel4 = new Parcel();
        parcel4.setToken(123456);
        parcel4.setClientName("Tiago");
        parcel4.setClientEmail("tiago@ua.pt");
        parcel4.setClientPhone(123456789);
        parcel4.setClientMobilePhone(987654321);
        parcel4.setExpectedArrival(LocalDate.now().plusDays(3));
        parcel4.setStore(store);
        parcel4.setStatus(ParcelStatus.RETURNED);
        parcel4.setCollectionPoint(collectionPoint);

        List<Parcel> parcels = new ArrayList<>();
        parcels.add(parcel);
        parcels.add(parcel2);
        parcels.add(parcel3);
        parcels.add(parcel4);

        //Save
        //CollectionPoint acp = collectionPointRepository.save(collectionPoint);
        //storeRepository.save(store);
        //parcelRepository.saveAll(parcels);

        return "parcelir";
    }
}
