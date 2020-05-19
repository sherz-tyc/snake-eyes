package com.push.snakeeyes.controller;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.push.snakeeyes.entity.Outcome;
import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.service.game.GameService;
import com.push.snakeeyes.service.game.WinningService;
import com.push.snakeeyes.service.player.PlayerService;
import com.push.snakeeyes.service.validation.ValidationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Snake Eyes", description = "Snake Eyes Application")
@Slf4j
@RestController
@Validated
@RequestMapping("/snakeeyes")
public class GameController {
	
	@Autowired
	ValidationService validator;
	
	@Autowired
	PlayerService playerService;
	
	@Autowired
	WinningService winningService;
	
	@Autowired
	GameService gameService;
	
	@Operation(summary = "Submit Snake Eyes game entry to this endpoint.")
	@ApiResponses(value = { 
	        @ApiResponse(responseCode = "200", description = "Attempt successful"),
	        @ApiResponse(responseCode = "400", description = "Atempt failed") })
    @GetMapping("/play")
    public ResponseEntity<Outcome> getSnakeEyesResult(
    		@RequestParam(value = "stake", required=true) @Min(1) double stake,
    		@RequestParam(value = "playerId", required=false) Long playerId) {
		
		log.info("Received attempt for Snake Eyes game..");
		playerId = playerId == null? 0l: playerId;
		
		Player player = playerService.retrieve(playerId);
		validator.validateGameEntry(player.getBalance(), stake);
		Outcome outcome = gameService.submitAttempt(player, stake);
		
        return new ResponseEntity<Outcome>(outcome, HttpStatus.OK);
    };
    
	
	@Operation(summary = "Retrieve game log by Player ID.")
	@ApiResponses(value = { 
	        @ApiResponse(responseCode = "200", description = "Retrieval successful"),
	        @ApiResponse(responseCode = "400", description = "Retrieval failed") })
    @GetMapping("/log")
    public ResponseEntity<List<Outcome>> getGameLog(
    		@RequestParam(value = "playerId", required=true) Long playerId) {
		
		log.info("Received instruction to retrieve game log..");
		
		List<Outcome> gameLog = winningService.getOutcomeLog(playerId);
		
        return new ResponseEntity<List<Outcome>>(gameLog, HttpStatus.OK);
    };
    
}
