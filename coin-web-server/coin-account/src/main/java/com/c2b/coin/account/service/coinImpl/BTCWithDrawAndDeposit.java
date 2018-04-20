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
import com.c2b.coin.account.feign.BTCClient;
import com.c2b.coin.account.feign.ClientAjaxResponse;
import com.c2b.coin.account.feign.dto.WithdrawLog;
import com.c2b.coin.account.mapper.AssetLogMapper;
import com.c2b.coin.account.service.IDigitalCoinWithDrawAndDeposit;
import com.c2b.coin.common.AssetChangeConstant;
import com.c2b.coin.common.DateUtil;

@Service
public class BTCWithDrawAndDeposit implements IDigitalCoinWithDrawAndDeposit{

	@Value("${wallet.btcWithDrawPoundage}")
	private String fee;
  @Value("${wallet.btcWithDrawLimit}")
  private String limit;
	@Autowired
	private BTCClient btcClient;
  @Value("${wallet.btcWithDrawMax}")
	private String max;
  @Autowired
  private AssetLogMapper assetLogMapper;

	@Override
	public String noticeWithDrawService(String currencyName, AssetLog assetLog) {
		WithdrawLog withdrawLog=new WithdrawLog();
		withdrawLog.setAccount(assetLog.getUsername());
		withdrawLog.setToAddress(assetLog.getAddress());
		withdrawLog.setMoney(assetLog.getActualAsset());
		withdrawLog.setOrderNo(assetLog.getOrderNo());
		ClientAjaxResponse ajaxResponse=this.btcClient.sendMoney(withdrawLog);
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
	public void countBTCAsset(AssetTotalVO atv, UserAccount userAccount,String btcName) {
		atv.setAvailableBTC(userAccount.getAvailableAmount().add(atv.getAvailableBTC()));
		atv.setFreezingBTC(userAccount.getFreezingAmount().add(atv.getFreezingBTC()));
		atv.setTotalBTC(userAccount.getTotalAmount().add(atv.getTotalBTC()));
	}

	@Override
	public String getAddress(String userName) {
		ClientAjaxResponse ajaxResponse=this.btcClient.createWallet(userName);
		String address =null;
		if(!ajaxResponse.getSuccess()) {
			ajaxResponse=this.btcClient.getAddress(userName);
			address = ajaxResponse.getData().toString();
		}else {
			Map resMap=(Map) ajaxResponse.getData();
			address = resMap.get("address").toString();
		}
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
    List<AssetLog> list = assetLogMapper.findByCurrency(AssetChangeConstant.WITHDRAW,userId, 1,startDate, endDate);
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
