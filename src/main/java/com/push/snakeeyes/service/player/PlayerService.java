package com.push.snakeeyes.service.player;

import com.push.snakeeyes.entity.Player;
import com.push.snakeeyes.exception.PlayerNotFoundException;

public interface PlayerService {
	
	public Player update(Player player);
	
	public Player retrieve(long playerId) throws PlayerNotFoundException;
	
}
