package com.push.snakeeyes.service.game;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.push.snakeeyes.entity.GameResult;
import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.exception.OutcomeRetrievalException;
import com.push.snakeeyes.service.player.PlayerService;

@Service
public class GameServiceImpl implements GameService {
	
	@Value("${dices.api.baseUri}")
	private String dicesBaseUri;
	
	@Value("${dices.api.uriPath}")
	private String dicesUriPath;
	
	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private WinningService winningsService;
	
	@Autowired
	private DiceRollService diceRollService;

	@Override
	public Outcome submitAttempt(Player player, double stake)  throws OutcomeRetrievalException {

		int[] result = diceRollService.rollDices();
		GameResult resultType = determineResult(result);
		
		BigDecimal winnings = winningsService.calculate(stake, resultType);
		
		// Deduct stake from balance
		BigDecimal currentBalance = player.getBalance().subtract(new BigDecimal(stake));
		
		// Add winnings to current balance
		player.setBalance(currentBalance.add(winnings));
		
		player = playerService.update(player);
		
		Outcome outcome = winningsService.logWinning(
				populateOutcome(result, stake, resultType, winnings, player));
		
		return outcome;
	}

	private Outcome populateOutcome(int[] result, double stake, GameResult resultType, 
			BigDecimal winnings, Player player) {
		
		Outcome outcome = new Outcome();
		outcome.setAttemptedAt(LocalDateTime.now());
		outcome.setDiceA(result[0]);
		outcome.setDiceB(result[1]);
		outcome.setStake(stake);
		outcome.setWinnings(winnings);
		outcome.setPayoutName(resultType.toName());
		outcome.setPlayerId(player.getId());
		outcome.setUpdatedBalance(player.getBalance());
		
		return outcome;
	}
	
	private GameResult determineResult(int[] result) {
		if (result.length != 2) {
			throw new OutcomeRetrievalException();
		}
		
		// Business Logic for determining game result
		if (result[0] == result[1]) {
			if (result[0] == 1) {
				return GameResult.SNAKE_EYES;
			}
			return GameResult.PAIRED;
		} else {
			return GameResult.NONE;
		}
		
	}

}
