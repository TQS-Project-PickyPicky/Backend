package tqs.project.backend.data.collection_point;

import javax.persistence.*;

import tqs.project.backend.data.parcel.Parcel;
import lombok.*;

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
    private String name;
    private String type;
    private Integer capacity;
    private String address;
    private Double latitude;
    private Double longitude;
    private String ownerName;
    private String ownerEmail;
    private String ownerGender;
    private Integer ownerPhone;
    private Integer ownerMobilePhone;
    @OneToMany(mappedBy = "collectionPoint", cascade = CascadeType.REMOVE)
    private List<Parcel> parcels;
}