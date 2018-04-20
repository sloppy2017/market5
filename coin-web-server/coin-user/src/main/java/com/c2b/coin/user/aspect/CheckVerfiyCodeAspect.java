package com.c2b.coin.user.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.user.annotation.CheckUserStatus;
import com.c2b.coin.user.annotation.CheckVerfiyCode;
import com.c2b.coin.user.service.WYYunDunService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(1)
public class CheckVerfiyCodeAspect {
  @Autowired
  WYYunDunService wyYunDunService;
  @Autowired
  MessageSource messageSource;
  @Autowired
  private Environment environment;
  @Pointcut("@annotation(com.c2b.coin.user.annotation.CheckVerfiyCode)")
  public void logPointCut() {

  }

  @Around("logPointCut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    if (!environment.acceptsProfiles("dev")){
      MethodSignature signature = (MethodSignature) point.getSignature();
      Method method = signature.getMethod();
      Object[] args = point.getArgs();
      CheckVerfiyCode checkUserStatus = method.getAnnotation(CheckVerfiyCode.class);
      String spel = checkUserStatus.value();
      SpelParse spelParse = new SpelParse();
      String verfiyCode = spelParse.getStringValue(method, args, spel);
      if (!wyYunDunService.checkValid(verfiyCode)) {
        return AjaxResponse.failure(ErrorMsgEnum.USER_VALIDATE_CODE_ERROR.getCode(),messageSource.getMessage(ErrorMsgEnum.USER_VALIDATE_CODE_ERROR.name(), null, LocaleContextHolder.getLocale()));
      }
    }
    Object result = point.proceed();
    return result;
  }
}
