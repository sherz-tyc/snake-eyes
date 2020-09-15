package com.push.snakeeyes.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple entity representing a player.
 */
@Entity
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class Player {
	
	@Id
	@GeneratedValue
	private long id;
	private String name;
	private BigDecimal balance;
	
	public Player(String name, BigDecimal balance) {
		this.name = name;
		this.balance = balance;
	}
	
}
