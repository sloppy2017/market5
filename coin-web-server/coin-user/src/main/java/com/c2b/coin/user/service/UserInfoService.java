package com.c2b.coin.user.service;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.*;
import com.c2b.coin.common.enumeration.*;
import com.c2b.coin.user.annotation.MarketingActivity;
import com.c2b.coin.user.entity.UserInfo;
import com.c2b.coin.user.entity.UserRegInfo;
import com.c2b.coin.user.mapper.UserInfoMapper;
import com.c2b.coin.user.mapper.UserNewsMapper;
import com.c2b.coin.user.mapper.UserRegInfoMapper;
import com.c2b.coin.user.util.Common;
import com.c2b.coin.cache.redis.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cz.mallat.uasparser.UserAgentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import java.util.regex.Pattern;

@Service
public class UserInfoService {
  @Autowired
  UserInfoMapper userInfoMapper;
  @Autowired
  UserRegInfoMapper userRegInfoMapper;
  @Autowired
  RedisUtil redisUtil;
  @Autowired
  MessageService messageService;
  @Autowired
  VerifiyService verifiyService;
  @Autowired
  UserNewsMapper userNewsMapper;
  @Autowired
  UserIdService userIdService;

  public List<UserInfo> findUserByUsernameOrEmailOrMobile(String property) {
    List<UserInfo> userInfoList = null;
    if (Pattern.matches(Constants.REGEX_EMAIL, property)) {
      userInfoList = findUserByEmail(property);
    } else if (Pattern.matches(Constants.REGEX_MOBILE, property)) {
      userInfoList = findUserByMobile(property);
    } else {
      userInfoList = findUserByUsername(property);
    }
    return userInfoList;
  }

  //  @Cacheable(value = "user", key = "#root.targetClass+'_'+#username", unless = "#result eq null")
  private List<UserInfo> findUserByUsername(String username) {
    UserInfo userInfo = new UserInfo();
    userInfo.setUsername(username);
    return findUser(userInfo);
  }

  private List<UserInfo> findUserByEmail(String email) {
    UserInfo userInfo = new UserInfo();
    userInfo.setEmail(email);
    return findUser(userInfo);
  }

  private List<UserInfo> findUserByMobile(String mobile) {
    UserInfo userInfo = new UserInfo();
    userInfo.setMobile(mobile);
    return findUser(userInfo);
  }

  public List<UserInfo> findUser(UserInfo userInfo) {
    return userInfoMapper.select(userInfo);
  }

  public UserInfo findUserById(String userId) {
    return userInfoMapper.selectByPrimaryKey(Long.parseLong(userId));
  }

  public List<UserInfo> findUserInfoByMobile(String mobile){
    UserInfo userInfo = new UserInfo();
    userInfo.setMobile(mobile);
    return userInfoMapper.select(userInfo);
  }

  public Map<String, Object> findUser(String userId) {
    return userInfoMapper.selectByUserId(Long.parseLong(userId));
  }

  public int activeUser(String userId) {
    UserInfo userInfo = new UserInfo();
    userInfo.setId(Long.parseLong(userId));
    userInfo.setUserStatus(UserStatusEnum.NORMAL.getStatusCode());
    return userInfoMapper.updateByPrimaryKeySelective(userInfo);
  }

  public int updateUser(UserInfo userInfo) {
    return userInfoMapper.updateByPrimaryKeySelective(userInfo);
  }

  /**
   * 注册用户
   *
   * @param username
   * @param password
   * @param ip
   * @return
   */
  @MarketingActivity(value={MarketingActivityType.REGISTER})
  public UserInfo saveUser(String username, String mobile, String email, String password, String inviteCode, String deviceId, String ip,
                           String channel, String appVersion, String activityCode, String remark, UserAgentInfo userAgentInfo,String regionCode, Locale locale) {
//    String address = AddressUtils.getAddress(ip, Constants.ENCODING_UTF8);
    Long currentTime = DateUtil.getCurrentTimestamp();
//    String username = userIdService.next();
    UserInfo userInfo = saveUser(username, mobile, email, password,regionCode, currentTime);
    // todo 新增用户来源信息
    saveUserInfoSource(userInfo, inviteCode, currentTime, channel, deviceId, ip, appVersion, Long.parseLong(activityCode), remark, userAgentInfo, locale.toLanguageTag());
    return userInfo;
  }

