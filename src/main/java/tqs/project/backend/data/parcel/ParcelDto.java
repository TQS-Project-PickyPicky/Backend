package tqs.project.backend.data.parcel;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParcelDto {
    private Integer id;
    private Integer token;
    private String clientName;
    private String clientEmail;
    private Integer clientPhone;
    private Integer clientMobilePhone;
    private String expectedArrival;
    private String status;
    private Integer storeId;
    private Integer collectionPointId;
}
