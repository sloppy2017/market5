package com.c2b.coin.common.enumeration;

import java.util.HashMap;
import java.util.Map;

public enum AuthStatusEnum {
  NO_AUTH(0, "未认证"), AUTH_SUCCESS(2, "认证成功"), AUTH_FAILURE(3, "认证失败"),AUTH_ING(1, "认证中");
  private int statusCode;
  private String statusName;

  AuthStatusEnum(int statusCode, String statusName) {
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

  private static final Map<Integer, AuthStatusEnum> authStatusEnumMap = new HashMap<>();

  static {
    for (AuthStatusEnum authStatusEnum : AuthStatusEnum.values()) {
      authStatusEnumMap.put(authStatusEnum.getStatusCode(), authStatusEnum);
    }
  }

  public static AuthStatusEnum getUserStatusEnum(Integer code) {
    return authStatusEnumMap.get(code);
  }
}
