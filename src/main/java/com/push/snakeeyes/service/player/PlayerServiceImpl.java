package com.push.snakeeyes.service.player;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.exception.PlayerNotFoundException;
import com.push.snakeeyes.repo.PlayerRepo;

@Service
public class PlayerServiceImpl implements PlayerService {
	
	@Value("${default.player.balance}")
	private BigDecimal defaultBalance;
	
	@Value("${default.player.name}")
	private String defaultName;
	
	@Autowired
	private PlayerRepo playerRepo;
	
	@Override
	@Transactional
	public Player update(Player player) {
		return playerRepo.save(player);
	}

	private Player createDefaultPlayer() {
		Player defaultPlayer = new Player();
		defaultPlayer.setBalance(defaultBalance);
		defaultPlayer.setName(defaultName);
		return defaultPlayer;
	}

	@Override
	@Transactional
	public Player retrieve(long playerId) throws PlayerNotFoundException {
		if (playerId <= 0) {
			return createDefaultPlayer();
		}
		Player player = playerRepo.findById(playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
		return player;
	}

}
