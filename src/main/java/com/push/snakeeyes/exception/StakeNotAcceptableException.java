package com.push.snakeeyes.exception;

public class StakeNotAcceptableException extends SnakeEyesGameException {
	
	private static final long serialVersionUID = -3782625471225565549L;

	public StakeNotAcceptableException() {
        super("Value of stake is not acceptable.");
    }

}
