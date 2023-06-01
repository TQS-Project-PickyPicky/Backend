package tqs.project.backend.data.collection_point;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionPointRepository extends JpaRepository<CollectionPoint, Integer> {
    List<CollectionPoint> findByStatus(Boolean status);
}