  private UserInfo saveUser(String username, String mobile, String email, String password,String regionCode, Long currentTime) {
    String address = "";
    UserInfo userInfo = new UserInfo();
    userInfo.setUsername(username);
    userInfo.setPassword(EncryptUtil.encrypt(password));
    userInfo.setEmail(email);
    userInfo.setMobile(mobile);
    int userStatus = 0;
    if (!StringUtils.isEmpty(email)) {
      userStatus = UserStatusEnum.NOT_ACTTIVE.getStatusCode();
    } else if (!StringUtils.isEmpty(mobile)) {
      userStatus = UserStatusEnum.NORMAL.getStatusCode();
    }
    userInfo.setRemark(LocaleContextHolder.getLocale().toLanguageTag());
    userInfo.setUserStatus(userStatus);
    userInfo.setGoogleBindStatus(StatusEnum.UNBIND.getStatusCode());
//    userInfo.setInviteCode(inviteCode);
    userInfo.setIsAuth(AuthStatusEnum.NO_AUTH.getStatusCode());
    userInfo.setLastUpdateTime(DateUtil.getCurrentTimestamp());
    userInfo.setRegAddress(address);
    userInfo.setStatus(StatusEnum.VALID.getStatusCode());
    userInfo.setTwoValid(StatusEnum.UNBIND.getStatusCode());
    userInfo.setCreatetime(currentTime);
    userInfo.setRegionCode(regionCode);
    int result = userInfoMapper.insert(userInfo);
    return userInfo;
  }


  @Async
  public int saveUserInfoSource(UserInfo userInfo, String inveteCode, Long time, String channel, String deviceId, String ip, String appVersion, Long activityCode, String remark, UserAgentInfo userAgentInfo, String language) {
    UserRegInfo userRegInfo = new UserRegInfo();
    userRegInfo.setActiviyCode(activityCode);
    userRegInfo.setAppVersion(appVersion);
    userRegInfo.setChannel(channel);
    userRegInfo.setCreatetime(time);
    userRegInfo.setDeviceId(deviceId);
    userRegInfo.setInviteCode(inveteCode);
    userRegInfo.setIp(ip);
    userRegInfo.setRemark(remark);
    userRegInfo.setRom(userAgentInfo.getOsFamily());
    userRegInfo.setRomVersion(userAgentInfo.getOsName());
    userRegInfo.setTerminal(userAgentInfo.getDeviceType());
    userRegInfo.setUserId(userInfo.getId());
    userRegInfo.setUsername(userInfo.getUsername());
    userRegInfo.setRemark(language);
    return userRegInfoMapper.insert(userRegInfo);
  }

  public Object bindMobile(UserInfo userInfo, String code) {
    if (!StringUtils.isEmpty(userInfo.getMobile())) {
      return ErrorMsgEnum.MOBILE_EXISTS;
    }
    String result = redisUtil.get(Constants.REDIS_BIND_MOBILE_KEY + userInfo.getUsername());
    if (StringUtils.isEmpty(result)) {
      return ErrorMsgEnum.USER_MOBILE_BIND_ERROR;
    }

    JSONObject jo = JSONObject.parseObject(result);
    if (!jo.getString("token").equals(code)) {
      return ErrorMsgEnum.USER_MOBILE_BIND_ERROR;
    }
    String mobile = jo.getString("mobile");
    List<UserInfo> userInfoList = findUserInfoByMobile(mobile);
    if (null != userInfoList && userInfoList.size() != 0) {
      return ErrorMsgEnum.USER_MOBILE_BIND_ERROR;
    }
    redisUtil.delKey(Constants.REDIS_BIND_MOBILE_KEY + userInfo.getUsername());
    userInfo.setMobile(mobile);
    userInfo.setRegionCode(jo.getString("regionCode"));
    userInfo.setTwoValid(StatusEnum.BIND.getStatusCode());
    redisUtil.delKey(Constants.REDIS_BIND_MOBILE_KEY + userInfo.getUsername());
    return userInfoMapper.updateByPrimaryKeySelective(userInfo);

  }

