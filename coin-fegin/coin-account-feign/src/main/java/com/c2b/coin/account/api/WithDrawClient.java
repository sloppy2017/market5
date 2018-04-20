package com.c2b.coin.account.api;

import java.math.BigDecimal;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("coin-account")
public interface WithDrawClient {

	/**
	 * 提币确认回调接口
	 * @param address
	 * @param currencyType
	 * @param userName
	 * @param hxId
	 * @param amount
	 * @return
	 */
	@RequestMapping(value="/client/withdraw/confirmCallBack",method=RequestMethod.POST)
	public String confirmCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount,@RequestParam("orderNo")String orderNo);
}
