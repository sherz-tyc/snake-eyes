package com.push.snakeeyes.service.player;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.exception.PlayerNotFoundException;
import com.push.snakeeyes.repo.PlayerRepo;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class PlayerServiceTests {
	
	@Value("${default.player.balance}")
	private BigDecimal defaultBalance;
	
	@Value("${default.player.name}")
	private String defaultName;
	
	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private PlayerRepo playerRepo;
	
	private static Player testPlayerA;
	private static Player testPlayerB;
	private static Player updatedPlayerA;
	private static Player updatedPlayerB;
	private static Player defaultNewPlayer;
	
	@BeforeAll
	public void setup() {
		ReflectionTestUtils.setField(playerService, "playerRepo", playerRepo);
		
		defaultNewPlayer = new Player(defaultName, defaultBalance);
		
		testPlayerA = new Player(1l, "Test Player A", new BigDecimal(250));
		testPlayerB = new Player(2l, "Test Player B", new BigDecimal(999));
		
		updatedPlayerA = playerRepo.save(testPlayerA);
		updatedPlayerB = playerRepo.save(testPlayerB);
	}
	
	@Test
	@Order(1)
	void givenValidPlayerId_whenPlayerExists_returnPlayer() {
		
		long targetPlayerId = updatedPlayerB.getId();
		Player result = playerService.retrieve(targetPlayerId);
		
		assertThat(result.getName(), comparesEqualTo(testPlayerB.getName()));
		assertThat(result.getBalance(), comparesEqualTo(testPlayerB.getBalance()));
	}
	
	@Test
	@Order(2)
	void givenPlayerId_whenPlayerDoesNotExist_throwPlayerNotFound() {
		
		long targetPlayerId = 333l;
		
		assertThatThrownBy(() -> {
			playerService.retrieve(targetPlayerId);
		})
		.isInstanceOf(PlayerNotFoundException.class)
		.hasMessageMatching(".+");
	}
	
	@Test
	@Order(3)
	void givenPlayerId_whenPlayerIdIsZero_returnNewDefaultPlayer() {
		
		long targetPlayerId = 0l;
		Player result = playerService.retrieve(targetPlayerId);
		
		assertThat(result.getName(), comparesEqualTo(defaultNewPlayer.getName()));
		assertThat(result.getBalance(), comparesEqualTo(defaultNewPlayer.getBalance()));
	}
	
	@Test
	@Order(4)
	void givenPlayerId_whenPlayerIdIsNegativeNumber_returnNewDefaultPlayer() {
		
		long targetPlayerId = -10l;
		Player result = playerService.retrieve(targetPlayerId);
		
		assertThat(result.getName(), comparesEqualTo(defaultNewPlayer.getName()));
		assertThat(result.getBalance(), comparesEqualTo(defaultNewPlayer.getBalance()));
	}
	
	@Test
	@Order(5)
	void givenUpdatedPlayer_whenUpdatePlayer_returnUpdatedValues() {
		
		Player updatePlayer = new Player(updatedPlayerA.getId(), "Test Player A+", new BigDecimal(400));
		Player result = playerService.update(updatePlayer);
		
		assertThat(result.getId(), comparesEqualTo(updatedPlayerA.getId()));
		assertThat(result.getName(), comparesEqualTo(updatePlayer.getName()));
		assertThat(result.getBalance(), comparesEqualTo(updatePlayer.getBalance()));
	}
	
	
	@Test
	@Order(6)
	void givenNewPlayer_whenUpdatePlayer_returnNewlyCreatedPlayer() {
		
		Player updatePlayer = new Player("Test Player C", new BigDecimal(500));
		Player result = playerService.update(updatePlayer);
		
		assertThat(result.getName(), comparesEqualTo(updatePlayer.getName()));
		assertThat(result.getBalance(), comparesEqualTo(updatePlayer.getBalance()));
	}
	
}
