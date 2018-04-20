package com.c2b.wallet.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.c2b.wallet.service.WithDrawService;

@Component
public class WithDrawServiceHystrix implements WithDrawService {

	@Override
	public String confirmCallback(String address, int currencyType,
			String userName, String hxId, BigDecimal amount,String orderNo) {
		return "-9999";
	}

}
