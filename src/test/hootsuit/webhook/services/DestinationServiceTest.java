package hootsuit.webhook.services;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import hootsuit.webhook.WebhookConfiguration;
import hootsuit.webhook.model.Destination;
import hootsuit.webhook.model.Message;
import hootsuit.webhook.persistence.DestinationRepository;
import hootsuit.webhook.persistence.MessageRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebhookConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DestinationServiceTest {
	
	private static final Logger logger = LoggerFactory.getLogger(DestinationServiceTest.class);
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getBaseUrl() {
		return "http://localhost:" + port;
	}
	
	@Autowired
	private DestinationRepository destinationRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	private Destination googleDest;
	private Destination trelloDest;
	
	private Message googleMessage1;
	private Message googleMessage2;
	
	@Before
	public void setUp() {
		logger.debug("setUp");
		
		messageRepository.deleteAll();
		destinationRepository.deleteAll();
		
		googleDest = new Destination("http://www.google.com");
		trelloDest = new Destination("http://www.trello.com");
		
		destinationRepository.save(Arrays.asList(googleDest, trelloDest));
		
		googleMessage1 = new Message("#safe=off&q=hootsuite", "text/html", googleDest);
		googleMessage2 = new Message("#safe=off&q=vanhack", "text/html", googleDest);
		
		messageRepository.save(Arrays.asList(googleMessage1, googleMessage2));
	}

	@Test
	public void registerNewDestinationTest() {
		logger.debug("registerNewDestinationTest");
		
		MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
		request.add("url", "http://www.slack.com");
		
		ResponseEntity<String> entity = restTemplate.postForEntity(getBaseUrl() + "/destinations", 
																   request,
																   String.class);
		
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entity.getBody()).isNotNull();
		
		Long id = Long.parseLong(entity.getBody()); 
		assertThat(id).isGreaterThan(0);
	}
	
	@Test
	public void listDestinationsTest() {
		logger.debug("listDestinationsTest");
		
		ResponseEntity<Destination[]> entity = restTemplate.getForEntity(getBaseUrl() + "/destinations", 
																	     Destination[].class);
		
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entity.getBody()).isNotNull();
		assertThat(entity.getBody().length).isGreaterThanOrEqualTo(2);
		assertThat(entity.getBody()).contains(googleDest, trelloDest);
	}
	
	@Test
	public void deleteDestinationTest() {
		logger.debug("deleteDestinationTest");
		
		Map<String, Long> urlVariables = new LinkedHashMap<>();
		urlVariables.put("id", googleDest.getId());
		
		restTemplate.delete(getBaseUrl() + "/destinations/{id}", urlVariables);
		
		// Retrieve the Destination's list to ensure googleDest is not present  
		ResponseEntity<Destination[]> entity = restTemplate.getForEntity(getBaseUrl() + "/destinations", 
			     														 Destination[].class);
		
		assertThat(entity.getBody()).doesNotContain(googleDest);
	}
	
	@Test
	public void postMessageToDestinationTest() {
		logger.debug("postMessageToDestinationTest");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);
		HttpEntity<String> request = new HttpEntity<String>("#safe=off&q=hackathon" ,headers);
		
		Map<String, Long> urlVariables = new LinkedHashMap<>();
		urlVariables.put("id", googleDest.getId());
		
		ResponseEntity<Object> entity = restTemplate.postForEntity(getBaseUrl() + "/destinations/{id}/message", 
																   request,
																   Object.class,
																   urlVariables);
		
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
