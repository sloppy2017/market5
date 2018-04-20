package com.c2b.coin.user.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.Constants;
import com.c2b.coin.user.annotation.LoginSecondVerfiy;
import com.c2b.coin.web.common.RedisUtil;
import com.c2b.coin.web.common.UserAgent;
import cz.mallat.uasparser.UserAgentInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
@Order(2)
public class RedisLoginSecondVerfiyspect {
  @Autowired
  RedisUtil redisUtil;


  @Pointcut("@annotation(com.c2b.coin.user.annotation.LoginSecondVerfiy)")
  public void logPointCut() {

  }

  @Around("logPointCut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    Object result = point.proceed();
    String resultStr = JSON.toJSONString(result);
    if ("{\"success\":true}".equals(resultStr)) {
      //请求的参数
      MethodSignature signature = (MethodSignature) point.getSignature();
      Method method = signature.getMethod();
      Object[] args = point.getArgs();
      LoginSecondVerfiy loginSecondVerfiy = method.getAnnotation(LoginSecondVerfiy.class);
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
      String username = request.getHeader(Constants.HTTP_HEADER_USER_NAME);
      redisUtil.set(Constants.REDIS_USER_LOGIN_VERFIY_TYPE + username, loginSecondVerfiy.value());
      String token = request.getHeader(Constants.HTTP_HEADER_TOKEN);
      UserAgentInfo userAgentInfo = UserAgent.userAgent(request);
      if (userAgentInfo.getDeviceType().toLowerCase().contains("computer")) {
        redisUtil.set(Constants.REDIS_USER_LOGIN_SECOND_VERFIY_KEY + token, "1", Constants.REDIS_PC_USER_LOGIN_SECOND_VERFIY_KEY);
      } else {
        redisUtil.set(Constants.REDIS_USER_LOGIN_SECOND_VERFIY_KEY + token, "1", Constants.REDIS_APP_USER_LOGIN_SECOND_VERFIY_KEY);
      }
    }
    return result;
  }
}
