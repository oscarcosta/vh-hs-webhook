package hootsuit.webhook.services;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hootsuit.webhook.model.Destination;
import hootsuit.webhook.model.Message;
import hootsuit.webhook.persistence.DestinationRepository;
import hootsuit.webhook.persistence.MessageRepository;

@RestController
public class DestinationService {
	
	@Autowired
	private DestinationRepository destinationRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@PostMapping("/destinations")
	public Long registerNewDestination(@RequestParam("url") String url) {
		validateParam(url, "url");
		
		Destination destination = destinationRepository.save(new Destination(url));
		return destination.getId();
	}
	
	@GetMapping("/destinations")
	public Iterable<Destination> listDestinations() {
		return destinationRepository.findAll();
	}
	
	@DeleteMapping("/destinations/{id}")
	public void deleteDestination(@PathVariable("id") Long id) {
		Destination destination = destinationRepository.findOne(id);
		if (destination == null) {
			throw new NoSuchElementException("Does not exist destination with id " + id);
		}
		
		destinationRepository.delete(id);
	}
	
	@PostMapping("/destinations/{id}/message")
	public void postMessageToDestination(@PathVariable("id") Long id, 
										 @RequestBody String body,
										 @RequestHeader("Content-Type") String contentType) {
		validateParam(body, "body");
		
		Destination destination = destinationRepository.findOne(id);
		if (destination == null) {
			throw new NoSuchElementException("Does not exist destination with id " + id);
		}
		
		messageRepository.save(new Message(body, contentType, destination));
		// TODO POST the message in the destination
	}
	
	private void validateParam(String param, String paramName) {
		if (param == null || param.isEmpty()) {
			throw new IllegalArgumentException("The '" + paramName + "' must not be null or empty");
		}
	}
	
}
