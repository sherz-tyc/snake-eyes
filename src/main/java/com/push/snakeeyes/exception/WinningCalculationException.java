package com.push.snakeeyes.exception;

import java.util.Arrays;

public class WinningCalculationException extends SnakeEyesGameException {
	
	public WinningCalculationException(double stake) {
        super(String.format("Cannot calculate winings from: Stake = %f", stake));
    }

}
