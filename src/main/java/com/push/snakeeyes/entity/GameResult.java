package com.push.snakeeyes.entity;

/**
 * This Enum class defines the finite set of possible results from the Game.
 */
public enum GameResult {
	SNAKE_EYES("snake eyes"), PAIRED("paired"), NONE("none");
	
	String name;
	
	private GameResult(String name) {
		this.name = name;
	}
	
	public String toName() {
		return name;
	}

}
