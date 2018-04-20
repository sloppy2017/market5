package com.c2b.coin.common.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明 委托交易类型枚举类
 *
 * @author Anne
 * @date 2017年10月20日
 */
public enum ConsignationTradeTypeEnum {

  LIMIT_PRICE_BUY("限价买入", "1", "BUY_PRICEDEAL", "buy"), LIMIT_PRICE_SELL("限价卖出", "2", "SELL_PRICEDEAL", "sell"), MARKET_PRICE_BUY(
    "市价买入", "3", "BUY_MARKETTRANSACTIONS", "buy"), MARKET_PRICE_SELL("市价卖出", "4", "SELL_MARKETTRANSACTIONS", "sell");
  private String statusCode;
  private String statusName;
  private String matchInfoCode;
  private String orderType;

  ConsignationTradeTypeEnum(String statusName, String statusCode, String matchInfoCode, String orderType) {
    this.statusCode = statusCode;
    this.statusName = statusName;
    this.matchInfoCode = matchInfoCode;
    this.orderType = orderType;
  }

  public String getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }

  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(String statusName) {
    this.statusName = statusName;
  }

  public String getMatchInfoCode() {
    return matchInfoCode;
  }

  public void setMatchInfoCode(String matchInfoCode) {
    this.matchInfoCode = matchInfoCode;
  }
  
  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  private static final Map<Object, ConsignationTradeTypeEnum> consignationTradeTypeEnumMap = new HashMap<>();

  static {
    for (ConsignationTradeTypeEnum ConsignationTradeTypeEnum : ConsignationTradeTypeEnum.values()) {
      consignationTradeTypeEnumMap.put(ConsignationTradeTypeEnum.getStatusCode(), ConsignationTradeTypeEnum);
      consignationTradeTypeEnumMap.put(ConsignationTradeTypeEnum.getMatchInfoCode(), ConsignationTradeTypeEnum);
      consignationTradeTypeEnumMap.put(ConsignationTradeTypeEnum.getOrderType(), ConsignationTradeTypeEnum);
    }
  }

  public static ConsignationTradeTypeEnum getConsignationTradeTypeEnum(Object code) {
    return consignationTradeTypeEnumMap.get(code);
  }
}
