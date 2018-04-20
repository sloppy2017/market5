package com.c2b.coin.account.service;

import java.math.BigDecimal;

public interface IMessageSendService {

	void insertToDB(Long userId, String userName, int type, BigDecimal amount, BigDecimal afterAmount);

}
