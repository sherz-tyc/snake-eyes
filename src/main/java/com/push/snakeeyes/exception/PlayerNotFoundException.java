package com.push.snakeeyes.exception;

public class PlayerNotFoundException extends SnakeEyesGameException {
	
	public PlayerNotFoundException(long playerId) {
        super(String.format("Player with id %d does not exist.", playerId));
    }

}