  public Object unBindMobile(UserInfo userInfo, String bizType, String code, Locale locale) {
    boolean flag = verifiyService.verfiyCode(userInfo, Common.VerfiyType.UNBIND_MOBILE_VALID_SMS, code);
    if (!flag) {
      return ErrorMsgEnum.USER_MOBILE_BIND_ERROR;
    }
    if (StringUtils.isEmpty(userInfo.getEmail())){
      return ErrorMsgEnum.USER_UNABLE_UNBIND_MOBILE;
    }
    userInfo.setMobile("");
    userInfo.setRegionCode("");
    if (userInfo.getGoogleBindStatus().equals(StatusEnum.UNBIND.getStatusCode())) {
      userInfo.setTwoValid(StatusEnum.UNBIND.getStatusCode());
    }
    messageService.sendUnbindMobileEmail(userInfo, locale);
    return userInfoMapper.updateByPrimaryKeySelective(userInfo);

  }

  public Object bindGoogle(UserInfo userInfo, String bizType, String code, Locale locale) {
    boolean flag = verifiyService.verfiyCode(userInfo, Common.VerfiyType.SECOND_VALID_GOOGLE, code);
    if (!flag) {
      return ErrorMsgEnum.USER_GOOGLE_BIND_ERROR;
    }
    if (userInfo.getGoogleBindStatus() == StatusEnum.UNBIND.getStatusCode()) {
      // 0/1 绑定/解绑
      if ("0".equals(bizType)) {
        userInfo.setGoogleBindStatus(StatusEnum.BIND.getStatusCode());
        userInfo.setTwoValid(StatusEnum.BIND.getStatusCode());
        return userInfoMapper.updateByPrimaryKeySelective(userInfo);
      } else {
        return ErrorMsgEnum.USER_GOOGLE_BIND_ERROR;
      }
    } else {
      // 0/1 绑定/解绑
      if ("1".equals(bizType)) {
        userInfo.setGoogleBindStatus(StatusEnum.UNBIND.getStatusCode());
        if (StringUtils.isEmpty(userInfo.getMobile())) {
          userInfo.setTwoValid(StatusEnum.UNBIND.getStatusCode());
        }
        messageService.sendMessage(userInfo, MessageEnum.EMAIL_UNBIND_GOOGLE, locale);
        return userInfoMapper.updateByPrimaryKeySelective(userInfo);
      } else {
        return ErrorMsgEnum.USER_GOOGLE_BIND_ERROR;
      }
    }
  }

  public Object setGoogleSk(String userId) {
    UserInfo userInfo = userInfoMapper.selectByPrimaryKey(Long.parseLong(userId));
    // 判断用户是否已绑定google验证码
    if (!StringUtils.isEmpty(userInfo.getGoogleBindStatus())) {
      if (userInfo.getGoogleBindStatus() == StatusEnum.BIND.getStatusCode()) {
        return ErrorMsgEnum.USER_ALREADY_BIND_GOOGLE;
      }
    }

    String secret = GoogleAuthenticator.generateSecretKey();
    String username = StringUtils.isEmpty(userInfo.getEmail()) ? userInfo.getMobile() : userInfo.getEmail();
    String qrcode = GoogleAuthenticator.getQRBarcode(username, secret);
    userInfo.setGoogleSk(secret);
    userInfoMapper.updateByPrimaryKeySelective(userInfo);
    Map<String, Object> result = new HashMap<>();
    result.put("username", username);
    result.put("sk", secret);
    result.put("qrcode", qrcode);
    return result;
  }

  public Object modifyLoginPwd(UserInfo userInfo, String oldPwd, String newPwd, String confirmPwd, Locale locale) {
    if (EncryptUtil.encrypt(oldPwd).equals(userInfo.getPassword())) {
      if (oldPwd.equals(newPwd)){
        return ErrorMsgEnum.USER_NEW_OLD_PWD_UNABLE_SAME;
      }
      if (newPwd.equals(confirmPwd)) {
        userInfo.setPassword(EncryptUtil.encrypt(newPwd));
        int result = userInfoMapper.updateByPrimaryKeySelective(userInfo);
        messageService.sendChangePwdEmail(userInfo, locale);
        return result;
      }
      return ErrorMsgEnum.USER_PWD_NOT_SAME;
    } else {
      return ErrorMsgEnum.USER_LOGIN_PWD_ERROR;
    }
  }

