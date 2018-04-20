package com.c2b.coin.common.enumeration;

public enum StatusEnum {
  INVALID(0,"无效"),VALID(1,"有效"),UNBIND(0,"未绑定"),BIND(1,"已绑定");
  private int statusCode;
  private String statusName;
  private StatusEnum(int statusCode, String statusName){
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
}
