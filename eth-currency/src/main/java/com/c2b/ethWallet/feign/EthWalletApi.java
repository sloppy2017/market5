package com.c2b.ethWallet.feign;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.c2b.coin.common.AjaxResponse;
import com.c2b.ethWallet.entity.WithdrawLog;

/**
 * 类说明
 * 
 * @author Anne
 * @date 2017年10月27日
 */
@RequestMapping("/api/ethWallet")
public interface EthWalletApi {
  @RequestMapping(value = "/client/eth/create/{account}", method = RequestMethod.POST)
  public AjaxResponse createWallet(@PathVariable("account") String account);

  @RequestMapping(value = "/client/eth/sendMoney", method = RequestMethod.POST)
  public AjaxResponse sendMoney(@RequestBody WithdrawLog withdrawLog);
}
