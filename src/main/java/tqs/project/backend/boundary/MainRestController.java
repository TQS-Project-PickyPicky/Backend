package tqs.project.backend.boundary;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDto;
import tqs.project.backend.data.user.User;
import tqs.project.backend.service.MainService;
import tqs.project.backend.util.ConverterUtils;

@RestController
@RequestMapping("/api/main")
public class MainRestController {

    @Autowired
    private MainService mainService;
    
    //create new acp + partner
    @PostMapping("/registerACP")
    public ResponseEntity<CollectionPoint> registerACP(@Valid 
                    @RequestBody CollectionPointDto cpDto,
                    @RequestParam(value="passwordCheck", required=true) String passwordCheck, 
                    @RequestParam(value="zipcode", required = true) String zipcode, 
                    @RequestParam(value="city", required = true) String city){

        if (!cpDto.getPartner().getPassword().equals(passwordCheck)) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CollectionPoint cp = ConverterUtils.fromCollectionPointDTOToCollectionPoint(cpDto);

        if(mainService.findPartnerByUsername(cp.getPartner().getUsername())!= null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CollectionPoint cp1 = mainService.saveCPPoint(cp, zipcode);

        if (cp1 == null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<CollectionPoint>(cp1, HttpStatus.CREATED);
    
    }

    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam(value = "username", required=true) String username, 
                                    @RequestParam(value = "password", required=true) String password){
        
        User user = mainService.findByUsernameAndPassword(username, password);
        return new ResponseEntity<User>(user, HttpStatus.OK);

    }
}
