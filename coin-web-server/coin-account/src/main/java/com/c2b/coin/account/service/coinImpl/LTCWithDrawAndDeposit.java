package com.c2b.coin.account.service.coinImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.entity.UserAccount;
import com.c2b.coin.account.entity.vo.AssetTotalVO;
import com.c2b.coin.account.feign.ClientAjaxResponse;
import com.c2b.coin.account.feign.LTCClient;
import com.c2b.coin.account.feign.MarketClient;
import com.c2b.coin.account.feign.dto.WithdrawLog;
import com.c2b.coin.account.mapper.AssetLogMapper;
import com.c2b.coin.account.service.IDigitalCoinWithDrawAndDeposit;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.AssetChangeConstant;
import com.c2b.coin.common.DateUtil;

@Service
public class LTCWithDrawAndDeposit implements IDigitalCoinWithDrawAndDeposit {

  @Value("${wallet.ltcWithDrawPoundage}")
  private String fee;

  @Value("${wallet.ltcWithDrawLimit}")
  private String limit;
  @Autowired
  private LTCClient ltcClient;

  @Autowired
  private MarketClient marketClient;
  @Value("${wallet.ltcWithDrawMax}")
  private String max;
  @Autowired
  private AssetLogMapper assetLogMapper;
  @Override
  public String noticeWithDrawService(String currencyName, AssetLog assetLog) {
    WithdrawLog withdrawLog = new WithdrawLog();
    withdrawLog.setAccount(assetLog.getUsername());
    withdrawLog.setToAddress(assetLog.getAddress());
    withdrawLog.setMoney(assetLog.getActualAsset());
    withdrawLog.setOrderNo(assetLog.getOrderNo());
    ClientAjaxResponse ajaxResponse = this.ltcClient.sendMoney(withdrawLog);
    if(ajaxResponse==null||!ajaxResponse.getSuccess()||null==ajaxResponse.getData()) {
		return null;
	}
	return ajaxResponse.getData().toString();
  }

  @Override
  public BigDecimal caculateFee() {
    return new BigDecimal(fee);
  }

  @Override
  public void countBTCAsset(AssetTotalVO atv, UserAccount userAccount, String btcName) {
    //汇率动态
    AjaxResponse ajaxResponse = marketClient.getRealTimePrice(userAccount.getCurrencyName(), btcName);
    BigDecimal ex;
    if(null==ajaxResponse.getData()){
    	ex=  BigDecimal.ZERO;
    }else {
    	ex=new BigDecimal(ajaxResponse.getData().toString());
    }
    
    
    
    BigDecimal availableBTC = userAccount.getAvailableAmount().multiply(ex);
    atv.setAvailableBTC(availableBTC.add(atv.getAvailableBTC()));
    BigDecimal freezingBTC = userAccount.getFreezingAmount().multiply(ex);
    atv.setFreezingBTC(freezingBTC.add(atv.getFreezingBTC()));
    BigDecimal totalBTC = userAccount.getTotalAmount().multiply(ex);
    atv.setTotalBTC(totalBTC.add(atv.getTotalBTC()));
  }

  @Override
  public String getAddress(String userName) {
    ClientAjaxResponse ajaxResponse = this.ltcClient.createWallet(userName);
    String address = null;
    if (!ajaxResponse.getSuccess()) {
      ajaxResponse = this.ltcClient.getAddress(userName);
      address = ajaxResponse.getData().toString();
    } else {
      Map resMap = (Map) ajaxResponse.getData();
      address = resMap.get("address").toString();
    }
    return address;
  }

  @Override
  public boolean checkAmountLimit(BigDecimal amount) {
    if (amount.compareTo(new BigDecimal(limit)) < 0) {
      return false;
    }
    return true;
  }

  @Override
  public boolean checkAmountMax(Long userId, BigDecimal amount) {
    long startDate = DateUtil.dateStartToUnixTimestamp();
    long endDate = DateUtil.dateEndToUnixTimestamp();
    List<AssetLog> list = assetLogMapper.findByCurrency(AssetChangeConstant.WITHDRAW,userId, 3,startDate, endDate);
    BigDecimal total = BigDecimal.ZERO;
    list.stream().forEach(assetLog -> {
      total.add(assetLog.getAsset());
    });
    if (total.add(amount).compareTo(new BigDecimal(max)) > 0){
      return false;
    }
    return true;
  }
  @Override
  public BigDecimal getDailyMax() {
    return new BigDecimal(max);
  }

  @Override
  public BigDecimal getLimit() {
    return new BigDecimal(limit);
  }
}
