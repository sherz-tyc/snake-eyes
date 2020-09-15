package com.push.snakeeyes.service.game;

import java.math.BigDecimal;
import java.util.List;

import com.push.snakeeyes.entity.GameResult;
import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.exception.WinningCalculationException;

public interface WinningService {
	
	/**
	 * Based on the amount of stake and the result obtained from a game submission, calculate
	 * the winning.
	 * 
	 * @param stake amount of stake
	 * @param resultType the result obtained from game submission
	 * @return the amount of winning
	 * @throws WinningCalculationException issue encountered while calculating winning
	 */
	public BigDecimal calculate(double stake, GameResult resultType) throws WinningCalculationException;
	
	/**
	 * Store record of outcome.
	 * 
	 * @param outcome outcome of a game submission
	 * @return
	 */
	public Outcome logWinning(Outcome outcome);
	
	/**
	 * Get all records of outcome for a given player
	 * 
	 * @param playerId target player
	 * @return {@link List} of {@link Outcome} for given player
	 */
	public List<Outcome> getOutcomeLog(long playerId);

}
