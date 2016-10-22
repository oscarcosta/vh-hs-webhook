package hootsuit.webhook.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hootsuit.webhook.model.Destination;
import hootsuit.webhook.model.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

	List<Message> findByDestination(Destination destination);
}
