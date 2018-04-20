package com.c2b.coin.account.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.c2b.coin.account.feign.dto.WithdrawLog;
import com.c2b.coin.common.AjaxResponse;

@FeignClient("sanchain-service")
public interface SANClient {

	@RequestMapping(value="/sanchain-service/client/sanchain/create",method=RequestMethod.POST)
	public AjaxResponse createWallet(@RequestParam("account") String account);
	
	@RequestMapping(value="/sanchain-service/client/sanchain/sendMoney",method=RequestMethod.POST)
    public AjaxResponse sendMoney( @RequestBody WithdrawLog withdrawLog);
}
