package com.push.snakeeyes.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.exception.InsufficientFundException;
import com.push.snakeeyes.exception.OutcomeRetrievalException;
import com.push.snakeeyes.exception.PlayerNotFoundException;
import com.push.snakeeyes.exception.StakeNotAcceptableException;
import com.push.snakeeyes.service.game.GameService;
import com.push.snakeeyes.service.game.WinningService;
import com.push.snakeeyes.service.player.PlayerService;
import com.push.snakeeyes.service.validation.ValidationService;

@SpringBootTest()
@AutoConfigureMockMvc
public class GameControllerTests {
	
	private static final String BASE_ROUTE = "/snakeeyes";
	private static final String PLAY_ROUTE = "/play?";
	private static final String LOG_ROUTE = "/log?";
	private static final String STAKE_PARAM = "stake=";
	private static final String AND_COND = "&";
	private static final String PLAYERID_PARAM = "playerId=";
	
	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
	private GameService mockedGameService;
	
	@MockBean
	private ValidationService mockedValidationService;
	
	@MockBean
	private PlayerService mockedPlayerService;
	
	@MockBean
	private WinningService mockedWinningService;
	
	@Test
	@DisplayName("/snakeeyes/play?stake=2 - Valid entry")
	void givenStakeOf2AndNoPlayerId_whenEntryIsValid_shouldReturnStatusCode200() throws Exception {
		
		// Mock Input
		final double testStake = 2;
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake);
		
		Outcome mockedOutcome =
				new Outcome(1l, 1l, 1, 1, testStake,LocalDateTime.now(),  new BigDecimal(60), "snake eyes", new BigDecimal(1058));
		Player defaultPlayer = new Player("Newbie", new BigDecimal(1000));
		final ArgumentCaptor<Long> playerIdCaptor = ArgumentCaptor.forClass(Long.class);
		
