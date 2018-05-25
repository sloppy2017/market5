package com.c2b.coin.trade.rest;

import com.alibaba.druid.util.StringUtils;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.CodeConstant;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.enumeration.ConsignationTradeTypeEnum;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.trade.client.RestClient;
import com.c2b.coin.trade.exceptions.IllegalParamException;
import com.c2b.coin.trade.exceptions.TradeException;
import com.c2b.coin.trade.service.ConsignationService;
import com.c2b.coin.trade.service.TradePairInfoService;
import com.c2b.coin.web.common.BaseRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author Anne
 * @date 2017.10.19
 */
@RestController
@RequestMapping("/client/trade")
public class ConsignationClientRest extends BaseRest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  ConsignationService consignationService;

  @Autowired
  TradePairInfoService tradePairInfoService;

  @Autowired
  RestClient restCient;


  private AjaxResponse palceConsignationOrder(long userId, String bizType, String tradeType, String consignationPrice, String consignationCount, String type) {
    if (StringUtils.isEmpty(bizType) || StringUtils.isEmpty(tradeType.trim())
      || new BigDecimal(consignationCount).compareTo(BigDecimal.ZERO) <= 0) {
      logger.info("userId=" + userId + "下委托单，参数错误：{}", bizType, tradeType,
        consignationPrice, consignationCount, type);
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (!consignationService.checkPriceAndCount(tradeType, consignationPrice, consignationCount)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    try {
      return consignationService.palceConsignationOrder(userId, "",
        bizType, tradeType, null == consignationPrice ? null
          : new BigDecimal(consignationPrice), new BigDecimal(
          consignationCount), type);
    } catch (TradeException e) {
      return writeObj(e.getErrorMsgEnum());
    }
  }

  @RequestMapping(value = "/limitPriceBuyConsignation", method = RequestMethod.POST)
  public AjaxResponse limitPriceBuyConsignation(@RequestParam long userId, @RequestParam String bizType, @RequestParam String consignationPrice, @RequestParam String consignationCount) {
    logger.debug("limitPriceBuyConsignation");
    return palceConsignationOrder(userId, bizType, ConsignationTradeTypeEnum.LIMIT_PRICE_BUY.getStatusCode(), consignationPrice, consignationCount, "buy");
  }

  @RequestMapping(value = "/limitPriceSellConsignation", method = RequestMethod.POST)
  public AjaxResponse limitPriceSellConsignation(@RequestParam long userId, @RequestParam String bizType, @RequestParam String consignationPrice, @RequestParam String consignationCount) {
    logger.debug("limitPriceSellConsignation");
    return palceConsignationOrder(userId, bizType, ConsignationTradeTypeEnum.LIMIT_PRICE_SELL.getStatusCode(), consignationPrice, consignationCount, "sell");
  }

  @RequestMapping(value = "/marketPriceBuyConsignation", method = RequestMethod.POST)
  public AjaxResponse marketPriceBuyConsignation(@RequestParam long userId, @RequestParam String bizType, @RequestParam String consignationCount) {
    logger.debug("marketPriceBuyConsignation");
    return palceConsignationOrder(userId, bizType, ConsignationTradeTypeEnum.MARKET_PRICE_BUY.getStatusCode(), null, consignationCount, "buy");
  }

  @RequestMapping(value = "/marketPriceSellConsignation", method = RequestMethod.POST)
  public AjaxResponse marketPriceSellConsignation(@RequestParam long userId, @RequestParam String bizType, @RequestParam String consignationCount) {
    logger.debug("marketPriceSellConsignation");
    return palceConsignationOrder(userId, bizType, ConsignationTradeTypeEnum.MARKET_PRICE_SELL.getStatusCode(), null, consignationCount, "sell");
  }

  @RequestMapping(value = "/revokeOrder", method = RequestMethod.POST)
  public AjaxResponse revokeOrder(@RequestParam String userId, @RequestParam String consignationNo) {
    logger.debug("revokeOrder");
    try {
      return writeObj(consignationService.revokeOrder(consignationNo));
    } catch (final IllegalParamException e) {
      return writeObj(CodeConstant.REVOKE_ORDER_FAIL);
    }
  }

  @RequestMapping(value = "/getBuySellOrder", method = RequestMethod.GET)
  public AjaxResponse getBuySellOrder(@RequestParam String userId, @RequestParam int pageNo, @RequestParam int pageSize, @RequestParam int consignationStatus, @RequestParam int bizType) {
    logger.debug("getBuySellOrder");
    return writeObj(consignationService.listConsignationOrderByUserId(userId, consignationStatus, bizType, pageNo, pageSize));
  }

}
