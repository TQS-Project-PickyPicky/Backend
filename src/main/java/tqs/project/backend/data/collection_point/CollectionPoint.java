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
    private String address;
    private Double latitude;
    private Double longitude;
    @OneToMany(mappedBy = "collectionPoint")
    private List<Parcel> parcels;
}