		// Mock Executions (Service calls are tested separately)
		when(mockedPlayerService.retrieve(playerIdCaptor.capture())).thenReturn(defaultPlayer);
		when(mockedGameService.submitAttempt(any(Player.class), anyDouble())).thenReturn(mockedOutcome);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(pathBuilder.toString())
				.contentType(MediaType.APPLICATION_JSON));
		
		// Verifies and Asserts
		verify(mockedValidationService).validateGameEntry(any(BigDecimal.class), anyDouble());
		
		assertThat(playerIdCaptor.getValue(), comparesEqualTo(0l));
		
		result.andExpect(status().isOk())
			.andExpect(jsonPath("player_id").value(1))
			.andExpect(jsonPath("payout_name").value("snake eyes"))
			.andExpect(jsonPath("winnings").value(new BigDecimal(60)))
			.andExpect(jsonPath("dice1").value(1))
			.andExpect(jsonPath("dice2").value(1));
	}
	

	@Test
	@DisplayName("/snakeeyes/play?stake=2 - Error retrieving Dice outcome from external API")
	void givenStakeOf2AndNoPlayerId_whenDiceOutcomeCannotBeDetermined_shouldReturnStatusCode500() throws Exception {
		
		// Mock Input
		final double testStake = 2;
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake);
		
		Player defaultPlayer = new Player("Newbie", new BigDecimal(1000));
		
		// Mock Executions (Service calls are tested separately)
		when(mockedPlayerService.retrieve(anyLong())).thenReturn(defaultPlayer);
		when(mockedGameService.submitAttempt(any(Player.class), anyDouble())).thenThrow(new OutcomeRetrievalException());
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(pathBuilder.toString())
				.contentType(MediaType.APPLICATION_JSON));
		
		// Verifies and Asserts
		verify(mockedValidationService).validateGameEntry(any(BigDecimal.class), anyDouble());
		
		result.andExpect(status().isInternalServerError());
	}
	
	@Test
	@DisplayName("/snakeeyes/play?stake=2&playerId=2 - Player does not exist")
	void givenStakeOf2AndPlayerIdOf2_whenPlayerDoesNotExist_shouldReturn406StatusCode() throws Exception {
		
		// Mock Input
		final double testStake = 2;
		final long playerId = 2;
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake)
			.append(AND_COND)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		when(mockedPlayerService.retrieve(anyLong())).thenThrow(new PlayerNotFoundException(playerId));
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(pathBuilder.toString())
				.contentType(MediaType.APPLICATION_JSON));
		
		// Verifies and Asserts
		verify(mockedValidationService, never()).validateGameEntry(any(BigDecimal.class), anyDouble());
		
		result.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("/snakeeyes/play?stake=0 - Invalid stake value")
	void givenStakeOf0AndNoPlayerId_whenEntryIsSubmitted_shouldReturn406StatusCode() throws Exception {
		
		// Mock Input
		final double testStake = 0;
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake);
		
		Player defaultPlayer = new Player("Newbie", new BigDecimal(1000));
		final ArgumentCaptor<Long> playerIdCaptor = ArgumentCaptor.forClass(Long.class);
		
		// Mock Executions (Service calls are tested separately)
		when(mockedPlayerService.retrieve(playerIdCaptor.capture())).thenReturn(defaultPlayer);
		doThrow(new StakeNotAcceptableException()).when(mockedValidationService).validateGameEntry(any(BigDecimal.class), anyDouble());
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(pathBuilder.toString())
				.contentType(MediaType.APPLICATION_JSON));
		
		// Verifies and Asserts
		verify(mockedValidationService).validateGameEntry(any(BigDecimal.class), anyDouble());
		
		result.andExpect(status().isNotAcceptable());
	}
	
	@Test
	@DisplayName("/snakeeyes/play?stake=10&playerId=2 - Player with insufficient balance")
	void givenStakeOf10_whenPlayerHasInsufficientBalance_shouldReturn406StatusCode() throws Exception {
		
		// Mock Input
		final double testStake = 10;
		final long playerId = 2l;
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake)
			.append(AND_COND)
			.append(PLAYERID_PARAM)
			.append(playerId);
			
		Player defaultPlayer = new Player(2l, "Player A", new BigDecimal(5));
		final ArgumentCaptor<Long> playerIdCaptor = ArgumentCaptor.forClass(Long.class);
		
		when(mockedPlayerService.retrieve(playerIdCaptor.capture())).thenReturn(defaultPlayer);
		doThrow(new InsufficientFundException()).when(mockedValidationService).validateGameEntry(any(BigDecimal.class), anyDouble());
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(pathBuilder.toString())
				.contentType(MediaType.APPLICATION_JSON));
		
		// Verifies and Asserts
		verify(mockedValidationService).validateGameEntry(any(BigDecimal.class), anyDouble());
		
		result.andExpect(status().isNotAcceptable());
	}
	
	@Test
	@DisplayName("/snakeeyes/log?playerId=10 - Existing Player with logs.")
	void givenPlayerExistsAndHasLogs_whenGetGameLogs_shouldReturnStatusCode200() throws Exception {
		
		// Mock Input
		final long playerId = 10l;
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(LOG_ROUTE)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		List<Outcome> mockedGameLog = new ArrayList<Outcome>();
		mockedGameLog.add(new Outcome(1l,10l,3,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1058)));
		mockedGameLog.add(new Outcome(2l,10l,2,6,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(708)));
		mockedGameLog.add(new Outcome(3l,10l,4,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1056)));
		mockedGameLog.add(new Outcome(4l,10l,3,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(588)));
		mockedGameLog.add(new Outcome(5l,10l,2,4,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1054)));
		
		when(mockedWinningService.getOutcomeLog(anyLong())).thenReturn(mockedGameLog);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(pathBuilder.toString())
				.contentType(MediaType.APPLICATION_JSON));
		
		// Verifies and Asserts
		result.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(5)));
	}
	
	@Test
	@DisplayName("/snakeeyes/log?playerId=5 - Existing Player with no game logs.")
	void givenPlayerExistsAndHasNoLogs_whenGetGameLogs_shouldReturnStatusCode200AndEmptyList() throws Exception {
		
		// Mock Input
		final long playerId = 5l;
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(LOG_ROUTE)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		List<Outcome> mockedGameLog = new ArrayList<Outcome>();
		
		when(mockedWinningService.getOutcomeLog(anyLong())).thenReturn(mockedGameLog);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(pathBuilder.toString())
				.contentType(MediaType.APPLICATION_JSON));
		
		// Verifies and Asserts
		result.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}
}
