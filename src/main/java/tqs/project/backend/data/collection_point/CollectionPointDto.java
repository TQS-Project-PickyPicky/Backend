package tqs.project.backend.data.collection_point;

import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tqs.project.backend.data.partner.Partner;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CollectionPointDto {
    private String name;
    private String type;
    private Integer capacity;
    private String address;    
    private String ownerName;
    private String ownerEmail;
    private String ownerGender;
    private Integer ownerPhone;
    private Integer ownerMobilePhone;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    private Partner partner;
}
