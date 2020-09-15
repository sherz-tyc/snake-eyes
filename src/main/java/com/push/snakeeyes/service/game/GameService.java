package com.push.snakeeyes.service.game;

import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.exception.OutcomeRetrievalException;

public interface GameService {
	
	/**
	 * Submit a game entry in order to obtain an outcome.
	 * 
	 * @param player given player
	 * @param stake amount of stake
	 * @return outcome of the game submission
	 * @throws OutcomeRetrievalException issue encountered whilst retrieving outcome for game
	 * submission.
	 */
	public Outcome submitAttempt(Player player, double stake) throws OutcomeRetrievalException;

}
