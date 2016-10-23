package com.hootsuit.webhook.services;

import java.util.Date;
import java.util.List;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hootsuit.webhook.events.MessageReceivedEvent;
import com.hootsuit.webhook.model.Destination;
import com.hootsuit.webhook.model.Message;
import com.hootsuit.webhook.persistence.DestinationRepository;
import com.hootsuit.webhook.persistence.MessageRepository;

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
	
	private void processMessagesForDestination(Destination destination) {
		try {
			logger.debug("Processing messages for Destination {}", destination.getUrl());
			
			destinationRepository.setDestinationOnline(destination.getId());
			
			List<Message> messages = messageRepository.findAllByDestinationOrderByIdAsc(destination);
			for (Message message : messages) {
				if (message.isMessageTimeout()) {
					deleteMessage(message);
				} else {
					sendMessage(message);
				}
			}
		} catch (MessageProcessorException ex) {
			logger.info("processMessagesForDestination caught an exception: {}", ex.getMessage());
		}
	}
	
	private void sendMessage(Message message) throws MessageProcessorException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set(HttpHeaders.CONTENT_TYPE, message.getContentType());
			HttpEntity<String> request = new HttpEntity<>(message.getMessageBody() ,headers);
			
			Thread.sleep(500); // wait 0.5 second before send message
			
			logger.debug("Sending Message {} to Destination {}", message.getId(), message.getDestinationUrl());
			
			ResponseEntity<String> entity = restTemplate.postForEntity(message.getDestinationUrl(), request, String.class);
			
			if (entity.getStatusCode().equals(HttpStatus.OK)) {
				onSendMessageSuccess(message);
			} else {
				throw new MessageProcessorException("Non 200 HTTP response code!");
			}
		} catch (Exception ex) {
			logger.info("sendMessage caught an exception: {}", ex.getMessage());
			
			onSendMessageError(message);
			throw new MessageProcessorException(ex.getMessage());
		}
	}
	
	private void onSendMessageSuccess(Message message) {
		logger.debug("Sent Message {}", message.getId());
		
		deleteMessage(message);
	}
	
	private void onSendMessageError(Message message) {
		logger.debug("Unsent Message {}", message.getId());
		
		destinationRepository.setDestinationOffline(message.getDestinationId());
	}
	
	private void deleteMessage(Message message) {
		messageRepository.delete(message.getId());
		
		logger.debug("Deleted Message {}", message.getId());
	}

}
