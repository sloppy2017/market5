package com.c2b.coin.account.feign;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.c2b.coin.account.feign.dto.WithdrawLog;
import com.c2b.coin.common.AjaxResponse;

@FeignClient("eco-currency-service")
public interface BTCClient {
	@RequestMapping(value="/eco-currency-service/client/btc/create",method=RequestMethod.POST)
	public ClientAjaxResponse createWallet(@RequestParam("account") String account);
	
	@RequestMapping(value="/eco-currency-service/client/btc/sendMoney")
	public ClientAjaxResponse sendMoney(@RequestBody WithdrawLog withdrawLog);
	
	
	@RequestMapping(value="/eco-currency-service/client/btc/getAddress",method=RequestMethod.GET)
	public ClientAjaxResponse getAddress(@RequestParam("account") String account);
}
