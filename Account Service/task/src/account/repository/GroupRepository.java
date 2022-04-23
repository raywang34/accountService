package account.repository;

import account.model.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Ray
 */
@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {
    Optional<Group> findByCodeIgnoreCase(String code);
}
