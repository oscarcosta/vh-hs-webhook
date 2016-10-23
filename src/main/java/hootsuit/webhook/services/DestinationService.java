package hootsuit.webhook.services;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hootsuit.webhook.events.MessageReceivedEvent;
import hootsuit.webhook.model.Destination;
import hootsuit.webhook.model.Message;
import hootsuit.webhook.persistence.DestinationRepository;
import hootsuit.webhook.persistence.MessageRepository;

@RestController
public class DestinationService implements ApplicationEventPublisherAware {
	
	private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);
	
	@Autowired
	private DestinationRepository destinationRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	private ApplicationEventPublisher applicationEventPublisher;
	
	/**
	 * Register a new destination (URL) returning its id
	 */
	@PostMapping("/destinations")
	public Long registerNewDestination(@RequestParam("url") String url) {
		validateParam(url, "url");
		
		Destination destination = destinationRepository.save(new Destination(url));
		
		logger.debug("Received Destination {}", url);
		
		return destination.getId();
	}
	
	/**
	 * List registered destinations [{id, URL},...]
	 */
	@GetMapping("/destinations")
	public Iterable<Destination> listDestinations() {
		logger.debug("Listing Destinations");
		
		return destinationRepository.findAll();
	}
	
	/**
	 * Delete a destination by id
	 */
	@DeleteMapping("/destinations/{id}")
	public void deleteDestination(@PathVariable("id") Long id) {
		Destination destination = getDestination(id);
		
		destinationRepository.delete(id);
		
		logger.debug("Deleted Destination {}", destination.getUrl());
	}
	
	/**
	 * POST a message to this destination
	 */
	@PostMapping("/destinations/{id}/message")
	public void postMessageToDestination(@PathVariable("id") Long id,
										 @RequestBody String body,
										 @RequestHeader("Content-Type") String contentType) {
		validateParam(body, "body");
		
		Destination destination = getDestination(id);
		
		Message message = messageRepository.save(new Message(body, contentType, destination));
		
		logger.debug("Received Message {} for Destination {}", message.getId(), message.getDestinationUrl());
		
		// Publishes the event of received message
		applicationEventPublisher.publishEvent(new MessageReceivedEvent(this, message));
	}
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	private Destination getDestination(Long id) throws NoSuchElementException {
		Destination destination = destinationRepository.findOne(id);
		if (destination == null) {
			throw new NoSuchElementException("Does not exist destination with ID " + id);
		}
		return destination;
	}
	
	private void validateParam(String param, String paramName) throws IllegalArgumentException {
		if (param == null || param.isEmpty()) {
			throw new IllegalArgumentException("The '" + paramName + "' must not be null or empty");
		}
	}
	
}
