package com.c2b.coin.trade.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

/**
 * @author Anne
 * @date 2017.10.19
 */
@Api("委托相关服务")
@RestController
public class ConsignationRest extends BaseRest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  ConsignationService consignationService;

  @Autowired
  TradePairInfoService tradePairInfoService;

  @Autowired
  RestClient restCient;

  private static final long forbiddenTime = 1512698400000L;

  private AjaxResponse palceConsignationOrder(String bizType, String tradeType,
                                              String consignationPrice, String consignationCount, String type) {
    if(DateUtil.compareDateWithNow(forbiddenTime)==1){
      return writeObj(ErrorMsgEnum.BANNING_ORDERS);
    }
    Long userId = Long.valueOf(getUserId());
    String userName = getUsername();
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
      return consignationService.palceConsignationOrder(userId, userName,
        bizType, tradeType, null == consignationPrice ? null
          : new BigDecimal(consignationPrice), new BigDecimal(
          consignationCount), type);
    } catch (TradeException e) {
      return writeObj(e.getErrorMsgEnum());
    }
  }

  @ApiOperation(value = "限价买入委托单", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "bizType", value = "交易对主键", required = true, paramType = "form", dataType = "String"),
    @ApiImplicitParam(name = "consignationPrice", value = "委托价", required = true, paramType = "form", dataType = "BigDecimal"),
    @ApiImplicitParam(name = "consignationCount", value = "委托量", required = true, paramType = "form", dataType = "BigDecimal")})
  @RequestMapping(value = "/limitPriceBuyConsignation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiResponses(value = {@ApiResponse(code = 9000, message = "参数异常"), @ApiResponse(code = 3001, message = "CONSIGN_SAVE_FAIL,委托失败，请稍后重试"), @ApiResponse(code = 3002, message = "GENERATE_ORDERNO_FAIL,订单提交失败，请稍候重试"), @ApiResponse(code = 3003, message = "FROZEN_ASSET_FAIL，订单提交失败，请稍候重试"), @ApiResponse(code = 3004, message = "TRADE_PAIR_NOT_FOUND，订单提交失败，请稍候重试")})
  public AjaxResponse limitPriceBuyConsignation(@RequestParam String bizType,
                                                @RequestParam String consignationPrice,
                                                @RequestParam String consignationCount) {
    logger.debug("limitPriceBuyConsignation");
    return palceConsignationOrder(bizType, ConsignationTradeTypeEnum.LIMIT_PRICE_BUY.getStatusCode(), consignationPrice, consignationCount, "buy");
  }

  @ApiOperation(value = "限价卖出委托单", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "bizType", value = "交易对主键", required = true, paramType = "form", dataType = "String"),
    @ApiImplicitParam(name = "consignationPrice", value = "委托价", required = true, paramType = "form", dataType = "BigDecimal"),
    @ApiImplicitParam(name = "consignationCount", value = "委托量", required = true, paramType = "form", dataType = "BigDecimal")})
  @RequestMapping(value = "/limitPriceSellConsignation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiResponses(value = {@ApiResponse(code = 9000, message = "参数异常"), @ApiResponse(code = 3001, message = "CONSIGN_SAVE_FAIL,委托失败，请稍后重试"), @ApiResponse(code = 3002, message = "GENERATE_ORDERNO_FAIL,订单提交失败，请稍候重试"), @ApiResponse(code = 3003, message = "FROZEN_ASSET_FAIL，订单提交失败，请稍候重试"), @ApiResponse(code = 3004, message = "TRADE_PAIR_NOT_FOUND，订单提交失败，请稍候重试")})
  public AjaxResponse limitPriceSellConsignation(@RequestParam String bizType,
                                                 @RequestParam String consignationPrice,
                                                 @RequestParam String consignationCount) {
    logger.debug("limitPriceSellConsignation");
    return palceConsignationOrder(bizType, ConsignationTradeTypeEnum.LIMIT_PRICE_SELL.getStatusCode(), consignationPrice, consignationCount, "sell");
  }

  /**
   * 注：市价只能和限价进行撮合交易，而且市价没有价格，所以只能传交易对主键和交易数量两个参数
   */
  @ApiOperation(value = "市价买入委托单", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "bizType", value = "交易对主键", required = true, paramType = "form", dataType = "String"),
    @ApiImplicitParam(name = "consignationCount", value = "委托量", required = true, paramType = "form", dataType = "BigDecimal")})
  @RequestMapping(value = "/marketPriceBuyConsignation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiResponses(value = {@ApiResponse(code = 9000, message = "参数异常"), @ApiResponse(code = 3001, message = "CONSIGN_SAVE_FAIL,委托失败，请稍后重试"), @ApiResponse(code = 3002, message = "GENERATE_ORDERNO_FAIL,订单提交失败，请稍候重试"), @ApiResponse(code = 3003, message = "FROZEN_ASSET_FAIL，订单提交失败，请稍候重试"), @ApiResponse(code = 3004, message = "TRADE_PAIR_NOT_FOUND，订单提交失败，请稍候重试")})
  public AjaxResponse marketPriceBuyConsignation(@RequestParam String bizType,
                                                 @RequestParam String consignationCount) {
    logger.debug("marketPriceBuyConsignation");
    return palceConsignationOrder(bizType, ConsignationTradeTypeEnum.MARKET_PRICE_BUY.getStatusCode(), null, consignationCount, "buy");
  }

  @ApiOperation(value = "市价卖出委托单", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "bizType", value = "交易对主键", required = true, paramType = "form", dataType = "String"),
    @ApiImplicitParam(name = "consignationCount", value = "委托量", required = true, paramType = "form", dataType = "BigDecimal")})
  @RequestMapping(value = "/marketPriceSellConsignation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiResponses(value = {@ApiResponse(code = 9000, message = "参数异常"), @ApiResponse(code = 3001, message = "CONSIGN_SAVE_FAIL,委托失败，请稍后重试"), @ApiResponse(code = 3002, message = "GENERATE_ORDERNO_FAIL,订单提交失败，请稍候重试"), @ApiResponse(code = 3003, message = "FROZEN_ASSET_FAIL，订单提交失败，请稍候重试"), @ApiResponse(code = 3004, message = "TRADE_PAIR_NOT_FOUND，订单提交失败，请稍候重试")})
  public AjaxResponse marketPriceSellConsignation(@RequestParam String bizType,
                                                  @RequestParam String consignationCount) {
    logger.debug("marketPriceSellConsignation");
    return palceConsignationOrder(bizType, ConsignationTradeTypeEnum.MARKET_PRICE_SELL.getStatusCode(), null, consignationCount, "sell");
  }

  @ApiOperation(value = "查询所有委托单-订单管理调用（含分页）", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "username '委托人名称',consignation_no '委托单号',biz_type '交易对/成交对',trade_type '交易类型(1-限价买入 2-限价卖出 3-市价买入 4市价卖出)',consignation_price '委托价/委托金额',consignation_count '委托量',made_count '已成交量',made_price '已成交总额',consignation_status '委托状态 1-委托中 2-未成交 3-部分成交 4-全部成交 5-已撤单',data_status '数据状态(0：未删除 1：已删除)',create_time '委托时间',made_time '最后成交时间',update_time '修改时间',made_average_price '成交均价',unTradeCount '未成交',consignationTotalMoney '委托总额'")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "pageNo", value = "页码", required = true, paramType = "query", dataType = "int"),
    @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, paramType = "query", dataType = "int"),
    @ApiImplicitParam(name = "consignationStatus", value = "委托单状态， 0表示全部状态", required = true, paramType = "query", dataType = "int"),
    @ApiImplicitParam(name = "bizType", value = "交易对主键", required = true, paramType = "query", dataType = "int")})
  @RequestMapping(value = "/getBuySellOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse getBuySellOrder(@RequestParam int pageNo,
                                      @RequestParam int pageSize, @RequestParam int consignationStatus,
                                      @RequestParam int bizType) {
    return writeObj(consignationService.listConsignationOrderByUserId(getUserId(),consignationStatus, bizType, pageNo, pageSize));
  }

  @ApiOperation(value = "当前委托（查询所有未成交的委托单）-交易中心调用", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "username '委托人名称',consignation_no '委托单号',biz_type '交易对/成交对',trade_type '交易类型(1-限价买入 2-限价卖出 3-市价买入 4市价卖出)',consignation_price '委托价/委托金额',consignation_count '委托量',made_count '已成交量',made_price '已成交总额',consignation_status '委托状态 1-委托中 2-未成交 3-部分成交 4-全部成交 5-已撤单',data_status '数据状态(0：未删除 1：已删除)',create_time '委托时间',made_time '最后成交时间',update_time '修改时间',made_average_price '成交均价',unTradeCount '未成交',consignationTotalMoney '委托总额'")
  @RequestMapping(value = "/getUntradeBuySellOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse getUntradeBuySellOrder() {
    return writeObj(consignationService.listUnTradeOrder(getUserId()));
  }

  @ApiOperation(nickname = "撤单", value = "撤单", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "")
  @ApiImplicitParams({@ApiImplicitParam(name = "consignationNo", value = "委托单号", required = true, paramType = "form", dataType = "String")})
  @RequestMapping(value = "/revokeOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse revokeOrder(@RequestParam String consignationNo) {
    try {
      return writeObj(consignationService.revokeOrder(consignationNo));
    } catch (final IllegalParamException e) {
      return writeObj(CodeConstant.REVOKE_ORDER_FAIL);
    }
  }

  @ApiOperation(nickname = "全部撤单", value = "全部撤单-暂时不用", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({@ApiImplicitParam(name = "consignationNos", value = "委托单号，多个单号中间用英文逗号分隔，如1,2,3", required = true, paramType = "form", dataType = "String")})
  @RequestMapping(value = "/revokeAllOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse revokeAllOrder(@RequestParam String consignationNos) {
    List<Object> list = new ArrayList<Object>();
    logger.info("revokeAllOrder params consignationNos=" + consignationNos);
    try {
      if (consignationNos == null || consignationNos.split(",").length <= 0
        || "".equals(consignationNos)) {
        return writeObj(ErrorMsgEnum.PARAM_ERROR);
      }
      String consignationNo = null;
      for (int i = 0; i < consignationNos.split(",").length; i++) {
        consignationNo = consignationNos.split(",")[i];
        logger.info("consignationNo=" + consignationNo);
        if (consignationNo != null && !"".equals(consignationNo)) {
          list.add(consignationService.revokeOrder(consignationNo));
        }
      }
      return writeObj(list);
    } catch (final IllegalParamException e) {
      return writeObj(CodeConstant.REVOKE_ORDER_FAIL);
    }
  }

  @ApiOperation(nickname = "全部撤单", value = "全部撤单", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "")
  @RequestMapping(value = "/revokeAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse revokeAll() {
    return writeObj(consignationService.revokeAllOrder(getUserId()));
  }
}
