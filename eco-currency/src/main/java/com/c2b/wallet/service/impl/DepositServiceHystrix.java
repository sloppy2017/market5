package com.c2b.wallet.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.c2b.wallet.service.DepositService;

@Component
public class DepositServiceHystrix implements DepositService {

	@Override
	public String broadcastCallback(String address, int currencyType,
			String userName, String hxId, BigDecimal amount) {
		return "-9999";
	}

	@Override
	public String confirmCallback(String address, int currencyType,
			String userName, String hxId, BigDecimal amount) {
		return "-9999";
	}

}
