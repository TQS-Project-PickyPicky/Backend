package tqs.project.backend.data.collection_point;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionPointUpdateDto {
    private String name;
    private String type;
    private Integer capacity;
    private Integer ownerPhone;
    private Integer ownerMobilePhone;
    private Boolean status;
}
