package com.c2b.coin.account.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.c2b.coin.account.feign.dto.WithdrawLog;
import com.c2b.coin.common.AjaxResponse;

@FeignClient("eco-lite-service")
public interface LTCClient {
	@RequestMapping(value="/eco-lite-service/client/ltc/create",method=RequestMethod.POST)
	public ClientAjaxResponse createWallet(@RequestParam("account") String account);
	
	@RequestMapping(value="/eco-lite-service/client/ltc/sendMoney",method=RequestMethod.POST)
    public ClientAjaxResponse sendMoney( @RequestBody WithdrawLog withdrawLog);

	@RequestMapping(value="/eco-lite-service/client/ltc/getAddress",method=RequestMethod.GET)
	public ClientAjaxResponse getAddress(@RequestParam("account") String account);
}
