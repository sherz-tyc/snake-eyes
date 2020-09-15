package com.push.snakeeyes.exception;

public class InsufficientFundException extends SnakeEyesGameException {
	
	private static final long serialVersionUID = 6258271398708373253L;

	public InsufficientFundException() {
        super("Player has insufficient fund.");
    }

}
