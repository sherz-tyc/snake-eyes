package com.push.snakeeyes.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This {@link ControllerAdvisor} class intercepts Exceptions and handles them gracefully by
 * returning a Response with meaningful message and relevant HTTP Status Code.
 */
@ControllerAdvice
public class ControllerAdvisor {
	
	private static final String TIMESTAMP_KEY = "timestamp";
    private static final String MESSAGE_KEY = "message";
    private static final String ERROR_MSG = "operation failed.";
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({PlayerNotFoundException.class, SnakeEyesGameException.class, IllegalStateException.class})
    public ResponseEntity<String> handle(RuntimeException e) {
        return new ResponseEntity<>(constructResponseBody(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({InsufficientFundException.class, StakeNotAcceptableException.class})
    public ResponseEntity<String> handle(SnakeEyesGameException e) {
    	return new ResponseEntity<>(constructResponseBody(e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({OutcomeRetrievalException.class, WinningCalculationException.class,
    	IllegalArgumentException.class, RestClientException.class, NullPointerException.class})
    public ResponseEntity<String> handleError(Exception e) {
        return new ResponseEntity<>(constructResponseBody(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String constructResponseBody(String msg) {
    	String resMsg = (msg == null || msg.isEmpty())? ERROR_MSG : msg;
        Map<String, String> body = new LinkedHashMap<>();
        body.put(TIMESTAMP_KEY, LocalDateTime.now().toString());
        body.put(MESSAGE_KEY, resMsg);
        String json = ERROR_MSG;
        try {
            json = new ObjectMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}
