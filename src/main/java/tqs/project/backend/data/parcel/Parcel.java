package ies.project.backend.data.parcel;

import javax.persistence.*;

import ies.project.backend.data.collection_point.CollectionPoint;
import ies.project.backend.data.store.Store;
import lombok.*;

@Entity
@Table(name = "parcel")
@Getter
@Setter
@NoArgsConstructor
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
