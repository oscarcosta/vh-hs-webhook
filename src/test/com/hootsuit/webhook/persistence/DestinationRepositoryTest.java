package com.hootsuit.webhook.persistence;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import com.hootsuit.webhook.model.*;

public class DestinationRepositoryTest extends AbstractRepositoryTest {
	
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

}
