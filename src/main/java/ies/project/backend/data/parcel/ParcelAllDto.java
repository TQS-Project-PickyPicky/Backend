package ies.project.backend.data.parcel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParcelAllDto {
    private Integer id;
    private ParcelStatus status;

    //Constructor
    public ParcelAllDto(Integer id, ParcelStatus status) {
        this.id = id;
        this.status = status;
    }
}
