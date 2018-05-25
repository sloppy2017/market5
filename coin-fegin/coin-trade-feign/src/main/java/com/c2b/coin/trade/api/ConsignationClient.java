package com.c2b.coin.trade.api;

import com.c2b.coin.common.AjaxResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("coin-trade")
public interface ConsignationClient {

  @PostMapping(value = "/client/trade/limitPriceBuyConsignation")
  AjaxResponse limitPriceBuyConsignation(@RequestParam("userId") long userId, @RequestParam("bizType") String bizType, @RequestParam("consignationPrice") String consignationPrice, @RequestParam("consignationCount") String consignationCount);

  @PostMapping(value = "/client/trade/limitPriceSellConsignation")
  AjaxResponse limitPriceSellConsignation(@RequestParam("userId") long userId, @RequestParam("bizType") String bizType, @RequestParam("consignationPrice") String consignationPrice, @RequestParam("consignationCount") String consignationCount);

  @PostMapping(value = "/client/trade/marketPriceBuyConsignation")
  AjaxResponse marketPriceBuyConsignation(@RequestParam("userId") long userId, @RequestParam("bizType") String bizType, @RequestParam("consignationCount") String consignationCount);

  @PostMapping(value = "/client/trade/marketPriceSellConsignation")
  AjaxResponse marketPriceSellConsignation(@RequestParam("userId") long userId, @RequestParam("bizType") String bizType, @RequestParam("consignationCount") String consignationCount);

  @PostMapping(value = "/revokeOrder")
  AjaxResponse revokeOrder(@RequestParam("userId") String userId, @RequestParam("consignationNo") String consignationNo);

  @GetMapping(value = "/getBuySellOrder")
  AjaxResponse getBuySellOrder(@RequestParam("userId") String userId, @RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize, @RequestParam("consignationStatus") int consignationStatus, @RequestParam("bizType") int bizType);

}
