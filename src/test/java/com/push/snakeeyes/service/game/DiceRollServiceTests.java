package com.push.snakeeyes.service.game;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.push.snakeeyes.exception.OutcomeRetrievalException;

@SpringBootTest
public class DiceRollServiceTests {
	
	@Autowired
	DiceRollService diceRollService;
	
	@MockBean
	RestTemplate restTemplate;
	
	@Test
	public void givenGetDicesIsCalled_whenReceived2Integers_thenExpectIntArrayOfSize2() {
		
		int expected = 2;
		String testOutput = "1	2";
		
		when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(new ResponseEntity<Object>(testOutput, HttpStatus.OK));
		
		int[] actualOutcome = diceRollService.rollDices();
		
		assertThat(actualOutcome.length, equalTo(expected));
	}
	
	@Test
	public void givenGetDicesIsCalled_whenReceivedNull_thenExpectExceptionThrown() {
		
		String testOutput = null;
		
		when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(new ResponseEntity<Object>(testOutput, HttpStatus.OK));
		
		assertThatThrownBy(() -> {
			diceRollService.rollDices();
		})
		.isInstanceOf(OutcomeRetrievalException.class);
	}
	
	@Test
	public void givenGetDicesIsCalled_whenReceivedEmpty_thenExpectExceptionThrown() {
		
		String testOutput = "";
		
		when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(new ResponseEntity<Object>(testOutput, HttpStatus.OK));
		
		assertThatThrownBy(() -> {
			diceRollService.rollDices();
		})
		.isInstanceOf(OutcomeRetrievalException.class);
	}
}
