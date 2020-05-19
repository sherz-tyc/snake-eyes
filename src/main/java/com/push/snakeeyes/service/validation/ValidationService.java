package com.push.snakeeyes.service.validation;

import java.math.BigDecimal;

import com.push.snakeeyes.exception.InsufficientFundException;
import com.push.snakeeyes.exception.StakeNotAcceptableException;

public interface ValidationService {
	
	public void validateGameEntry(BigDecimal playerBalance, double stake) 
			throws StakeNotAcceptableException, InsufficientFundException;
	
}
