package com.push.snakeeyes.service.validation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.push.snakeeyes.exception.InsufficientFundException;
import com.push.snakeeyes.exception.StakeNotAcceptableException;

@SpringBootTest
public class ValidationServiceTests {
	
	@Value("${validation.stake.options}")
	private double[] allowedStakeArr;
	
	@Autowired
	private ValidationService validationService;
	
	
	@Test
	void givenAcceptableStakeAndSufficientBalance_thenExpectNoExceptionThrown() {
		
		for (double stake: allowedStakeArr) {
			validationService.validateGameEntry(new BigDecimal(1000), stake);
		}
		assertThat("No Exception Thrown - Pass!", true);
	}
	
	@Test
	void givenAcceptableStakeAndInsufficientBalance_thenExpectInsufficentFundExceptionThrown() {
		
		assertThatThrownBy(() -> {
			validationService.validateGameEntry(BigDecimal.ZERO, allowedStakeArr[0]);
		})
		.isInstanceOf(InsufficientFundException.class)
		.hasMessageMatching(".+");
	}
	
	@Test
	void givenInvalidStakeAndSufficientBalance_thenExpectStakeNotAcceptableExceptionThrown() {
		
		// Adding the accepted stakes ensures it becomes a non-acceptable stake
		double invalidStake = Arrays.stream(allowedStakeArr).sum();
		
		assertThatThrownBy(() -> {
			validationService.validateGameEntry(new BigDecimal(5000), invalidStake);
		})
		.isInstanceOf(StakeNotAcceptableException.class)
		.hasMessageMatching(".+");
	}
	
	@Test
	void givenZeroStakeAndSufficientBalance_thenExpectStakeNotAcceptableExceptionThrown() {
		
		assertThatThrownBy(() -> {
			validationService.validateGameEntry(new BigDecimal(1000), 0);
		})
		.isInstanceOf(StakeNotAcceptableException.class)
		.hasMessageMatching(".+");
	}
	
	
}
