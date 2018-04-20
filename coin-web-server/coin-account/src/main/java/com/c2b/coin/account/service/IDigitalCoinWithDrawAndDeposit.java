package com.c2b.coin.account.service;

import java.math.BigDecimal;

import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.entity.UserAccount;
import com.c2b.coin.account.entity.vo.AssetTotalVO;



/**
 * 提币接口
 * @author lenovo
 *
 */
public interface IDigitalCoinWithDrawAndDeposit {

	/**
	 * 通知提币接口
	 * @param currencyName
	 * @param assetLog
	 * @return
	 */
	String noticeWithDrawService(String currencyName, AssetLog assetLog);

	/**
	 * 计算提币费率
	 * @return
	 */
	BigDecimal caculateFee();

	void countBTCAsset(AssetTotalVO atv, UserAccount userAccount, String btcName);

	String getAddress(String userName);

  boolean checkAmountLimit(BigDecimal amount);

  boolean checkAmountMax(Long userId, BigDecimal amount);

  BigDecimal getDailyMax();
  BigDecimal getLimit();
}