  public Object modifyPayPwd(UserInfo userInfo, String oldPwd, String newPwd, String confirmPwd, Locale locale) {
    if (EncryptUtil.encrypt(oldPwd).equals(userInfo.getPayPwd())) {
      if (newPwd.equals(confirmPwd)) {
        userInfo.setPayPwd(EncryptUtil.encrypt(newPwd));
        redisUtil.set(Constants.REDIS_USER_CHANGE_PAY_PWD_KEY + userInfo.getId(), "1", Constants.REDIS_USER_CHANGE_PAY_PWD_KEY_EXPIRE);
        int result = userInfoMapper.updateByPrimaryKeySelective(userInfo);
        messageService.sendChangePayPwdEmail(userInfo, locale);
        return result;
      }
      return ErrorMsgEnum.USER_PWD_NOT_SAME;
    } else {
      return ErrorMsgEnum.USER_PAY_PWD_ERROR;
    }
  }

  public String loginToken(UserInfo userInfo, Map<String, Object> cache, UserAgentInfo userAgentInfo) {
    cache.put("id", userInfo.getId());
    cache.put("loginTime", DateUtil.getCurrentDate());
    cache.put("terminal", userAgentInfo.getDeviceType());
    String token = SecurityUtil.authentication(cache);
    cache.put("token", token);
    // 存入redis
    redisUtil.set(Constants.REDIS_USER_TKOEN_KEY + token, JSONObject.toJSONString(cache), Constants.REDIS_USER_TOKEN_KEY_EXPIRE);
    // 存入用户所有生成的token
    redisUtil.sadd(Constants.REDIS_USER_TOKEN_LIST_KEY + userInfo.getUsername(), token);
    return token;
  }

  public void freezeUser(String username) {
    Set<byte[]> set = redisUtil.smember(Constants.REDIS_USER_TOKEN_LIST_KEY + username);
    for (byte[] token : set) {
      redisUtil.delKey(Constants.REDIS_USER_TKOEN_KEY + new String(token));
    }
  }

  public PageInfo<Map<String, Object>> findUserNews(String userId, String page, String rows) {
    PageHelper.startPage(Integer.valueOf(page), Integer.valueOf(rows));
    List<Map<String, Object>> list = userNewsMapper.findUserNews(Long.parseLong(userId));
    return new PageInfo(list);
  }

  public int countNoRead(String userId) {
    Map<String, Object> result = userNewsMapper.countNoRead(Long.parseLong(userId));
    return Integer.valueOf(String.valueOf(result.get("count")));
  }

  public void clearUserNews(String userId) {
    userNewsMapper.clearUserNews(Long.parseLong(userId));
  }

  public void readAllNews(String userId) {
    userNewsMapper.readAllUserNews(Long.parseLong(userId));
  }

  public void readNews(String userId, String id) {
    userNewsMapper.readUserNews(Long.parseLong(userId), Long.parseLong(id));
  }

  public Object smsResetPwd(String mobile, String smsCode, Locale locale) {
    boolean flag = verifiyService.verfiySmsCode(Constants.REDIS_SMS_RESET_PWD_KEY + mobile, smsCode);
    if (!flag)
      return ErrorMsgEnum.USER_VALIDATE_CODE_ERROR;
    List<UserInfo> userInfos = findUserByUsernameOrEmailOrMobile(mobile);
    if (null == userInfos || userInfos.size() == 0)
      return ErrorMsgEnum.USER_NOT_EXIST;
    UserInfo userInfo = userInfos.get(0);
    String token = RandNumUtil.getRandNum(8);
    userInfo.setPassword(EncryptUtil.encrypt(token));
    userInfoMapper.updateByPrimaryKeySelective(userInfo);
    messageService.sendResetSuccessSms(userInfo, token, locale);
    redisUtil.delKey(Constants.REDIS_SMS_RESET_PWD_KEY + mobile);
    return null;
  }
}
