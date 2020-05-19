package com.push.snakeeyes.exception;

public class InsufficientFundException extends SnakeEyesGameException {
	
	public InsufficientFundException() {
        super("Player has insufficient fund.");
    }

}
