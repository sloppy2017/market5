package com.c2b.coin.account.api;

import java.math.BigDecimal;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("coin-account")
public interface AccountClient {

  @RequestMapping(value = "/client/asset/total", method = RequestMethod.GET)
  String getAssetTotal(@RequestParam long userId);

  @RequestMapping(value = "/client/asset/list", method = RequestMethod.GET)
  String getAssetList(@RequestParam long userId);

  @RequestMapping(value = "/client/account/assetChange", method = RequestMethod.POST)
  String assetChange(
    @RequestParam("userId") long userId,
    @RequestParam("userName") String userName,
    @RequestParam("orderNo") String orderNo,
    @RequestParam("bizType") int bizType,
    @RequestParam("currencyType") int currencyType,
    @RequestParam("amount") BigDecimal amount);

  @RequestMapping(value = "/client/account/checkAvailableAsset", method = RequestMethod.POST)
  boolean checkAvailableAsset(
    @RequestParam("userId") long userId,
    @RequestParam("currencyType") int currencyType,
    @RequestParam("amount") BigDecimal amount);

  @RequestMapping(value = "/client/account/tradePairAssetChange", method = RequestMethod.POST)
  String tradePairAssetChange(
    @RequestParam("buyerUserId") long buyerUserId,
    @RequestParam("buyerUserName") String buyerUserName,
    @RequestParam("sellerUserId") long sellerUserId,
    @RequestParam("sellerUserName") String sellerUserName,
    @RequestParam("orderNo") String orderNo,
    @RequestParam("baseCurrencyType") int baseCurrencyType,
    @RequestParam("targetCurrencyType") int targetCurrencyType,
    @RequestParam("baseAmount") BigDecimal baseAmount,
    @RequestParam("targetAmount") BigDecimal targetAmount,
    @RequestParam("baseRate") BigDecimal baseRate,
    @RequestParam("targetRate") BigDecimal targetRate);

  @RequestMapping(value = "/client/account/discountPoundage", method = RequestMethod.POST)
  String discountPoundage(
    @RequestParam("buyerUserId") long buyerUserId,
    @RequestParam("buyerUserName") String buyerUserName,
    @RequestParam("sellerUserId") long sellerUserId,
    @RequestParam("sellerUserName") String sellerUserName,
    @RequestParam("orderNo") String orderNo,
    @RequestParam("baseCurrencyType") int baseCurrencyType,
    @RequestParam("targetCurrencyType") int targetCurrencyType,
    @RequestParam("baseAmount") BigDecimal baseAmount,
    @RequestParam("targetAmount") BigDecimal targetAmount,
    @RequestParam("remark") String remark);

}
