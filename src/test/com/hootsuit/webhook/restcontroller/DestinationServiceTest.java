package com.hootsuit.webhook.restcontroller;

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

import com.hootsuit.webhook.WebhookConfiguration;
import com.hootsuit.webhook.model.Destination;
import com.hootsuit.webhook.model.Message;
import com.hootsuit.webhook.persistence.DestinationRepository;
import com.hootsuit.webhook.persistence.MessageRepository;

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
	
	private Destination httpbinDest;
	private Destination posttestDest;
	
	private Message googleMessage1;
	private Message googleMessage2;
	
	@Before
	public void setUp() {
		logger.debug("setUp");
		
		messageRepository.deleteAll();
		destinationRepository.deleteAll();
		
		httpbinDest = new Destination("https://httpbin.org/post");
		posttestDest = new Destination("http://posttestserver.com/post.php?dir=webhook");
		
		destinationRepository.save(Arrays.asList(httpbinDest, posttestDest));
		
		googleMessage1 = new Message("#safe=off&q=hootsuite", "text/plain", httpbinDest);
		googleMessage2 = new Message("#safe=off&q=vanhack", "text/plain", httpbinDest);
		
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
		assertThat(entity.getBody()).contains(httpbinDest, posttestDest);
	}
	
	@Test
	public void deleteDestinationTest() {
		logger.debug("deleteDestinationTest");
		
		Map<String, Long> urlVariables = new LinkedHashMap<>();
		urlVariables.put("id", httpbinDest.getId());
		
		restTemplate.delete(getBaseUrl() + "/destinations/{id}", urlVariables);
		
		// Retrieve the Destination's list to ensure googleDest is not present  
		ResponseEntity<Destination[]> entity = restTemplate.getForEntity(getBaseUrl() + "/destinations", 
			     														 Destination[].class);
		
		assertThat(entity.getBody()).doesNotContain(httpbinDest);
	}
	
	@Test
	public void postMessageToDestinationTest() {
		logger.debug("postMessageToDestinationTest");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);
		HttpEntity<String> request = new HttpEntity<String>("#safe=off&q=hackathon" ,headers);
		
		Map<String, Long> urlVariables = new LinkedHashMap<>();
		urlVariables.put("id", httpbinDest.getId());
		
		ResponseEntity<Object> entity = restTemplate.postForEntity(getBaseUrl() + "/destinations/{id}/message", 
																   request,
																   Object.class,
																   urlVariables);
		
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	public void exceptionsHandlerTest() {
		logger.debug("exceptionsHandlerTest");
		
		MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
		request.add("url", "");
		
		ResponseEntity<String> entity = restTemplate.postForEntity(getBaseUrl() + "/destinations", 
																   request,
																   String.class);
		
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

}
