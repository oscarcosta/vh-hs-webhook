package hootsuit.webhook.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import hootsuit.webhook.events.MessageReceivedEvent;
import hootsuit.webhook.model.Destination;
import hootsuit.webhook.model.Message;
import hootsuit.webhook.persistence.DestinationRepository;
import hootsuit.webhook.persistence.MessageRepository;

@Service
public class MessageProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private DestinationRepository destinationRepository;
	
	private final RestTemplate restTemplate;
	
	public MessageProcessor(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	/**
	 * Async EventListener for MessageReceivedEvent
	 */
	@Async
	@EventListener
	public void messageReceivedListener(MessageReceivedEvent messageReceivedEvent) {
		Message message = messageReceivedEvent.getMessage();
		
		logger.debug("Listening Event for Message {}", message.getId());
		
		// Direct call
		processMessagesForDestination(message.getDestination());
	}
	
	/**
	 * Scheduled method to process the messages saved on database
	 */
	@Scheduled(cron="0 0 */6 * * *") // Run at minute 0 past every 6th hour.
	public void scheduledMessagesProcessor() {
		logger.debug("Executing scheduled message processor at {}", new Date(System.currentTimeMillis()));
		
		destinationRepository.findAll().forEach(destination -> processMessagesForDestination(destination));
	}
	
	/**
	 * Processes a destination's messages asynchronously 
	 * 
	 * @return The list of messages successfully processed
	 */
	@Async
	public Future<List<Message>> processMessagesForDestination(Destination destination) {
		List<Message> sentMessages = new ArrayList<>();
		
		logger.debug("Processing messages for Destination {}", destination.getUrl());
		
		List<Message> messages = messageRepository.findAllByDestinationOrderByIdAsc(destination);
		for (Message message : messages) {
			if (sendMessage(message)) {
				sentMessages.add(message);
			} else {
				break;
			}
		}
		
		return new AsyncResult<>(sentMessages);
	}

	private boolean sendMessage(Message message) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set(HttpHeaders.CONTENT_TYPE, message.getContentType());
			HttpEntity<String> request = new HttpEntity<>(message.getMessageBody() ,headers);
			
			Thread.sleep(500); // wait 0.5 second before send message
			
			logger.debug("Sending Message {} to Destination {}", message.getId(), message.getDestinationUrl());
			
			ResponseEntity<String> entity = restTemplate.postForEntity(message.getDestinationUrl(), request, String.class);
			
			if (entity.getStatusCode().equals(HttpStatus.OK)) {
				onMessageSentOK(message);
				return true;
			} else {
				onMessageSentError(message);
				return false;
			}
		} catch (Exception ex) {
			logger.info("sendMessage caught an exception: {}", ex.getMessage());
			onMessageSentError(message);
			return false;
		}
	}
	
	private void onMessageSentOK(Message message) {
		logger.debug("Sent Message {}", message.getId());
		
		destinationRepository.setDestinationOnline(message.getDestinationId());
		deleteMessage(message);
	}
	
	private void onMessageSentError(Message message) {
		logger.debug("Unsent Message {}", message.getId());
		
		destinationRepository.setDestinationOffline(message.getDestinationId());
	}
	
	private void deleteMessage(Message message) {
		messageRepository.delete(message.getId());
		
		logger.debug("Deleted Message {}", message.getId());
	}

}
