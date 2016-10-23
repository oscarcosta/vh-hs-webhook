package com.hootsuit.webhook.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import com.hootsuit.webhook.model.*;

public class MessageRepositoryTest extends AbstractRepositoryTest {
	
	/**
	 * Test the MessageRepository.findAllByDestinationOrderByIdAsc() method.
	 */
	@Test
	public void getAllDestinationMessagesOrderedCorrectly() {
		logger.debug("getAllDestinationMessagesCorrectly");
		
		List<Message> googleResult = messageRepository.findAllByDestinationOrderByIdAsc(googleDest);
		assertThat(googleResult.size()).isEqualTo(2);
		assertThat(googleResult).containsSequence(googleMessage1, googleMessage2);
		
		List<Message> trelloResult = messageRepository.findAllByDestinationOrderByIdAsc(trelloDest);
		assertThat(trelloResult.size()).isEqualTo(0);
	}
	
}
