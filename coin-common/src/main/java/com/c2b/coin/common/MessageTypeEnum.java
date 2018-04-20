package com.c2b.coin.common;

public enum MessageTypeEnum {
  SMS(0,"SMS"),EMAIL(1,"EMAIL"),GOOGLE(2,"GOOGLE");
  private int code;
  private String name;
  private MessageTypeEnum(int code, String name) {
    this.code = code;
    this.name = name;
  }
  public int getCode() {
    return code;
  }
  public void setCode(int code) {
    this.code = code;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
}
