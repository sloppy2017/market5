package com.c2b.coin.account.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.entity.DigitalCoin;
import com.c2b.coin.account.entity.UserAccount;
import com.c2b.coin.account.entity.vo.AccountAssetVO;
import com.c2b.coin.account.entity.vo.AssetTotalVO;



public interface IUserAccountAssetService {

  AssetTotalVO getAssetTotal(long userId);

	/**
	 * 获取总资产
	 * @param userId 用户id
	 * @return 用户资产列表
	 */
	List<AccountAssetVO> getAssetList(long userId);

	/**
	 * 获取资产历史表
	 * @param type 历史变动类型
	 * @param userId 用户id
	 * @return 资产历史情况
	 */
	List<AssetLog> getAssetHistory(Integer type, long userId);

	/**
	 * 获取币种信息
	 * @return 币种信息列表
	 */
	List<DigitalCoin> getDigitalCoin();
	/**
	 * 检查可用余额
	 * @param userId
	 * @param currencyType
	 * @param amount
	 * @return
	 */
	boolean checkAvailableAsset(long userId, int currencyType, BigDecimal amount);

	Map<String,Object> exchangeRate();

	String poundage(String currencyName);

	UserAccount addUserAccount(int currencyType,String currencyName,long userId,String userName,String address);

	Object usdToCNYRate();

}
