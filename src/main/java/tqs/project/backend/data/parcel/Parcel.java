package tqs.project.backend.data.parcel;

import javax.persistence.*;

import lombok.*;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.store.Store;

import java.time.LocalDate;

@Entity
@Table(name = "parcel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    private Store store;
    @ManyToOne
    private CollectionPoint collectionPoint;
    private ParcelStatus Status;
}
