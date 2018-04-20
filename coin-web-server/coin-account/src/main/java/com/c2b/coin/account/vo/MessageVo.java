package com.c2b.coin.account.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.account.feign.UserClient;
import com.c2b.coin.account.util.SpringUtil;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.MessageEnum;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.util.HashMap;
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

  public static MessageVo initMessageVo(Long userId, MessageEnum messageEnum, String userName, String coinType, BigDecimal amount){
    //发送mq
    MessageVo messageVo = new MessageVo();
    messageVo.setToUser(String.valueOf(userId));
    Map<String,Object> data = new HashMap<String, Object>();
    data.put("date", DateUtil.formateFullDate(DateUtil.getCurrentDate(), LocaleContextHolder.getLocale()));
    data.put("coinType", coinType);
    data.put("count", amount);
    messageVo.setMessageType(messageEnum.getName());
    messageVo.setUsername(userName);
    messageVo.setData(data);
    messageVo.setLanguage(LocaleContextHolder.getLocale().toLanguageTag());
    return messageVo;
  }
}
