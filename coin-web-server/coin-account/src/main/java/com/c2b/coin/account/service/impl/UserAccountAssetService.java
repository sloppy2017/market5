package com.c2b.coin.account.service.impl;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.entity.DigitalCoin;
import com.c2b.coin.account.entity.UserAccount;
import com.c2b.coin.account.entity.vo.AccountAssetVO;
import com.c2b.coin.account.entity.vo.AssetTotalVO;
import com.c2b.coin.account.mapper.AssetLogMapper;
import com.c2b.coin.account.mapper.DigitalCoinMapper;
import com.c2b.coin.account.mapper.UserAccountMapper;
import com.c2b.coin.account.service.IDigitalCoinWithDrawAndDeposit;
import com.c2b.coin.account.service.IUserAccountAssetService;
import com.c2b.coin.account.task.RateTask;
import com.c2b.coin.account.util.SpringUtil;
import com.c2b.coin.common.DateUtil;

@Service
public class UserAccountAssetService implements IUserAccountAssetService {

  @Autowired
  private UserAccountMapper userAccountMapper;
  @Autowired
  private DigitalCoinMapper digitalCoinMapper;
  @Autowired
  private AssetLogMapper assetLogMapper;

  @Resource(name = "redisTemplate")
  protected RedisTemplate<Object, Object> redisTemplate;

  @Override
  public AssetTotalVO getAssetTotal(long userId) {
    //计算总资产
    UserAccount userAccount = new UserAccount();
    userAccount.setUserId(userId);
    List<UserAccount> list = this.userAccountMapper.select(userAccount);
    AssetTotalVO atv = new AssetTotalVO();
    atv.setAvailableBTC(new BigDecimal("0"));
    atv.setFreezingBTC(new BigDecimal("0"));
    atv.setTotalBTC(new BigDecimal("0"));
    DigitalCoin coin = new DigitalCoin();
    coin.setCoinName("BTC");
    coin.setIsEnabled(1);
    coin = this.digitalCoinMapper.selectOne(coin);
    String btcName = coin.getCoinName();
    for (UserAccount userAccountEle : list) {
      this.countBTCAsset(atv, userAccountEle, btcName);
    }
    this.countCNYAsset(atv);
    return atv;
  }

  @Override
  public List<AccountAssetVO> getAssetList(long userId) {
    //1. 从数据库获取用户全部资产
    return this.userAccountMapper.selectAccountAssetVO(userId);
  }


  @Override
  public List<AssetLog> getAssetHistory(Integer type, long userId) {
    return this.assetLogMapper.selectAssetLogOrderByCreateTime(type, userId);
  }

  @Override
  public List<DigitalCoin> getDigitalCoin() {
    DigitalCoin coin = new DigitalCoin();
    coin.setIsEnabled(1);
    List<DigitalCoin> list = this.digitalCoinMapper.select(coin);
    return list;
  }

  @Override
  public boolean checkAvailableAsset(long userId, int currencyType, BigDecimal amount) {
    //检查可用资产
    UserAccount userAccount = new UserAccount();
    userAccount.setUserId(userId);
    userAccount.setCurrencyType(currencyType);
    userAccount = this.userAccountMapper.selectOne(userAccount);
    if (userAccount == null) {
      return false;
    }
    if (userAccount.getAvailableAmount().compareTo(amount) >= 0) {
      return true;
    }
    return false;
  }

  /**
   * 计算对应的人民资产
   *
   * @param atv
   */
  private void countCNYAsset(AssetTotalVO atv) {
    BigDecimal rate = (BigDecimal) redisTemplate.opsForValue().get(RateTask.USDT_BTC_RATE_KEY);
    atv.setAvailableUSD(atv.getAvailableBTC().multiply(rate).doubleValue());
    atv.setFreezingUSD(atv.getFreezingBTC().multiply(rate).doubleValue());
    atv.setTotalUSD(atv.getTotalBTC().multiply(rate).doubleValue());
  }

  /**
   * 计算对应的比特币资产
   *
   * @param atv
   * @param userAccount
   * @param bTCCurrencyId
   */
  private void countBTCAsset(AssetTotalVO atv, UserAccount userAccount, String btcName) {
    IDigitalCoinWithDrawAndDeposit digitalCoinWithDraw = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(userAccount.getCurrencyName() + "WithDrawAndDeposit");
    digitalCoinWithDraw.countBTCAsset(atv, userAccount, btcName);

  }


  @Override
  public Map<String, Object> exchangeRate() {
    //获取比特币汇率
    BigDecimal btcRate = (BigDecimal) redisTemplate.opsForValue().get(RateTask.USDT_BTC_RATE_KEY);
    //获取以太坊汇率
    BigDecimal ethRate = (BigDecimal) redisTemplate.opsForValue().get(RateTask.USDT_ETH_RATE_KEY);
    Map<String, Object> resultMap = new HashMap<String, Object>();
    resultMap.put("btcRate", btcRate);
    resultMap.put("ethRate", ethRate);
    return resultMap;
  }


  @Override
  public String poundage(String currencyName) {
    IDigitalCoinWithDrawAndDeposit digitalCoinWithDrawAndDeposit = (IDigitalCoinWithDrawAndDeposit) SpringUtil.getBean(currencyName + "WithDrawAndDeposit");
    return digitalCoinWithDrawAndDeposit.caculateFee().toString();
  }


  @Override
  public UserAccount addUserAccount(int currencyType, String currencyName, long userId, String userName,
                                    String address) {
    UserAccount userAccount = new UserAccount();
    userAccount.setUserId(userId);
    userAccount.setCurrencyType(currencyType);
    userAccount.setCurrencyName(currencyName);
    userAccount.setAccountType(1);
    userAccount.setCreatetime(DateUtil.getCurrentTimestamp());
    userAccount.setUpdateTime(DateUtil.getCurrentTimestamp());
    userAccount.setAccountAddress(address);
    BigDecimal zero = new BigDecimal("0");
    userAccount.setTotalAmount(zero);
    userAccount.setAvailableAmount(zero);
    userAccount.setFreezingAmount(zero);
    //插入数据库
    this.userAccountMapper.insert(userAccount);
    return userAccount;
  }


  @Override
  public Object usdToCNYRate() {
    double cnyValue = (double) redisTemplate.opsForValue().get(RateTask.CNY_USDT_RATE_KEY);
    return cnyValue;
  }


}
