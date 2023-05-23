package tqs.project.backend.data.collection_point;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import tqs.project.backend.data.parcel.Parcel;

import lombok.*;
import tqs.project.backend.data.partner.Partner;

import java.util.List;

@Entity
@Table(name = "collection_point")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotEmpty(message = "This parameter is required")
    private String name;
    private String type;
    private Integer capacity;

    @NotEmpty(message = "This parameter is required")
    private String address;

    private Double latitude;
    private Double longitude;

    @NotEmpty(message = "This parameter is required")
    private String ownerName;

    @NotEmpty
    @Email(message = "Not accepted email")
    private String ownerEmail;

    private String ownerGender;

    @NotNull(message = "This parameter is required")
    private Integer ownerPhone;

    private Integer ownerMobilePhone;

    private Boolean status;

    @Valid
    @OneToOne(mappedBy = "collectionPoint")
    private Partner partner;

    @OneToMany(mappedBy = "collectionPoint", cascade = CascadeType.REMOVE)
    private List<Parcel> parcels;

    @Override
    public String toString() {
        return "CollectionPoint [id=" + id + ", name=" + name + ", type=" + type + ", capacity=" + capacity
                + ", address=" + address + ", latitude=" + latitude + ", longitude=" + longitude + ", ownerName="
                + ownerName + ", ownerEmail=" + ownerEmail + ", ownerGender=" + ownerGender + ", ownerPhone="
                + ownerPhone + ", ownerMobilePhone=" + ownerMobilePhone + ", status=" + status + ", partner=" + partner
                + ", parcels=" + parcels + "]";
    }

    
}