package ies.project.backend.data.parcel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParcelDto {
    private Integer id;
    private ParcelStatus status;
    private Long eta;

    //Constructor
    public ParcelDto(Integer id, ParcelStatus status, Long eta) {
        this.id = id;
        this.status = status;
        this.eta = eta;
    }
}
