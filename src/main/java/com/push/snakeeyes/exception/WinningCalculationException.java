package com.push.snakeeyes.exception;

public class WinningCalculationException extends SnakeEyesGameException {
	
	private static final long serialVersionUID = -3775654150320485433L;

	public WinningCalculationException(double stake) {
        super(String.format("Cannot calculate winings from: Stake = %f", stake));
    }

}
