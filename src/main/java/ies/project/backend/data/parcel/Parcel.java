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
    private String client_name;
    private String client_email;
    private Integer client_phone;
    private Integer client_mobile_phone;
    private LocalDate expectedArrival;
    @ManyToOne
    private Store store;
    @ManyToOne
    private CollectionPoint collectionPoint;
    private ParcelStatus status;
}
