package com.c2b.coin.sanchain.service;

import java.io.IOException;

import org.sanchain.client.api.exception.APIException;

import com.c2b.coin.sanchain.entity.WithdrawLog;


public interface ISanchainService {

	public final static String REDIS_ADDRESS_KEY="sanchain_address";
	public final static String SANCHAIN_DEPOSIT_LEGERHEIGHT="sanchain_deposit_leger_height";
	/**
	 * 生成c充币地址
	 * @param account
	 * @return
	 */
	String createAddress(String account) throws IOException;

	/**
	 * 三界宝提币
	 * @param withdrawLog
	 * @return
	 */
	String sendMoney(WithdrawLog withdrawLog) throws APIException;

}
