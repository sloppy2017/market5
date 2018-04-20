package com.c2b.coin.account.service;

import java.math.BigDecimal;
import java.util.List;

import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.entity.WithdrawAddressLog;
import com.c2b.coin.common.AjaxResponse;

public interface IWithdrawService {



	void addAddress(WithdrawAddressLog w);

	List<WithdrawAddressLog> getAddressList(Integer currencyType, long userId);

	void withdraw(long userId, int currencyType,String CurrencyName, String address, String remark, BigDecimal amount, String userName);

	AjaxResponse checkAssetPassword(long userId, String password,String verifyType,String verifyCode);

	void confirmCallback(String address, int currencyType, String userName, String hxId, BigDecimal amount, String orderNo);

	AjaxResponse checkPermission(long userId);

	boolean checkAmountLimit(String currencyName, BigDecimal amount);

	boolean checkAmountMax(Long userId, String currencyName, BigDecimal amount);

	void approvalWithDraw(int id,String currencyName, int status);

	List<AssetLog> getWithDrawList();

	BigDecimal getDailyMax(String currencyName);

	BigDecimal getLimit(String currencyName);

	void deleteAddress(long id);
}
