package com.hootsuit.webhook.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hootsuit.webhook.model.Destination;
import com.hootsuit.webhook.model.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {

	List<Message> findAllByDestinationOrderByIdAsc(Destination destination);

}
