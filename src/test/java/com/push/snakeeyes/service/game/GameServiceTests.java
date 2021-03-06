package com.push.snakeeyes.service.game;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import com.push.snakeeyes.entity.GameResult;
import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.exception.OutcomeRetrievalException;
import com.push.snakeeyes.service.player.PlayerService;

@SpringBootTest(webEnvironment = WebEnvironment.NONE,
	properties = { "dices.api.baseUri=http://localhost:6063",
	"dices.api.uriPath=/integers/?num=2&min=1&max=6&col=2&base=10&format=plain"})
@TestInstance(Lifecycle.PER_CLASS)
public class GameServiceTests {
	
	@Value("${dices.api.uriPath}")
	private String dicesUriPath;
	
	@Autowired
	private GameService gameService;
	
	@MockBean
	private PlayerService mockedPlayerService;
	
	@MockBean
	private WinningService mockedWinningsService;
	
	@MockBean
	private DiceRollService mockedDiceRollService;
	
	Player defaultPlayer;
	Player updatedPlayer;
	
	@BeforeAll
	public void setup() {
		ReflectionTestUtils.setField(gameService, "playerService", mockedPlayerService);
		ReflectionTestUtils.setField(gameService, "winningsService", mockedWinningsService);
		ReflectionTestUtils.setField(gameService, "diceRollService", mockedDiceRollService);
		
		defaultPlayer = new Player("Newbie", new BigDecimal(1000));
		updatedPlayer = new Player(1l, "Newbie", new BigDecimal(1058));
		
	}
	
	@Test
	void givenStakeOf2And1000Balance_whenReceivedDicePairOf1_returnUpdatedBalanceOf1058() {
		
		final ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
		final ArgumentCaptor<Outcome> outcomeCaptor = ArgumentCaptor.forClass(Outcome.class);
		defaultPlayer = new Player("Newbie", new BigDecimal(1000));
		updatedPlayer = new Player(1l, "Newbie", new BigDecimal(1058));
		
		when(mockedDiceRollService.rollDices()).thenReturn(new int[]{1,1});
		when(mockedPlayerService.update(playerCaptor.capture())).thenReturn(updatedPlayer);
		when(mockedWinningsService.logWinning(outcomeCaptor.capture())).thenReturn(new Outcome());
		when(mockedWinningsService.calculate(Mockito.anyDouble(), Mockito.any(GameResult.class)))
			.thenReturn(new BigDecimal(60));
		
		final BigDecimal expectedBalance = new BigDecimal(1058);
		final String expectedPayoutName = "snake eyes";
		final double expectedStake = 2;
		final int expectedDiceA = 1;
		final int expectedDiceB = 1;
		
		gameService.submitAttempt(defaultPlayer, expectedStake);
		
		assertThat(playerCaptor.getValue().getBalance(), comparesEqualTo(expectedBalance));
		assertThat(outcomeCaptor.getValue().getUpdatedBalance(), comparesEqualTo(expectedBalance));
		assertThat(outcomeCaptor.getValue().getStake(), comparesEqualTo(expectedStake));
		assertThat(outcomeCaptor.getValue().getDiceA(), comparesEqualTo(expectedDiceA));
		assertThat(outcomeCaptor.getValue().getDiceB(), comparesEqualTo(expectedDiceB));
		assertThat(outcomeCaptor.getValue().getPayoutName(), comparesEqualTo(expectedPayoutName));
	}
	
