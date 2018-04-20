package com.c2b.coin.common.enumeration;

import java.util.HashMap;
import java.util.Map;
@SuppressWarnings("ALL")
public enum UserStatusEnum {
  NOT_ACTTIVE(0,"未激活"),NORMAL(1,"正常"),DISABLE(2,"禁用");

  private int statusCode;
  private String statusName;
  private UserStatusEnum(int statusCode, String statusName){
    this.statusName = statusName;
    this.statusCode = statusCode;
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
  private static final Map<Integer, UserStatusEnum> userStatusEnumMap = new HashMap<>();
  static {
    for (UserStatusEnum userStatusEnum : UserStatusEnum.values()) {
      userStatusEnumMap.put(userStatusEnum.getStatusCode(),userStatusEnum);
    }
  }

  public static UserStatusEnum getUserStatusEnum(Integer code){
    return userStatusEnumMap.get(code);
  }
}
