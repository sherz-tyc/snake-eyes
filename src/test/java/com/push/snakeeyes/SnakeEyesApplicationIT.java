package com.push.snakeeyes;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.JsonConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.repo.OutcomeRepo;
import com.push.snakeeyes.repo.PlayerRepo;

/**
 * This is the main Integration Test that covers the functions and cycle of
 * the Snake Eyes Game, from Controller, to Service, to Repository.
 */
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
properties={"dices.api.baseUri=http://localhost:6064",
"dices.api.uriPath=/integers/?num=2&min=1&max=6&col=2&base=10&format=plain"})
@TestInstance(Lifecycle.PER_CLASS)
@Transactional
@DisplayName("Snake Eyes Application Integration Testing")
class SnakeEyesApplicationIT {
	
	public static WireMockServer wiremock = new WireMockServer(6064);
	
	@Value("${dices.api.uriPath}")
	private String dicesUriPath;
	
	private static final String BASE_ROUTE = "/snakeeyes";
	private static final String PLAY_ROUTE = "/play?";
	private static final String LOG_ROUTE = "/log?";
	private static final String STAKE_PARAM = "stake=";
	private static final String AND_COND = "&";
	private static final String PLAYERID_PARAM = "playerId=";
	
	private static final String PAYOUT_SNAKEEYES = "snake eyes";
	private static final String PAYOUT_PAIR = "paired";
	private static final String PAYOUT_NONE = "none";
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private PlayerRepo playerRepo;
	
	@Autowired
	private OutcomeRepo outcomeRepo;
	
	private Player playerBalanceOfTen_A; 
	private Player playerBalanceOfTen_B; 
	private Player playerBalanceOfFive_A; 
	private Player playerBalanceOfFive_B; 
	private Player playerBalanceOfZero; 
	
