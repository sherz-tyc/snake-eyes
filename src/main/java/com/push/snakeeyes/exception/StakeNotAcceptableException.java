package com.push.snakeeyes.exception;

public class StakeNotAcceptableException extends SnakeEyesGameException {
	
	public StakeNotAcceptableException() {
        super("Value of stake is not acceptable.");
    }

}
