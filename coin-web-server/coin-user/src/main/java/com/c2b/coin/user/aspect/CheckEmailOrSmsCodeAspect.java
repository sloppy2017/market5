package com.c2b.coin.user.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class CheckEmailOrSmsCodeAspect {
  @Pointcut("@annotation(com.c2b.coin.user.annotation.CheckEmailOrSmsCode)")
  public void logPointCut() {

  }
}
