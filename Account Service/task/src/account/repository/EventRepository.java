package account.repository;

import account.model.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Ray
 */
@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findAll();
}