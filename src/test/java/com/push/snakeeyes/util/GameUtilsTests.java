package com.push.snakeeyes.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.push.snakeeyes.exception.OutcomeRetrievalException;

@SpringBootTest
public class GameUtilsTests {
	
	@Test
	public void givenInputStringWith2Integers_returnIntArrayOfSize2() {
		String testInput = "1	2";
		int[] expectedResult = {1,2};
		
		int[] actual = GameUtils.processDicesOutcome(testInput);
		
		assertThat(actual.length, comparesEqualTo(expectedResult.length));
		assertThat(actual, is(expectedResult));
	}
	
	@Test
	public void givenInputStringWith2IntegersAndSpecialChar_returnIntArrayOfSize2() {
		String testInput = "3&^%	^&$£3";
		int[] expectedResult = {3,3};
		
		int[] actual = GameUtils.processDicesOutcome(testInput);
		
		assertThat(actual.length, comparesEqualTo(expectedResult.length));
		assertThat(actual, is(expectedResult));
	}
	
	@Test
	public void givenInputStringWithOnly1IntegersAndSpecialChar_thenExpectExceptionThrown() {
		String testInput = "3	";
		
		assertThatThrownBy(() -> {
			GameUtils.processDicesOutcome(testInput);
		})
		.isInstanceOf(OutcomeRetrievalException.class)
		.hasMessageMatching(".+");
	}
	
	@Test
	public void givenInputStringWithIntegersMoreThanValueOf6_thenExpectExceptionThrown() {
		String testInput = "7	10";
		
		assertThatThrownBy(() -> {
			GameUtils.processDicesOutcome(testInput);
		})
		.isInstanceOf(OutcomeRetrievalException.class)
		.hasMessageMatching(".+");
	}
	
	@Test
	public void givenInputStringWithIntegerValueOf0_thenExpectExceptionThrown() {
		String testInput = "0	0";
		
		assertThatThrownBy(() -> {
			GameUtils.processDicesOutcome(testInput);
		})
		.isInstanceOf(OutcomeRetrievalException.class)
		.hasMessageMatching(".+");
	}
	
	@Test
	public void givenInputStringWithNegativeIntegers_returnPositiveIntegers() {
		String testInput = "-3	-6";
		
		int[] expectedResult = {3,6};
		
		int[] actual = GameUtils.processDicesOutcome(testInput);
		
		assertThat(actual.length, comparesEqualTo(expectedResult.length));
		assertThat(actual, is(expectedResult));
	}
	
	@Test
	public void givenInputStringWithNegativeIntegers_thenExpectExceptionThrown() {
		String testInput = "-7	-7";
		
		assertThatThrownBy(() -> {
			GameUtils.processDicesOutcome(testInput);
		})
		.isInstanceOf(OutcomeRetrievalException.class)
		.hasMessageMatching(".+");
	}
	
	@Test
	public void givenInputStringWithOnlyNoIntegersAndSpecialChar_thenExpectExceptionThrown() {
		String testInput = "two three";
		
		assertThatThrownBy(() -> {
			GameUtils.processDicesOutcome(testInput);
		})
		.isInstanceOf(OutcomeRetrievalException.class)
		.hasMessageMatching(".+");
	}
	
	@Test
	public void givenInputStringWith5Integers_returnIntArrayOfFirstTwoIntegers() {
		String testInput = "1	2	3	4	5";
		int[] expectedResult = {1,2};
		
		int[] actual = GameUtils.processDicesOutcome(testInput);
		
		assertThat(actual.length, comparesEqualTo(expectedResult.length));
		assertThat(actual, is(expectedResult));
	}

}
