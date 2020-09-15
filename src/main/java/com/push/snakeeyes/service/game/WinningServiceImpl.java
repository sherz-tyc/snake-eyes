package com.push.snakeeyes.service.game;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.push.snakeeyes.entity.GameResult;
import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.exception.WinningCalculationException;
import com.push.snakeeyes.repo.OutcomeRepo;

@Service
public class WinningServiceImpl implements WinningService {
	
	@Value("${prize.multiplier.snakeeyes}")
	private BigDecimal snakeEyesMultipler;

	@Value("${prize.multiplier.pair}")
	private BigDecimal normalPairMultipler;
	
	@Value("${prize.multiplier.nomatch}")
	private BigDecimal noMatchMultipler;
	
	@Autowired
	private OutcomeRepo outcomeRepo;

	@Override
	public BigDecimal calculate(double stake, GameResult resultType) throws WinningCalculationException {
		
		if (stake <= 0) {
			throw new WinningCalculationException(stake);
		}
		return new BigDecimal(stake).multiply(determineWinningMultiplier(resultType));
	}
	
	private BigDecimal determineWinningMultiplier(GameResult resultType) {
		// Business Logic for determining winnings, values taken from configuration file.
		switch(resultType) {
			case SNAKE_EYES:
				return snakeEyesMultipler;
			case PAIRED:
				return normalPairMultipler;
			default:
				return noMatchMultipler;
		}
	}

	@Override
	@Transactional
	public Outcome logWinning(Outcome outcome) {
		return outcomeRepo.save(outcome);
	}

	@Override
	@Transactional
	public List<Outcome> getOutcomeLog(long playerId) {
		return outcomeRepo.findByPlayerId(playerId);
	}

}
