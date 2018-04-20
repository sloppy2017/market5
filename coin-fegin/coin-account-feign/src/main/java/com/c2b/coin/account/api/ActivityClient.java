package com.c2b.coin.account.api;

import java.math.BigDecimal;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.c2b.coin.common.AjaxResponse;

@FeignClient("coin-account")
public interface ActivityClient {
	@RequestMapping(value="/client/activity/addActivityAsset",method=RequestMethod.POST)
	public AjaxResponse addActivityAsset(@RequestParam("userId") long userId,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("amount")BigDecimal amount,@RequestParam("operationType")int operationType,@RequestParam("remark") String remark);
}
