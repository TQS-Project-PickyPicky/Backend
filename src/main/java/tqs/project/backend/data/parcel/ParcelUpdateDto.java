package tqs.project.backend.data.parcel;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParcelUpdateDto {
    private String clientName;
    private String clientEmail;
    private Integer clientPhone;
    private Integer clientMobilePhone;
    private String expectedArrival;
    private ParcelStatus status;
    private Integer collectionPointId;
}
