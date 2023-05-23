package tqs.project.backend.data.store;

import javax.persistence.*;

import lombok.*;
import tqs.project.backend.data.parcel.Parcel;

import java.util.List;


@Entity
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    @OneToMany(mappedBy = "store")
    private List<Parcel> parcels;
}
