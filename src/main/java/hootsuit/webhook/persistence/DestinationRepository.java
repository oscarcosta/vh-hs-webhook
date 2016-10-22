package hootsuit.webhook.persistence;

import org.springframework.data.repository.CrudRepository;

import hootsuit.webhook.model.Destination;

public interface DestinationRepository extends CrudRepository<Destination, Long> {

}
