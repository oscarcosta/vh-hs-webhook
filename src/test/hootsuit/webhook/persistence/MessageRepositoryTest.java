package hootsuit.webhook.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import hootsuit.webhook.model.*;

public class MessageRepositoryTest extends AbstractRepositoryTest {
	
	/**
	 * Test the MessageRepository.findByDestinationOrderByIdAsc() method.
	 */
	@Test
	public void getAllDestinationMessagesOrderedCorrectly() {
		logger.debug("getAllDestinationMessagesCorrectly");
		
		List<Message> googleResult = (List<Message>) messageRepository.findAllByDestinationOrderByIdAsc(googleDest);
		assertThat(googleResult.size()).isEqualTo(2);
		assertThat(googleResult).containsSequence(googleMessage1, googleMessage2);
		
		List<Message> trelloResult = (List<Message>) messageRepository.findAllByDestinationOrderByIdAsc(trelloDest);
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
