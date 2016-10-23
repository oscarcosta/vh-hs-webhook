package hootsuit.webhook.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import hootsuit.webhook.events.MessageReceivedEvent;
import hootsuit.webhook.model.Message;
import hootsuit.webhook.persistence.DestinationRepository;
import hootsuit.webhook.persistence.MessageRepository;

@Component
public class MessageProcessor implements ApplicationListener<MessageReceivedEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
	
	@Autowired
	private DestinationRepository destinationRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private MessageRequestService messageRequestService;

	@Override
	public void onApplicationEvent(MessageReceivedEvent event) {
		Message messageReceived = event.getMessage();
		
		logger.debug("Processing messages for Destination {}", messageReceived.getDestinationUrl());
		
		List<Message> messages = messageRepository.findAllByDestinationOrderByIdAsc(messageReceived.getDestination());
		
		for (Message message : messages) {
			messageRequestService.sendMessage(message);
		}
	}

}
