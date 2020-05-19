package com.push.snakeeyes.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class Outcome {
	
	@Id
	@GeneratedValue
	@JsonIgnore
	private long id;
	
	@JsonProperty("player_id")
	private long playerId;
	
	@JsonProperty("dice1")
	private int diceA;
	
	@JsonProperty("dice2")
	private int diceB;
	
	@JsonProperty("stake")
	private double stake;
	
	@JsonProperty("attempted_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime attemptedAt;
	
	@JsonProperty("winnings")
	private BigDecimal winnings;
	
	@JsonProperty("payout_name")
	private String payoutName;
	
	@JsonProperty("updated_balance")
	private BigDecimal updatedBalance;

}
