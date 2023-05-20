package tqs.project.backend.data.parcel;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParcelCreateDto {
    private String clientName;
    private String clientEmail;
    private Integer clientPhone;
    private Integer clientMobilePhone;
    private String expectedArrival;
    private Integer storeId;
    private Integer collectionPointId;
}
