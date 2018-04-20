package com.c2b.coin.account.service;

import java.math.BigDecimal;

public interface IDepositService {


	String getAddress(long userId, String userName, int currencyType, String currencyName);

	void broadcastCallback(String address, int currencyType, String userName, String hxId, BigDecimal amount);

	void confirmCallback(String address, int currencyType, String userName, String hxId, BigDecimal amount);

}
