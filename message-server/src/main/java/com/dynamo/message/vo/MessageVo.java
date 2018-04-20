package com.dynamo.message.vo;

import java.util.Map;

public class MessageVo {
  private String system;
  private String toUser;
  private String username;
  private String language;
  private String messageType;
  private String regionCode;
  private Map<String,Object> data;

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getToUser() {
    return toUser;
  }

  public void setToUser(String toUser) {
    this.toUser = toUser;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  public String getRegionCode() {
    return regionCode;
  }

  public void setRegionCode(String regionCode) {
    this.regionCode = regionCode;
  }
}
