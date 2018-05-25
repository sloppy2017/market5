package com.c2b.coin.user.service;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.GoogleAuthenticator;
import com.c2b.coin.user.entity.UserInfo;
import com.c2b.coin.user.util.Common;
import com.coin.config.cache.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class VerifiyService {
  @Autowired
  RedisUtil redisUtil;

  public boolean verfiyCode(UserInfo userInfo, Common.VerfiyType verfiyType, String code) {
    switch (verfiyType) {
      case SECOND_VALID_SMS:
        return secondValidSms(userInfo.getMobile(), code);
      case SECOND_VALID_GOOGLE:
        return secondValidGoogle(userInfo.getGoogleSk(), code);
      case BIND_MOBILE_VALID_SMS:
        return bindMobileValidCode(userInfo.getUsername(), code);
      case UNBIND_MOBILE_VALID_SMS:
        return unBindMobileValidCode(userInfo.getUsername(), code);
      case WITHDRAW_SMS:
        return withdraw(userInfo.getUsername(), code);
      case QUICK_REGISTER_SMS:
        return quickSms(userInfo.getMobile(), code);
//      case CLOSE_SECOND_VALID_SMS:
//        return closeSecondValidCode(userInfo.getMobile(), code);
      default:
        return false;
    }

  }

  private boolean quickSms(String mobile, String code) {
    return false;
  }

//  private boolean closeSecondValidCode(String mobile, String code) {
//    String redisCode = redisUtil.get(Constants.REDIS_SECOND_VALID_SMS_CODE_KEY + mobile);
//    if (StringUtils.isEmpty(redisCode))
//      return false;
//    boolean flag = redisCode.equals(code);
//    if (flag) {
//      redisUtil.delKey(Constants.REDIS_SECOND_VALID_SMS_CODE_KEY + mobile);
//    }
//    return flag;
//  }

  private boolean withdraw(String username, String code) {
    return verfiySmsCode(Constants.REDIS_WITHDRAW_SMS_CODE_KEY + username, code);
  }
  private boolean secondValidSms(String mobile, String code) {
    String redisCode = redisUtil.get(Constants.REDIS_SECOND_VALID_SMS_CODE_KEY + mobile);
    if (StringUtils.isEmpty(redisCode))
      return false;
    boolean flag = redisCode.equals(code);
    if (flag) {
      redisUtil.delKey(Constants.REDIS_SECOND_VALID_SMS_CODE_KEY + mobile);
    }
    return flag;
  }

  private boolean secondValidGoogle(String sk, String code) {
    GoogleAuthenticator ga = new GoogleAuthenticator();
    ga.setWindowSize(1);
    return ga.check_code(sk, Long.parseLong(code), DateUtil.getCurrentTimestamp());
  }

  private boolean bindMobileValidCode(String username, String code) {
    return verfiySmsCode(Constants.REDIS_BIND_MOBILE_KEY + username, code);
  }

  private boolean unBindMobileValidCode(String username, String code) {
    return verfiySmsCode(Constants.REDIS_UNBIND_MOBILE_KEY + username, code);
  }

  public boolean verfiySmsCode(String key, String value) {
    String result = redisUtil.get(key);
    if (StringUtils.isEmpty(result)) {
      return false;
    }
    JSONObject jo = JSONObject.parseObject(result);
    if (!jo.getString("token").equals(value)) {
      return false;
    }
    redisUtil.delKey(key);
    return true;
  }
}
