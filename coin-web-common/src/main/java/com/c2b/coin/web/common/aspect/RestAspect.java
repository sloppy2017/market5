package com.c2b.coin.web.common.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
@Order(1)
public class RestAspect {
  private Logger logger = LoggerFactory.getLogger(RestAspect.class);
  private LocalVariableTableParameterNameDiscoverer discoverer ;
  @Pointcut("execution(* com.c2b.coin..*.*Rest.*(..))")
  public void logPointCut() {

  }

  @Around("logPointCut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    discoverer = new LocalVariableTableParameterNameDiscoverer();
    Object[] args = point.getArgs();
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String uri = request.getRequestURI();
    EvaluationContext context = new StandardEvaluationContext();
    MethodSignature signature = (MethodSignature) point.getSignature();
    Method method = signature.getMethod();

    try{
      String[] params = discoverer.getParameterNames(method);
      for (int len = 0; len < params.length; len++) {
        context.setVariable(params[len], args[len]);
      }
      if (null != args && args.length != 0) {
        String argsStr = JSON.toJSONString(context);
        logger.info("===============================RestAspectLog:url【{}】传入参数【{}】", uri, argsStr);
      }

    }catch (Exception e){
      logger.error(e.getLocalizedMessage());
    }

    Object result = point.proceed();
    try {
//      logger.info("===============================RestAspectLog:url【{}】返回结果【{}】", uri, JSON.toJSONString(result));
    } catch (Exception e) {
      logger.error(e.getLocalizedMessage());
    }
    return result;
  }
}
