package tqs.project.backend.data.collection_point;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tqs.project.backend.data.partner.Partner;

import javax.persistence.OneToOne;
import javax.validation.Valid;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CollectionPointCreateDto {
    private String name;
    private String type;
    private Integer capacity;
    private String address;
    private String ownerName;
    private String ownerEmail;
    private String ownerGender;
    private Integer ownerPhone;
    private Integer ownerMobilePhone;

    private String zipcode;

    @Valid
    @OneToOne(mappedBy = "collectionPoint")
    private Partner partner;
}
