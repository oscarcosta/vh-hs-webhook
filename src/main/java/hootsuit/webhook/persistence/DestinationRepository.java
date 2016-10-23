package hootsuit.webhook.persistence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import hootsuit.webhook.model.Destination;

public interface DestinationRepository extends CrudRepository<Destination, Long> {
	
	@Modifying
	@Transactional
	@Query("update Destination d set d.online = true where d.id = ?")
	int setDestinationOnline(Long id);
	
	@Modifying
	@Transactional
	@Query("update Destination d set d.online = false where d.id = ?")
	int setDestinationOffline(Long id);

}
