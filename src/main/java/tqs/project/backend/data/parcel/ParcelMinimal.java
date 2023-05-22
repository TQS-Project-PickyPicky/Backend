package tqs.project.backend.data.parcel;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParcelMinimal {
    private Integer id;
    private ParcelStatus status;
}
