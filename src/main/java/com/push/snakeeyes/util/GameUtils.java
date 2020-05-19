package com.push.snakeeyes.util;

import com.push.snakeeyes.exception.OutcomeRetrievalException;

public class GameUtils {
	
	public static int[] processDicesOutcome(String outcomeString) {
		int diceA = 0;
		int diceB = 0;
		try {
			outcomeString = outcomeString.replaceAll("[^\\d.]", "");
			diceA = Character.getNumericValue(outcomeString.charAt(0));
			diceB = Character.getNumericValue(outcomeString.charAt(1));
			
		} catch (NullPointerException nex) {
			throw new OutcomeRetrievalException();
		} catch (IndexOutOfBoundsException iex) {
			throw new OutcomeRetrievalException();
		}
		
		if (diceA <= 0 || diceA > 6 || diceB <= 0 || diceB > 6) {
			throw new OutcomeRetrievalException();
		}
		
		return new int[] {diceA, diceB};
	}

}
