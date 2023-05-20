package ies.project.backend.data.parcel;

import javax.persistence.*;

import ies.project.backend.data.collection_point.CollectionPoint;
import ies.project.backend.data.store.Store;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "parcel")
@Getter
@Setter
@NoArgsConstructor
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer token;
    private String clientName;
    private String clientEmail;
    private Integer clientPhone;
    private Integer clientMobilePhone;
    private LocalDate expectedArrival;
    @ManyToOne
    private Store store;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private CollectionPoint collectionPoint;
    private ParcelStatus status;
}
