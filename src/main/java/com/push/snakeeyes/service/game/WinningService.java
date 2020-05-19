package com.push.snakeeyes.service.game;

import java.math.BigDecimal;
import java.util.List;

import com.push.snakeeyes.entity.GameResult;
import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.exception.WinningCalculationException;

public interface WinningService {
	
	public BigDecimal calculate(double stake, GameResult resultType) throws WinningCalculationException;
	
	public Outcome logWinning(Outcome outcome);
	
	public List<Outcome> getOutcomeLog(long playerId);

}
