package com.push.snakeeyes.service.validation;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.push.snakeeyes.exception.InsufficientFundException;
import com.push.snakeeyes.exception.StakeNotAcceptableException;

@Service
public class ValidationServiceImpl implements ValidationService {
	
	@Value("${validation.stake.options}")
	private double[] allowedStakeArr;
	
	@Override
	public void validateGameEntry(BigDecimal playerBalance, double stake)
			throws StakeNotAcceptableException, InsufficientFundException {
		isValidStake(stake);
		hasSufficientFunds(playerBalance,stake);
	}
	
	private void isValidStake(double stake) {
		if(!DoubleStream.of(allowedStakeArr).anyMatch(d -> d == stake)) {
			throw new StakeNotAcceptableException();
		}
	}

	private void hasSufficientFunds(BigDecimal playerBalance, double stake) {
		if (playerBalance.compareTo(new BigDecimal(stake)) < 0) {
			throw new InsufficientFundException();
		}
	}

}
