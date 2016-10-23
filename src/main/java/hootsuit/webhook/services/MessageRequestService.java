package hootsuit.webhook.services;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import hootsuit.webhook.model.Message;
import hootsuit.webhook.persistence.DestinationRepository;
import hootsuit.webhook.persistence.MessageRepository;

@Service
public class MessageRequestService {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageRequestService.class);
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private DestinationRepository destinationRepository;
	
	private final RestTemplate restTemplate;
	
	public MessageRequestService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Async
	public Future<Message> sendMessage(Message message) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE, message.getContentType());
		HttpEntity<String> request = new HttpEntity<>(message.getMessageBody() ,headers);
		
		logger.debug("Sending Message {} to Destination {}", message.getId(), message.getDestinationUrl());
		
		ResponseEntity<String> entity = restTemplate.postForEntity(message.getDestinationUrl(), request, String.class);
		
		if (entity.getStatusCode().equals(HttpStatus.OK)) {
			onMessageSent(message);
		} else {
			onMessageUnsent(message);
		}
		
		return new AsyncResult<>(message);
	}
	
	private void onMessageSent(Message message) {
		logger.debug("Sent Message {}", message.getId());
		
		destinationRepository.setDestinationOnline(message.getDestinationId());
		deleteMessage(message);
	}
	
	private void onMessageUnsent(Message message) {
		logger.debug("Unsent Message {}", message.getId());
		
		destinationRepository.setDestinationOffline(message.getDestinationId());
	}
	
	private void deleteMessage(Message message) {
		messageRepository.delete(message.getId());
		
		logger.debug("Deleted Message {}", message.getId());
	}

}