	@BeforeAll
	@Transactional
    public void seedData() {
		
		// Players with different balances:
		playerBalanceOfTen_A = playerRepo.save(new Player("Player A", new BigDecimal(10)));
		playerBalanceOfTen_B = playerRepo.save(new Player("Player B", new BigDecimal(10)));
		playerBalanceOfFive_A = playerRepo.save(new Player("Player C", new BigDecimal(5)));
		playerBalanceOfFive_B = playerRepo.save(new Player("Player C", new BigDecimal(5)));
		playerBalanceOfZero = playerRepo.save(new Player("Player D", new BigDecimal(0)));
		
		List<Outcome> mockedGameLog = new ArrayList<Outcome>();
		mockedGameLog.add(new Outcome(1l,34l,3,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1058)));
		mockedGameLog.add(new Outcome(2l,23l,2,6,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(708)));
		mockedGameLog.add(new Outcome(3l,34l,4,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1056)));
		mockedGameLog.add(new Outcome(4l,34l,3,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1054)));
		mockedGameLog.add(new Outcome(5l,10l,2,4,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(588)));
		mockedGameLog.add(new Outcome(4l,34l,3,5,2,LocalDateTime.now(),new BigDecimal(0),"none",new BigDecimal(1052)));
		
		outcomeRepo.saveAll(mockedGameLog);
		
		RestAssured.config = RestAssured.config()
				.jsonConfig(JsonConfig.jsonConfig().numberReturnType(NumberReturnType.DOUBLE));
		RestAssured.defaultParser = Parser.JSON;
		RestAssured.port = port;
		wiremock.start();
		
	}
	
	@Test
	@DisplayName("Scenario: New Player submits an attempt and hit Snake Eyes (pair of value 1)")
	public void givenBalanceOf1000AndStakeOf2_whenOutcomeIsPairOf1_thenReturnBalance1058() {
		
		final int testStake = 2;
		final int expectedWinnings = 60;
		final int expectedUpdatedBalance = 1058;
		final int expectedDice1 = 1;
		final int expectedDice2 = 1;
		final String expectedPayoutName = PAYOUT_SNAKEEYES;
		
		// URL = /snakeeyes/play?stake=2
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake);
		
		wiremock.stubFor(get(urlEqualTo(dicesUriPath)).willReturn(aResponse()
			.withHeader("Content-Type", ContentType.TEXT.toString())
			.withBody("1	1")));
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_OK))
			.assertThat().body("winnings", is(expectedWinnings))
			.assertThat().body("payout_name", is(expectedPayoutName))
			.assertThat().body("updated_balance", is(expectedUpdatedBalance))
			.assertThat().body("dice1", is(expectedDice1))
			.assertThat().body("dice2", is(expectedDice2));
	}
	
	@Test
	@DisplayName("Scenario: New Player submits an attempt and hit a pair that is not value of 1")
	public void givenBalanceOf1000AndStakeOf2_whenOutcomeIsPairOf3_thenReturnBalance1012() {
		
		final int testStake = 2;
		final int expectedWinnings = 14;
		final int expectedUpdatedBalance = 1012;
		final int expectedDice1 = 3;
		final int expectedDice2 = 3;
		final String expectedPayoutName = PAYOUT_PAIR;
		
		// URL = /snakeeyes/play?stake=2
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake);
		
		wiremock.stubFor(get(urlEqualTo(dicesUriPath)).willReturn(aResponse()
			.withHeader("Content-Type", ContentType.TEXT.toString())
			.withBody("3	3")));
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_OK))
			.assertThat().body("winnings", is(expectedWinnings))
			.assertThat().body("payout_name", is(expectedPayoutName))
			.assertThat().body("updated_balance", is(expectedUpdatedBalance))
			.assertThat().body("dice1", is(expectedDice1))
			.assertThat().body("dice2", is(expectedDice2));
	}
	
	@Test
	@DisplayName("Scenario: New Player submits an attempt and did not hit a pair")
	public void givenBalanceOf1000AndStakeOf2_whenOutcomeIs2And5_thenReturnBalance998() {
		
		final int testStake = 2;
		final int expectedWinnings = 0;
		final int expectedUpdatedBalance = 998;
		final int expectedDice1 = 2;
		final int expectedDice2 = 5;
		final String expectedPayoutName = PAYOUT_NONE;
		
		// URL = /snakeeyes/play?stake=2
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake);
		
		wiremock.stubFor(get(urlEqualTo(dicesUriPath)).willReturn(aResponse()
			.withHeader("Content-Type", ContentType.TEXT.toString())
			.withBody("2	5")));
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_OK))
			.assertThat().body("winnings", is(expectedWinnings))
			.assertThat().body("payout_name", is(expectedPayoutName))
			.assertThat().body("updated_balance", is(expectedUpdatedBalance))
			.assertThat().body("dice1", is(expectedDice1))
			.assertThat().body("dice2", is(expectedDice2));
	}

	@Test
	@Transactional
	@DisplayName("Scenario: Player exhausts his balance with an attempt and did not hit a pair")
	public void givenBalanceOf10AndStakeOf10_whenOutcomeIs2And5_thenReturnBalance0() {
		
		final int testStake = 10;
		final long playerId = playerBalanceOfTen_A.getId();
		final int expectedWinnings = 0;
		final double expectedUpdatedBalance = 0;
		final int expectedDice1 = 2;
		final int expectedDice2 = 5;
		final String expectedPayoutName = PAYOUT_NONE;
		
		// URL = /snakeeyes/play?stake=10&playerId=1
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake)
			.append(AND_COND)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		wiremock.stubFor(get(urlEqualTo(dicesUriPath)).willReturn(aResponse()
			.withHeader("Content-Type", ContentType.TEXT.toString())
			.withBody("2	5")));
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_OK))
			.assertThat().body("winnings", is(expectedWinnings))
			.assertThat().body("payout_name", is(expectedPayoutName))
			.assertThat().body("updated_balance", is(expectedUpdatedBalance))
			.assertThat().body("dice1", is(expectedDice1))
			.assertThat().body("dice2", is(expectedDice2));
		
	}
	
	@Test
	@DisplayName("Scenario: Player exhausts his balance with an attempt and hit Snake Eyes (pair of value 1)")
	public void givenBalanceOf10AndStakeOf10_whenOutcomeIsPairOf1_thenReturnBalance300() {
		
		final int testStake = 10;
		final long playerId = playerBalanceOfTen_B.getId();
		final int expectedWinnings = 300;
		final double expectedUpdatedBalance = 300;
		final int expectedDice1 = 1;
		final int expectedDice2 = 1;
		final String expectedPayoutName = PAYOUT_SNAKEEYES;
		
		// URL = /snakeeyes/play?stake=10&playerId=2
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake)
			.append(AND_COND)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		wiremock.stubFor(get(urlEqualTo(dicesUriPath)).willReturn(aResponse()
			.withHeader("Content-Type", ContentType.TEXT.toString())
			.withBody("1	1")));
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_OK))
			.assertThat().body("winnings", is(expectedWinnings))
			.assertThat().body("payout_name", is(expectedPayoutName))
			.assertThat().body("updated_balance", is(expectedUpdatedBalance))
			.assertThat().body("dice1", is(expectedDice1))
			.assertThat().body("dice2", is(expectedDice2));
	}

	@Test
	@DisplayName("Scenario: Player with balance of 5 attempt to play game with stake of 10")
	public void givenBalanceOf5AndStakeOf10_whenSumbitAttempt_thenReturnStatusCode406() {
		
		final int testStake = 10;
		final long playerId = playerBalanceOfFive_A.getId();
		
		// URL = /snakeeyes/play?stake=10&playerId=3
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake)
			.append(AND_COND)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_NOT_ACCEPTABLE));
	}
	

	@Test
	@DisplayName("Scenario: Player with balance of 5 attempt to play game with stake of 2")
	public void givenBalanceOf5AndStakeOf2_whenSumbitAttempt_thenReturnStatusCode200() {
		
		final int testStake = 2;
		final long playerId = playerBalanceOfFive_B.getId();
		final int expectedWinnings = 14;
		final double expectedUpdatedBalance = 17;
		final int expectedDice1 = 6;
		final int expectedDice2 = 6;
		final String expectedPayoutName = PAYOUT_PAIR;
		
		// URL = /snakeeyes/play?stake=2&playerId=3
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake)
			.append(AND_COND)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		wiremock.stubFor(get(urlEqualTo(dicesUriPath)).willReturn(aResponse()
			.withHeader("Content-Type", ContentType.TEXT.toString())
			.withBody("6	6")));
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_OK))
			.assertThat().body("winnings", is(expectedWinnings))
			.assertThat().body("payout_name", is(expectedPayoutName))
			.assertThat().body("updated_balance", is(expectedUpdatedBalance))
			.assertThat().body("dice1", is(expectedDice1))
			.assertThat().body("dice2", is(expectedDice2));
	}
	
	@Test
	@DisplayName("Scenario: Player with balance of 0 attempt to play game with stake of 1")
	public void givenBalanceOf0AndStakeOf1_whenSumbitAttempt_thenReturnStatusCode406() {
		
		final int testStake = 1;
		final long playerId = playerBalanceOfZero.getId();
		
		// URL = /snakeeyes/play?stake=1&playerId=4
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(PLAY_ROUTE)
			.append(STAKE_PARAM)
			.append(testStake)
			.append(AND_COND)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_NOT_ACCEPTABLE));
	}

	@Test
	@Transactional
	@DisplayName("Scenario: Retrieve game log for player with 4 previous attempts")
	public void givenPlayerHas4GameLogs_whenRetrieveLogs_thenReturnStatusCode200AndLogs() {
		
		final int playerId = 34;
		
		// URL = /snakeeyes/log?playerId=34
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(LOG_ROUTE)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_OK))
			.assertThat().body("$", hasSize(4));
	}
	

	@Test
	@Transactional
	@DisplayName("Scenario: Retrieve game log for player with no previous attempts")
	public void givenPlayerHas0GameLogs_whenRetrieveLogs_thenReturnStatusCode200AndEmptyLog() {
		
		final int playerId = 99;
		
		// URL = /snakeeyes/log?playerId=99
		StringBuilder pathBuilder = new StringBuilder(BASE_ROUTE);
		pathBuilder.append(LOG_ROUTE)
			.append(PLAYERID_PARAM)
			.append(playerId);
		
		given().contentType(ContentType.JSON).accept(ContentType.JSON)
			.when().get(pathBuilder.toString()).then()
			.assertThat().statusCode(equalTo(HttpStatus.SC_OK))
			.assertThat().body("$", hasSize(0));
	}
	
	@AfterEach
	void after() {
		wiremock.resetAll();
		playerRepo.deleteAll();
	}

	@AfterAll
	void clean() {
		wiremock.shutdown();
		playerRepo.deleteAll();
	}

}
