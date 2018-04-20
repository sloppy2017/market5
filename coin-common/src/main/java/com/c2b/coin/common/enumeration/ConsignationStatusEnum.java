package com.c2b.coin.common.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明 委托状态枚举类
 * 
 * @author Anne
 * @date 2017年10月19日
 */
public enum ConsignationStatusEnum {

  CONSIGNING("委托中", 1), NO_DEAL("未成交", 2), PART_DEAL("部分成交", 3), ALL_DEAL(
      "全部成交", 4), REVOKE("已撤单", 5);
  private Integer statusCode;
  private String statusName;

  ConsignationStatusEnum(String statusName, int statusCode) {
    this.statusCode = statusCode;
    this.statusName = statusName;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(String statusName) {
    this.statusName = statusName;
  }

  private static final Map<Integer, ConsignationStatusEnum> consignationStatusEnumMap = new HashMap<>();

  static {
    for (ConsignationStatusEnum consignationStatusEnum : ConsignationStatusEnum
        .values()) {
      consignationStatusEnumMap.put(consignationStatusEnum.getStatusCode(),
          consignationStatusEnum);
    }
  }

  public static ConsignationStatusEnum getConsignationStatusEnum(Integer code) {
    return consignationStatusEnumMap.get(code);
  }
}
