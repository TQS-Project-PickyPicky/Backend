package tqs.project.backend.data.partner;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Integer> {

    Partner findByUsernameAndPassword(String username, String password);
<<<<<<< HEAD

    Partner findByCollectionPointId(Integer id);
=======
>>>>>>> d19509d792dc30898f61775b7a08ead04eb47b97
}
