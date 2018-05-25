package com.c2b.coin.user.aspect;

import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.common.enumeration.UserStatusEnum;
import com.c2b.coin.user.annotation.CheckUserStatus;
import com.c2b.coin.user.entity.UserInfo;
import com.c2b.coin.user.service.UserInfoService;
import com.coin.config.cache.redis.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@Order(3)
public class CheckUserStatusAspect {
  @Autowired
  UserInfoService userInfoService;
  @Autowired
  MessageSource messageSource;
  @Autowired
  RedisUtil redisUtil;
  @Value("${coin.login.maxErrorTimes}")
  private int loginMaxErrorTimes;

  @Pointcut("@annotation(com.c2b.coin.user.annotation.CheckUserStatus)")
  public void logPointCut() {

  }

  @Around("logPointCut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    //请求的参数
    MethodSignature signature = (MethodSignature) point.getSignature();
    Method method = signature.getMethod();
    Object[] args = point.getArgs();

    CheckUserStatus checkUserStatus = method.getAnnotation(CheckUserStatus.class);
    String spel = checkUserStatus.username();
    SpelParse spelParse = new SpelParse();
    String username = spelParse.getStringValue(method, args, spel);
    List<UserInfo> userInfoList = userInfoService.findUserByUsernameOrEmailOrMobile(username);
    if (userInfoList.size() != 1) {
      return AjaxResponse.failure(ErrorMsgEnum.USER_NOT_EXIST.getCode(),messageSource.getMessage(ErrorMsgEnum.USER_NOT_EXIST.name(), null, LocaleContextHolder.getLocale()));
    }
    UserInfo userInfo = userInfoList.get(0);
    UserStatusEnum userStatusEnum = UserStatusEnum.getUserStatusEnum(userInfo.getUserStatus());

    String[] check = checkUserStatus.value();
    for (String str : check) {
      if (str.equals(UserStatus.USER_ACTIVE)) {
        if (userStatusEnum.equals(UserStatusEnum.NOT_ACTTIVE)) {
          return AjaxResponse.failure(ErrorMsgEnum.USER_NOT_ACTIVE.getCode(),messageSource.getMessage(ErrorMsgEnum.USER_NOT_ACTIVE.name(), null, LocaleContextHolder.getLocale()));
        }
      } else if (str.equals(UserStatus.USER_FREEZE)) {
        if (userStatusEnum.equals(UserStatusEnum.DISABLE)) {
          return AjaxResponse.failure(ErrorMsgEnum.USER_DISABLE.getCode(),messageSource.getMessage(ErrorMsgEnum.USER_DISABLE.name(), null, LocaleContextHolder.getLocale()));
        }
      }else if (str.equals(UserStatus.USER_LOGIN_ERROR_TIMES)) {
        String times = redisUtil.get(Constants.REDIS_USER_LOGIN_ERROR_TIMES_KEY + userInfo.getUsername());
        if (!StringUtils.isEmpty(times) && !(times.compareTo(String.valueOf(loginMaxErrorTimes)) < 0)) {
          String leftTime = DateUtil.secToTime(redisUtil.ttl(Constants.REDIS_USER_LOGIN_ERROR_TIMES_KEY + userInfo.getUsername()).intValue());
          return  AjaxResponse.failure(ErrorMsgEnum.USER_LOCK.getCode(),String.format(messageSource.getMessage(ErrorMsgEnum.USER_LOCK.name(), null, LocaleContextHolder.getLocale()), leftTime));
        }
      }
    }
    Object result = point.proceed();
    return result;
  }
}
