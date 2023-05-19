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
    private Integer token;
    private String clientName;
    private String clientEmail;
    private Integer clientPhone;
    private Integer clientMobilePhone;
    private LocalDate expectedArrival;
    private ParcelStatus status;
    @ManyToOne
    private Store store;
    @ManyToOne
    private CollectionPoint collectionPoint;
}