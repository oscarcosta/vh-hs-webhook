package hootsuit.webhook.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hootsuit.webhook.model.Message;
import hootsuit.webhook.persistence.MessageRepository;

@Service
public class MessageService {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private MessageRequestService messageRequestService;
	
    /**
	 * Post a message to destination, after saving in the database... 
	 */
	public void postMessageToDestination(Message message) {
		message = messageRepository.save(message);
		
		logger.debug("Received Message {} for Destination {}", message.getId(), message.getDestinationUrl());
		
		if (message.isDestinationOnline()) {
			messageRequestService.sendMessage(message);
		}
	}
	
}
