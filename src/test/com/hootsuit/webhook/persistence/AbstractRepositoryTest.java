package com.hootsuit.webhook.persistence;

import java.util.Arrays;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hootsuit.webhook.model.Destination;
import com.hootsuit.webhook.model.Message;
import com.hootsuit.webhook.persistence.DestinationRepository;
import com.hootsuit.webhook.persistence.MessageRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public abstract class AbstractRepositoryTest {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	DestinationRepository destinationRepository;
	
	@Autowired
	MessageRepository messageRepository;
	
	Destination googleDest;
	Destination trelloDest;
	
	Message googleMessage1;
	Message googleMessage2;
	
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

}
