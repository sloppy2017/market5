package com.c2b.coin.account.api;

import java.math.BigDecimal;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * 提币
 * @author lenovo
 *
 */
@FeignClient("coin-account")
public interface DepositClient {
	/**
	 * 广播时回调
	 * @param address
	 * @param currencyType
	 * @param userName
	 * @param hxId
	 * @param amount
	 * @return
	 */
	@RequestMapping(value="/client/deposit/broadcastCallback",method=RequestMethod.POST)
	public String broadcastCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount);
	/**
	 * 确认时回调
	 * @param address
	 * @param currencyType
	 * @param userName
	 * @param hxId
	 * @param amount
	 * @return
	 */
	@RequestMapping(value="/client/deposit/confirmCallback",method=RequestMethod.POST)
	public String confirmCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount);
	
}
