package hootsuit.webhook.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hootsuit.webhook.model.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PersistenceTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceTest.class);
	
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
	
	/**
	 * Test the DestinationRepository.DestinationOffline() method.
	 */
	@Test
	public void setDestinationOnlineOfflineTest() {
		logger.debug("setDestinationOnlineTest");
		
		destinationRepository.setDestinationOffline(trelloDest.getId());
		
		// Retrieve trelloDest to check the online flag    
		Destination result = destinationRepository.findOne(trelloDest.getId());
		assertThat(result.isOnline()).isFalse();
		
		destinationRepository.setDestinationOnline(trelloDest.getId());
		
		// Retrieve trelloDest to check the online flag    
		result = destinationRepository.findOne(trelloDest.getId());
		assertThat(result.isOnline()).isTrue();
	}
	
	/**
	 * Test the relation between Destination and Message
	 */
	@Test
	public void deleteDestinationCorrectly() {
		logger.debug("deleteDestinationCorrectly");
		
		destinationRepository.delete(googleDest.getId());

		// Tries to find googleDest to ensure it was deleted   
		Destination result = destinationRepository.findOne(googleDest.getId());
		assertThat(result).isNull();
	}

	/**
	 * Test the MessageRepository.findByDestinationOrderByIdAsc() method.
	 */
	@Test
	public void getAllDestinationMessagesOrderedCorrectly() {
		logger.debug("getAllDestinationMessagesCorrectly");
		
		List<Message> googleResult = (List<Message>) messageRepository.findByDestinationOrderByIdAsc(googleDest);
		assertThat(googleResult.size()).isEqualTo(2);
		assertThat(googleResult).containsSequence(googleMessage1, googleMessage2);
		
		List<Message> trelloResult = (List<Message>) messageRepository.findByDestinationOrderByIdAsc(trelloDest);
		assertThat(trelloResult.size()).isEqualTo(0);
	}
	
	/**
	 * Test the MessageRepository.findAllByOrderByTimestampAsc() method.
	 */
	@Test
	public void gellAllMessagesOrderedCorrectly() {
		logger.debug("gellAllMessagesOrdered");
		
		List<Message> result = (List<Message>) messageRepository.findAllByOrderByTimestampAsc();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result).containsSequence(googleMessage1, googleMessage2);
	}

}
