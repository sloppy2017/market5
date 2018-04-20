package com.c2b.coin.trade.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MarketingActivity {
  String[] value() default "";
}
