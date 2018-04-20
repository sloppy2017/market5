package com.c2b.coin.user.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.Base64Util;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.user.entity.UserInfo;
import com.c2b.coin.user.entity.UserOperationLog;
import com.c2b.coin.user.mapper.UserOperationLogMapper;
import com.c2b.coin.user.service.UserInfoService;
import com.c2b.coin.web.common.AddressUtils;
import com.c2b.coin.web.common.IPUtils;
import com.c2b.coin.web.common.annotation.SysLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;


/**
 * 系统日志，切面处理类
 */
@Aspect
@Component
@Order(4)
public class SysLogAspect {
  @Autowired
  private UserOperationLogMapper userOperationLogMapper;
  @Autowired
  private UserInfoService userInfoService;
  @Autowired
  private ThreadPoolTaskExecutor taskExecutor;

  @Pointcut("@annotation(com.c2b.coin.web.common.annotation.SysLog)")
  public void logPointCut() {

  }

  @Around("logPointCut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    long beginTime = DateUtil.getCurrentTimestamp();
    //执行方法
    Object result = point.proceed();
    //执行时长(毫秒)
    long time = DateUtil.getCurrentTimestamp() - beginTime;

    saveSysLog(point, time);
    return result;
  }

  private void saveSysLog(ProceedingJoinPoint joinPoint, long time) throws Exception {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    SysLog syslog = method.getAnnotation(SysLog.class);
    if (null != syslog) {
      UserOperationLog userOperationLog = new UserOperationLog();
      userOperationLog.setOperation(syslog.value());
      String className = joinPoint.getTarget().getClass().getName();
      String methodName = signature.getName();
      userOperationLog.setMethod(className + "." + methodName + "()");
      //请求的参数
      String param = "";
      Object[] args = joinPoint.getArgs();
      if (null != args && args.length != 0) {
        param = Base64Util.encode(JSON.toJSONString(args));
      }
      userOperationLog.setParam(param);
      //获取request
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
      String ua = request.getHeader("User-Agent");
      String ip = IPUtils.getIpAddr(request);
      String userId = request.getHeader(com.c2b.coin.common.Constants.HTTP_HEADER_USER_ID);
      taskExecutor.execute(() -> {
        SpelParse spelParse = new SpelParse();
        if (StringUtils.isEmpty(userId)) {
          List<UserInfo> userInfos = userInfoService.findUserByUsernameOrEmailOrMobile(spelParse.getStringValue(method, args, syslog.username()));
          userOperationLog.setUserId(userInfos.get(0).getId());
          userOperationLog.setUsername(userInfos.get(0).getUsername());
        } else {
          userOperationLog.setUserId(Long.parseLong(userId));
          userOperationLog.setUsername(request.getHeader(com.c2b.coin.common.Constants.HTTP_HEADER_USER_NAME));
        }
        userOperationLog.setAddress(AddressUtils.getAddress(ip, com.c2b.coin.common.Constants.ENCODING_UTF8));
        userOperationLog.setUa(ua);
        userOperationLog.setIp(ip);
        userOperationLog.setCreateTime(DateUtil.getCurrentTimestamp());
        userOperationLogMapper.insert(userOperationLog);
      });


    }
  }
}
