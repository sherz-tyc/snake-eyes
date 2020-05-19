package com.push.snakeeyes.service.game;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import com.push.snakeeyes.entity.GameResult;
import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.exception.WinningCalculationException;
import com.push.snakeeyes.repo.OutcomeRepo;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestInstance(Lifecycle.PER_CLASS)
public class WinningServiceTests {
	
	@Value("${prize.multiplier.snakeeyes}")
	private BigDecimal snakeEyesMultipler;

	@Value("${prize.multiplier.pair}")
	private BigDecimal normalPairMultipler;
	
	@Value("${prize.multiplier.nomatch}")
	private BigDecimal noMatchMultipler;
	
	@Autowired
	private WinningService winningService;
	
	@Autowired
	private OutcomeRepo outcomeRepo;
	
	@BeforeAll
	public void setup() {
		ReflectionTestUtils.setField(winningService, "outcomeRepo", outcomeRepo);
		
		// Seed data
		List<Outcome> outcomeLog = new ArrayList<Outcome>();
		outcomeLog.add(new Outcome(1l,10l,3,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1058)));
		outcomeLog.add(new Outcome(2l,3l,2,6,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(708)));
		outcomeLog.add(new Outcome(3l,10l,4,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1056)));
		outcomeLog.add(new Outcome(4l,4l,3,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(588)));
		outcomeLog.add(new Outcome(5l,10l,2,4,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1054)));
		outcomeRepo.saveAll(outcomeLog);
	}
	
	@Test
	void givenStakeOf2_whenHitSnakeEyes_return60() {
		
		double stake = 2d;
		BigDecimal expected = new BigDecimal(60);
		BigDecimal actual = winningService.calculate(stake, GameResult.SNAKE_EYES);
		
		assertThat(actual, comparesEqualTo(expected));
	}
	
	@Test
	void givenStakeOf2_whenHitANormalPair_return14() {
		
		double stake = 2d;
		BigDecimal expected = new BigDecimal(14);
		BigDecimal actual = winningService.calculate(stake, GameResult.PAIRED);
		
		assertThat(actual, comparesEqualTo(expected));
	}
	
	@Test
	void givenStakeOf2_whenNoMatcingDices_return0() {
		
		double stake = 2d;
		BigDecimal expected = new BigDecimal(0);
		BigDecimal actual = winningService.calculate(stake, GameResult.NONE);
		
		assertThat(actual, comparesEqualTo(expected));
	}
	
	@Test
	void givenStakeOf0_whenHitSnakeEyes_thenExpectWinningCalculationExceptionThrown() {
		
		double stake = 0;
		assertThatThrownBy(() -> {
			winningService.calculate(stake, GameResult.SNAKE_EYES);
		})
		.isInstanceOf(WinningCalculationException.class)
		.hasMessageMatching(".+");
	}
	
	@Test
	@Transactional
	void givenNewOutcome_whenPerformLogOutcome_thenExpectOutcomeIsSaved() {
		
		Outcome expectedOutcome = new Outcome();
		expectedOutcome.setDiceA(1);
		expectedOutcome.setDiceB(1);
		expectedOutcome.setPayoutName("snake_eyes");
		expectedOutcome.setPlayerId(1l);
		expectedOutcome.setStake(10);
		expectedOutcome.setWinnings(new BigDecimal(300));
		
		Outcome actual = winningService.logWinning(expectedOutcome);
		
//		Outcome inspectOutcome = outcomeRepo.findById(1l).orElse(null);
//		if (inspectOutcome == null) assertThat("Did not retreive expected outcome.", false);
		assertThat(actual.getDiceA(), comparesEqualTo(expectedOutcome.getDiceA()));
		assertThat(actual.getDiceB(), comparesEqualTo(expectedOutcome.getDiceB()));
		assertThat(actual.getPayoutName(), comparesEqualTo(expectedOutcome.getPayoutName()));
		assertThat(actual.getPlayerId(), comparesEqualTo(expectedOutcome.getPlayerId()));
		assertThat(actual.getStake(), comparesEqualTo(expectedOutcome.getStake()));
		assertThat(actual.getWinnings(), comparesEqualTo(expectedOutcome.getWinnings()));
	}
	
	@Test
	@Transactional
	void given3ExistingLogsForPlayerId10_whenPerformGetLogsForPlayer_thenExpectListOfSize3() {
		
		long targetPlayerId = 10l;
		
		List<Outcome> actualOutcomeLog = winningService.getOutcomeLog(targetPlayerId);
		
		assertThat(actualOutcomeLog.size(), comparesEqualTo(3));
	}
	
	@Test
	@Transactional
	void given0ExistingLogsForPlayerId99_whenPerformGetLogsForPlayer_thenExpectEmptyList() {
		
		long targetPlayerId = 99l;
		
		List<Outcome> actualOutcomeLog = winningService.getOutcomeLog(targetPlayerId);
		
		assertThat(actualOutcomeLog.size(), comparesEqualTo(0));
	}
	
	@AfterAll
	public void afterAll() {
		outcomeRepo.deleteAll();
	}

}
