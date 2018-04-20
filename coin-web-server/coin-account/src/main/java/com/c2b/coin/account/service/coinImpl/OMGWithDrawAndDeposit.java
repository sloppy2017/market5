package com.c2b.coin.account.service.coinImpl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.c2b.coin.account.entity.AssetLog;
import com.c2b.coin.account.entity.UserAccount;
import com.c2b.coin.account.entity.vo.AssetTotalVO;
import com.c2b.coin.account.exception.AssetChangeException;
import com.c2b.coin.account.feign.MarketClient;
import com.c2b.coin.account.feign.OMGClient;
import com.c2b.coin.account.feign.dto.WithdrawLog;
import com.c2b.coin.account.mapper.AssetLogMapper;
import com.c2b.coin.account.service.IDigitalCoinWithDrawAndDeposit;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.AssetChangeConstant;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;

@Service
public class OMGWithDrawAndDeposit implements IDigitalCoinWithDrawAndDeposit{

	@Value("${wallet.omgWithDrawPoundage}")
	private String fee;
  @Value("${wallet.omgWithDrawLimit}")
  private String limit;
	@Autowired
	private MarketClient marketClient;

	@Autowired
	private OMGClient omgClient;
  @Value("${wallet.omgWithDrawMax}")
  private String max;
  @Autowired
  private AssetLogMapper assetLogMapper;
  
  private final static Map<String,ErrorMsgEnum> ERROR_Map=new HashMap<String,ErrorMsgEnum>();
  
  static {
	  ErrorMsgEnum[] enums=ErrorMsgEnum.values();
	  for (ErrorMsgEnum errorMsgEnum : enums) {
		  ERROR_Map.put(errorMsgEnum.getCode(), errorMsgEnum);
	}
  }
  
	@Override
	public String noticeWithDrawService(String currencyName, AssetLog assetLog) {
		WithdrawLog withdrawLog=new WithdrawLog();
		withdrawLog.setAccount(assetLog.getUsername());
		withdrawLog.setToAddress(assetLog.getAddress());
		withdrawLog.setMoney(assetLog.getActualAsset());
		withdrawLog.setOrderNo(assetLog.getOrderNo());
		AjaxResponse ajaxResponse=this.omgClient.sendMoney(withdrawLog);
		if(ajaxResponse==null) {
			return null;
		}
		if(!ajaxResponse.isSuccess()||null==ajaxResponse.getData()) {
			throw new AssetChangeException(ERROR_Map.get(ajaxResponse.getError().getCode()), "通知以太坊钱包失败");
		}
		return ajaxResponse.getData().toString();
	}

	@Override
	public BigDecimal caculateFee() {
		return new BigDecimal(fee);
	}

	@Override
	public void countBTCAsset(AssetTotalVO atv, UserAccount userAccount, String btcName) {
		AjaxResponse ajaxResponse=marketClient.getRealTimePrice(userAccount.getCurrencyName(), btcName);
		BigDecimal ex=new BigDecimal(ajaxResponse.getData().toString());
		BigDecimal availableBTC=userAccount.getAvailableAmount().multiply(ex);
		atv.setAvailableBTC(availableBTC.add(atv.getAvailableBTC()));
		BigDecimal freezingBTC=userAccount.getFreezingAmount().multiply(ex);
		atv.setFreezingBTC(freezingBTC.add(atv.getFreezingBTC()));
		BigDecimal totalBTC=userAccount.getTotalAmount().multiply(ex);
		atv.setTotalBTC(totalBTC.add(atv.getTotalBTC()));
	}

	@Override
	public String getAddress(String userName) {
		AjaxResponse ajaxResponse=this.omgClient.createWallet(userName);
		String address =null;
		if(!ajaxResponse.isSuccess()) {
			ajaxResponse=this.omgClient.getAddress(userName);
		}
		address =ajaxResponse.getData().toString();
		return address;
	}

  @Override
  public boolean checkAmountLimit(BigDecimal amount) {
    if(amount.compareTo(new BigDecimal(limit)) < 0){
      return false;
    }
    return true;
  }

  @Override
  public boolean checkAmountMax(Long userId, BigDecimal amount) {
    long startDate = DateUtil.dateStartToUnixTimestamp();
    long endDate = DateUtil.dateEndToUnixTimestamp();
    List<AssetLog> list = assetLogMapper.findByCurrency(AssetChangeConstant.WITHDRAW,userId, 2,startDate, endDate);
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
