package com.push.snakeeyes.exception;

public class OutcomeRetrievalException extends SnakeEyesGameException {
	
	private static final long serialVersionUID = -385392557746873405L;

	public OutcomeRetrievalException() {
        super("There has been an error retrieving your results. Please rest assure that you have not been charged for this round.");
    }

}
