package com.c2b.coin.account.service;

import java.math.BigDecimal;

/**
 * 资产变动业务接口
 * @author lenovo
 *
 */
public interface IAssetChangeLogService {

	void assetChange(long userId, String userName, String orderNo, int bizType, int currencyType, BigDecimal amount);

	void tradePairAssetChange(long buyerUserId,String buyerUserName,long sellerUserId,String sellerUserName,String orderNo,
			int baseCurrencyType,int targetCurrencyType,BigDecimal baseAmount,BigDecimal targetAmount, BigDecimal baseRate, BigDecimal targetRate);

	void discountPoundage(long buyerUserId, String buyerUserName, long sellerUserId, String sellerUserName,
			String orderNo, int baseCurrencyType, int targetCurrencyType, BigDecimal baseAmount,
			BigDecimal targetAmount, String remark);

}
