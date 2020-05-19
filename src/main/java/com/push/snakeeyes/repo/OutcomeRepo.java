package com.push.snakeeyes.repo;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.push.snakeeyes.entity.Outcome;

@Repository
public interface OutcomeRepo extends JpaRepository<Outcome, Long> {
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public <S extends Outcome> S  save(S outcome);
	
	@Lock(LockModeType.PESSIMISTIC_READ)
	public List<Outcome> findByPlayerId(long id);

}
