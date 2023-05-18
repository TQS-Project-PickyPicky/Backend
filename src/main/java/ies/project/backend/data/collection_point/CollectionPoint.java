package ies.project.backend.data.collection_point;

import javax.persistence.*;

import ies.project.backend.data.parcel.Parcel;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "collection_point")
@Getter
@Setter
@NoArgsConstructor
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
    private String owner_name;
    private String owner_email;
    private String owner_gender;
    private Integer owner_phone;
    private Integer owner_mobile_phone;
    @OneToMany(mappedBy = "collectionPoint")
    private List<Parcel> parcels;
}