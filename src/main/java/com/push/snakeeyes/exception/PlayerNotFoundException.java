package com.push.snakeeyes.exception;

public class PlayerNotFoundException extends SnakeEyesGameException {
	
	private static final long serialVersionUID = -8874123387259849373L;

	public PlayerNotFoundException(long playerId) {
        super(String.format("Player with id %d does not exist.", playerId));
    }

}
