package tqs.project.backend.data.parcel;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParcelMinimalEta {
    private Integer id;
    private ParcelStatus status;
    private Long eta;
}
