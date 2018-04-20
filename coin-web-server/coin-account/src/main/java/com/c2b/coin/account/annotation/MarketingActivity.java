package com.c2b.coin.account.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MarketingActivity {
  String[] value() default "";
}
