package tqs.project.backend.data.collection_point;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CollectionPointDDto {
    private Integer id;
    private String name;
    private String type;
    private String email;
}
