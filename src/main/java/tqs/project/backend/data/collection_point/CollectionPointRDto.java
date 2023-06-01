package tqs.project.backend.data.collection_point;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CollectionPointRDto {
    private Integer id;
    private String name;
    private String type;
    private Integer capacity;
    private String address;
    private Boolean status;
}
