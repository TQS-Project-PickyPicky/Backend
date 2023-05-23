package tqs.project.backend.data.store;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StoreDto {
    Integer id;
    String name;
    List<Integer> parcelsId;
}
