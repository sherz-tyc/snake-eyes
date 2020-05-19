package com.push.snakeeyes.repo;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.push.snakeeyes.entity.Player;

@Repository
public interface PlayerRepo extends JpaRepository<Player, Long> {
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public <S extends Player> S  save(S player);
	
	@Lock(LockModeType.PESSIMISTIC_READ)
	public boolean existsById(long id);
	
	@Lock(LockModeType.PESSIMISTIC_READ)
	public Optional<Player> findById(long id);

}
