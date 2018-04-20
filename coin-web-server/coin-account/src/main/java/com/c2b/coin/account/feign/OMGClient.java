package com.c2b.coin.account.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.c2b.coin.account.feign.dto.WithdrawLog;
import com.c2b.coin.common.AjaxResponse;

@FeignClient("eth-currency")
public interface OMGClient {

	@RequestMapping(value="/client/omg/create",method=RequestMethod.POST)
	public AjaxResponse createWallet(@RequestParam("account") String account);
	
	@RequestMapping(value="/client/omg/sendMoney")
	public AjaxResponse sendMoney(@RequestBody WithdrawLog withdrawLog);
	@RequestMapping(value="/client/omg/getAddress",method=RequestMethod.GET)
	public AjaxResponse getAddress(@RequestParam("account") String account);
}