	@Test
	void givenStakeOf2And1000Balance_whenReceivedDicePairOf3_returnUpdatedBalanceOf1012() {
		
		final ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
		final ArgumentCaptor<Outcome> outcomeCaptor = ArgumentCaptor.forClass(Outcome.class);
		defaultPlayer = new Player("Newbie", new BigDecimal(1000));
		updatedPlayer = new Player(1l, "Newbie", new BigDecimal(1012));
		
		when(mockedDiceRollService.rollDices()).thenReturn(new int[]{3,3});
		when(mockedPlayerService.update(playerCaptor.capture())).thenReturn(updatedPlayer);
		when(mockedWinningsService.logWinning(outcomeCaptor.capture())).thenReturn(new Outcome());
		when(mockedWinningsService.calculate(Mockito.anyDouble(), Mockito.any(GameResult.class)))
			.thenReturn(new BigDecimal(14));
		
		final BigDecimal expectedBalance = new BigDecimal(1012);
		final String expectedPayoutName = "paired";
		final double expectedStake = 2;
		final int expectedDiceA = 3;
		final int expectedDiceB = 3;
		
		gameService.submitAttempt(defaultPlayer, expectedStake);
		
		assertThat(playerCaptor.getValue().getBalance(), comparesEqualTo(expectedBalance));
		assertThat(outcomeCaptor.getValue().getUpdatedBalance(), comparesEqualTo(expectedBalance));
		assertThat(outcomeCaptor.getValue().getStake(), comparesEqualTo(expectedStake));
		assertThat(outcomeCaptor.getValue().getDiceA(), comparesEqualTo(expectedDiceA));
		assertThat(outcomeCaptor.getValue().getDiceB(), comparesEqualTo(expectedDiceB));
		assertThat(outcomeCaptor.getValue().getPayoutName(), comparesEqualTo(expectedPayoutName));
	}
	
	
	@Test
	void givenStakeOf2And1000Balance_whenReceivedNoMatchingDices_returnUpdatedBalanceOf998() {
		
		final ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
		final ArgumentCaptor<Outcome> outcomeCaptor = ArgumentCaptor.forClass(Outcome.class);
		defaultPlayer = new Player("Newbie", new BigDecimal(1000));
		updatedPlayer = new Player(1l, "Newbie", new BigDecimal(998));
		
		when(mockedDiceRollService.rollDices()).thenReturn(new int[]{4,3});
		when(mockedPlayerService.update(playerCaptor.capture())).thenReturn(updatedPlayer);
		when(mockedWinningsService.logWinning(outcomeCaptor.capture())).thenReturn(new Outcome());
		when(mockedWinningsService.calculate(Mockito.anyDouble(), Mockito.any(GameResult.class)))
			.thenReturn(new BigDecimal(0));
		
		final BigDecimal expectedBalance = new BigDecimal(998);
		final String expectedPayoutName = "none";
		final double expectedStake = 2;
		final int expectedDiceA = 4;
		final int expectedDiceB = 3;
		
		gameService.submitAttempt(defaultPlayer, expectedStake);
		
		assertThat(playerCaptor.getValue().getBalance(), comparesEqualTo(expectedBalance));
		assertThat(outcomeCaptor.getValue().getUpdatedBalance(), comparesEqualTo(expectedBalance));
		assertThat(outcomeCaptor.getValue().getStake(), comparesEqualTo(expectedStake));
		assertThat(outcomeCaptor.getValue().getDiceA(), comparesEqualTo(expectedDiceA));
		assertThat(outcomeCaptor.getValue().getDiceB(), comparesEqualTo(expectedDiceB));
		assertThat(outcomeCaptor.getValue().getPayoutName(), comparesEqualTo(expectedPayoutName));
	}
	
	@Test
	void givenStakeOf2_whenReceivedNoDiceOutcome_thenExpectExceptionThrown() {
		
		when(mockedDiceRollService.rollDices()).thenThrow(new OutcomeRetrievalException());
		final double expectedStake = 2;
		
		assertThatThrownBy(() -> {
			gameService.submitAttempt(defaultPlayer, expectedStake);
		})
		.isInstanceOf(OutcomeRetrievalException.class)
		.hasMessageMatching(".+");
		
	}
	
	@Test
	void givenStakeOf2_whenReceivedInvlidNumberOfDices_thenExpectExceptionThrown() {
		
		when(mockedDiceRollService.rollDices()).thenThrow(new OutcomeRetrievalException());
		final double expectedStake = 2;
		
		assertThatThrownBy(() -> {
			gameService.submitAttempt(defaultPlayer, expectedStake);
		})
		.isInstanceOf(OutcomeRetrievalException.class)
		.hasMessageMatching(".+");
		
	}
}
