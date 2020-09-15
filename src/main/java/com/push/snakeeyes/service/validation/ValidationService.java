package com.push.snakeeyes.service.validation;

import java.math.BigDecimal;

import com.push.snakeeyes.exception.InsufficientFundException;
import com.push.snakeeyes.exception.StakeNotAcceptableException;

public interface ValidationService {
	
	/**
	 * Provides validation for given game entry.
	 * 
	 * @param playerBalance player's current balance
	 * @param stake player's proposed stake
	 * @throws StakeNotAcceptableException stake value is not recognised by system
	 * @throws InsufficientFundException player does not have sufficient fund
	 */
	public void validateGameEntry(BigDecimal playerBalance, double stake) 
			throws StakeNotAcceptableException, InsufficientFundException;
	
}
