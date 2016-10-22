package hootsuit.webhook.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
	 * Test the relation between Destination and Message
	 */
	@Test
	public void deleteDestinationCorrectly() {
		destinationRepository.delete(googleDest.getId());

		// Tries to find googleDest to ensure it was deleted   
		Destination result = destinationRepository.findOne(googleDest.getId());
		assertThat(result).isNull();
	}

	/**
	 * Test the MessageRepository.findByDestination() method.
	 */
	@Test
	public void getAllDestinationMessagesCorrectly() {
		List<Message> googleResult = (List<Message>) messageRepository.findByDestinationOrderByIdAsc(googleDest);
		assertThat(googleResult.size()).isEqualTo(2);
		assertThat(googleResult).contains(googleMessage1, googleMessage2);
		
		List<Message> trelloResult = (List<Message>) messageRepository.findByDestinationOrderByIdAsc(trelloDest);
		assertThat(trelloResult.size()).isEqualTo(0);
	}

}
