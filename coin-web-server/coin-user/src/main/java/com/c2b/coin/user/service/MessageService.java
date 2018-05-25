package com.c2b.coin.user.service;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.RandNumUtil;
import com.c2b.coin.common.SecurityUtil;
import com.c2b.coin.common.MessageEnum;
import com.c2b.coin.user.entity.UserInfo;
import com.c2b.coin.user.util.MqUtil;
import com.c2b.coin.user.vo.MessageVo;
import com.c2b.coin.cache.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class MessageService {
  @Value("${coin.resetpwd}")
  public String resetpwdUrl;
  @Value("${coin.active}")
  public String activeUrl;

  @Value("${coin.system}")
  private String systemName;
  @Autowired
  private MqUtil mqUtil;
  @Autowired
  RedisUtil redisUtil;
  @Autowired
  ThreadPoolTaskExecutor taskExecutor;
  @Autowired
  protected HttpServletRequest request;

  private void sendEmailAndCache(UserInfo userInfo, MessageEnum messageEnum, Locale locale) {
    // 生成激活token
    Map<String, Object> cache = createCache(userInfo);
    String token = SecurityUtil.authentication(cache);
    String url = saveEmailRedis(messageEnum, token, cache);
    Map<String, Object> param = createMap("token", token, "url",url, "email", userInfo.getEmail());
    // 发送消息
    sendMessage(userInfo.getUsername(), userInfo.getRegionCode(), userInfo.getEmail(), param, messageEnum,locale);

  }

  public void sendMessage(String username, String regionCode, String touser, Map<String, Object> param, MessageEnum messageEnum, Locale locale) {
    taskExecutor.execute(() -> {
      MessageVo messageVo = new MessageVo();
      messageVo.setSystem(systemName);
      messageVo.setLanguage(locale.toLanguageTag());
      messageVo.setMessageType(messageEnum.getName());
      messageVo.setToUser(touser);
      messageVo.setUsername(username);
      messageVo.setData(param);
      messageVo.setRegionCode(regionCode);
      mqUtil.sendMessageQueue(messageVo);
    });
  }

  public void sendSmsAndCache(UserInfo userInfo,String key, MessageEnum messageEnum, Locale locale) {
    Map<String, Object> cache = createCache(userInfo);
    String token = RandNumUtil.getRandNum(6);
    Map<String, Object> param = createMap("token", token, "mobile", userInfo.getMobile(), "regionCode", userInfo.getRegionCode());
    cache.put("mobile", userInfo.getMobile());
    cache.put("token", token);
    cache.put("regionCode", userInfo.getRegionCode());
    saveSmsRedis(messageEnum, key, cache);
    // 发送消息
    sendMessage(userInfo.getUsername(), userInfo.getRegionCode(), userInfo.getMobile(), param, messageEnum,locale);
  }

  public void sendRegSmsAndCache(String regionCode, String mobile, Locale local) {
    String random = RandNumUtil.getRandNum(6);
    Map<String, Object> param = createMap("token", random, "mobile", mobile, "regionCode", regionCode);
    saveSmsRedis(MessageEnum.SMS_QUICK_REGISTER, mobile, param);
    sendMessage("", regionCode, mobile, param, MessageEnum.SMS_QUICK_REGISTER, local);
  }

  private Map<String, Object> createCache(UserInfo userInfo) {
    return createMap("userId", userInfo.getId(), "username", userInfo.getUsername(), "createtime", DateUtil.getCurrentDate(), "email", userInfo.getEmail());
  }

  private Map<String, Object> createMap(Object... obj) {
    Map<String, Object> map = new HashMap<>();
    for (int i = 0; i < obj.length; i += 2) {
      map.put(String.valueOf(obj[i]), obj[i + 1]);
    }
    return map;
  }

  private void saveSmsRedis(MessageEnum messageEnum, String key, Map<String, Object> cache) {
    switch (messageEnum) {
      case SMS_BIND_MOBILE:
        redisUtil.set(Constants.REDIS_BIND_MOBILE_KEY + key, JSONObject.toJSONString(cache), Constants.REDIS_BIND_MOBILE_KEY_EXPIRE);
        break;
      case SMS_UNBIND_MOBILE:
        redisUtil.set(Constants.REDIS_UNBIND_MOBILE_KEY + key, JSONObject.toJSONString(cache), Constants.REDIS_UNBIND_MOBILE_KEY_EXPIRE);
        break;
      case SMS_WITHDRAW:
        redisUtil.set(Constants.REDIS_WITHDRAW_SMS_CODE_KEY + key, JSONObject.toJSONString(cache), Constants.REDIS_WITHDRAW_SMS_CODE_KEY_EXPIRE);
        break;
      case SMS_QUICK_REGISTER:
        redisUtil.set(Constants.REDIS_REG_SMS_CODE_KEY + key, JSONObject.toJSONString(cache), Constants.REDIS_REG_SMS_CODE_KEY_EXPIRE);
        break;
      case SMS_RESET_PASSWORD:
        redisUtil.set(Constants.REDIS_SMS_RESET_PWD_KEY + key, JSONObject.toJSONString(cache), Constants.REDIS_SMS_RESET_PWD_KEY_EXPIRE);
        break;
      default:
        return;
    }
  }

  private String saveEmailRedis(MessageEnum messageEnum, String token, Map<String, Object> cache) {
    String url = "";
    switch (messageEnum) {
      case EMAIL_RESET_PASSWORD:
        url = resetpwdUrl + "?token=" + token;
        redisUtil.set(Constants.REDIS_RESET_PASSWORD_KEY + token, JSONObject.toJSONString(cache), Constants.REDIS_EMAIL_KEY_EXPIRE);
        break;
      case EMAIL_ACTIVE:
        url = activeUrl + "?token=" + token;
        redisUtil.set(Constants.REDIS_ACTIVE_EMAIL_KEY + token, JSONObject.toJSONString(cache), Constants.REDIS_EMAIL_KEY_EXPIRE);
        break;
      default:
        return "";
    }
    return url;
  }


   void sendMessage(UserInfo userInfo, MessageEnum messageEnum, Locale locale) {
    Map<String, Object> param = new HashMap<>();
    param.put("date", DateUtil.formateFullDate(DateUtil.getCurrentDate(), locale));
    switch (messageEnum) {
      case EMAIL_CHANGE_LOGIN_PWD:
      case EMAIL_CHANGE_PAY_PWD:
      case EMAIL_UNBIND_GOOGLE:
      case EMAIL_UNBIND_MOBILE:
        param.put("email", userInfo.getEmail().replaceAll("(\\w{2}).+(\\w{2}@+)", "$1****$2"));
        break;
      case EMAIL_AUTH_FAILURE:
      case EMAIL_AUTH_SUCCESS:
        break;
    }
    sendMessage(userInfo.getUsername(), "", userInfo.getEmail(), param, messageEnum,locale);
  }

  public void sendActiveEmail(UserInfo userInfo, Locale locale) {
    sendEmailAndCache(userInfo, MessageEnum.EMAIL_ACTIVE, locale);
  }

  public void sendQuickRegSuccessSms(UserInfo userInfo, String password, Locale locale) {
    Map<String, Object> param = new HashMap<>();
    param.put("token", password);
    sendMessage(userInfo.getUsername(), userInfo.getRegionCode(), userInfo.getMobile(), param, MessageEnum.SMS_QUICK_REGISTER_SUCCESS, locale);
  }
  public void sendResetSuccessSms(UserInfo userInfo, String password, Locale locale) {
    Map<String, Object> param = new HashMap<>();
    param.put("token", password);
    sendMessage(userInfo.getUsername(), userInfo.getRegionCode(), userInfo.getMobile(), param, MessageEnum.SMS_RESET_PASSWORD_SUCCESS, locale);
  }

  public void sendResetPwdEmail(String email, Locale locale) {
    UserInfo userInfo = new UserInfo();
    userInfo.setEmail(email);
    sendEmailAndCache(userInfo, MessageEnum.EMAIL_RESET_PASSWORD, locale);
  }

  public void sendResetPwdSms(String regionCode, String mobile, Locale locale) {
    UserInfo userInfo = new UserInfo();
    userInfo.setRegionCode(regionCode);
    userInfo.setMobile(mobile);
    sendSmsAndCache(userInfo, mobile, MessageEnum.SMS_RESET_PASSWORD, locale);
  }

  public void sendUnbindMobileEmail(UserInfo userInfo, Locale locale) {
    sendMessage(userInfo, MessageEnum.EMAIL_UNBIND_MOBILE,locale);
  }

  public void sendUnbindGoogleEmail(UserInfo userInfo, Locale locale) {
    sendMessage(userInfo, MessageEnum.EMAIL_UNBIND_GOOGLE, locale);
  }

  public void sendChangePwdEmail(UserInfo userInfo, Locale locale) {
    sendMessage(userInfo, MessageEnum.EMAIL_CHANGE_LOGIN_PWD, locale);
  }

  public void sendChangePayPwdEmail(UserInfo userInfo, Locale locale) {
    sendMessage(userInfo, MessageEnum.EMAIL_CHANGE_PAY_PWD, locale);
  }

  public void sendAuthSuccessEmail(UserInfo userInfo, Locale locale) {
    sendMessage(userInfo, MessageEnum.EMAIL_AUTH_SUCCESS, locale);
  }

  public void sendAuthFailureEmail(UserInfo userInfo, Locale locale) {
    sendMessage(userInfo, MessageEnum.EMAIL_AUTH_FAILURE, locale);
  }

  public void sendLoginEmail(UserInfo userInfo, Locale locale){ sendMessage(userInfo, MessageEnum.EMAIL_LOGIN_SUCCESS, locale);}
}
