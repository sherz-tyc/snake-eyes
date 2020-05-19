package com.push.snakeeyes.service.game;

import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.exception.OutcomeRetrievalException;

public interface GameService {
	
	public Outcome submitAttempt(Player player, double stake) throws OutcomeRetrievalException;

}
