package com.push.snakeeyes.service.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.push.snakeeyes.exception.OutcomeRetrievalException;
import com.push.snakeeyes.util.GameUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DiceRollServiceImpl implements DiceRollService {

	@Value("${dices.api.baseUri}")
	private String dicesBaseUri;
	
	@Value("${dices.api.uriPath}")
	private String dicesUriPath;
	
	RestTemplate restTemplate;
	
	@Autowired
	public DiceRollServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@Override
	public int[] rollDices() {
		
		ResponseEntity<String> response = restTemplate.getForEntity(dicesBaseUri + dicesUriPath, String.class);
		log.debug("External API Response: {}", response.getBody());
		if (response.getBody() == null || response.getBody().isEmpty()) {
			throw new OutcomeRetrievalException();
		}
		
		return GameUtils.processDicesOutcome(response.getBody());
	}

}
