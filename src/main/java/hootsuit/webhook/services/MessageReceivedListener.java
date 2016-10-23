package hootsuit.webhook.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import hootsuit.webhook.events.MessageReceivedEvent;
import hootsuit.webhook.model.Message;

@Component
public class MessageReceivedListener implements ApplicationListener<MessageReceivedEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageReceivedListener.class);
	
	@Autowired
	private MessageProcessor messageProcessor;

	@Override
	public void onApplicationEvent(MessageReceivedEvent event) {
		Message messageReceived = event.getMessage();
		
		logger.debug("Event received for Message {}", messageReceived.getId());
		
		// Async call to messageProcessor
		messageProcessor.processMessagesForDestination(messageReceived.getDestination());		
	}
}
