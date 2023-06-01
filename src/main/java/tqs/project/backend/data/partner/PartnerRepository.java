package tqs.project.backend.data.partner;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Integer> {

    Partner findByUsernameAndPassword(String username, String password);

    Partner findByCollectionPointId(Integer id);
}
