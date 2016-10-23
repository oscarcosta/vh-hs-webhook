package hootsuit.webhook.persistence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import hootsuit.webhook.model.Destination;

public interface DestinationRepository extends CrudRepository<Destination, Long> {
	
	@Modifying
	@Transactional
	@Query("update Destination d set d.online = ?1 where d.id = ?2")
	int setDestinationOnline(Boolean online, Long id);

}
