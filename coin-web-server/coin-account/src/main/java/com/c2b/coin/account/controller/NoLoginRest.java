package com.c2b.coin.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.account.service.IUserAccountAssetService;
import com.c2b.coin.web.common.BaseRest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("不需要携带token的接口")
@RestController
@RequestMapping("/nologin")
public class NoLoginRest extends BaseRest{
	@Autowired
	private IUserAccountAssetService userAccountAssetService;
	
	
	@PostMapping("/asset/USDCNYRate")
	@ApiOperation(value="人民币与美元汇率",notes="获取人民币与美元的汇率")
	public String usdToCNYRate() {
		return writeJson(this.userAccountAssetService.usdToCNYRate());
	}
	
	@PostMapping("/asset/exchangeRate")
	@ApiOperation(value="基础货币汇率接口",notes="需要货币汇率的时候调用")
	public String exchangeRate() {
		
		return writeJson(this.userAccountAssetService.exchangeRate());
	}
}
