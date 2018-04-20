package com.c2b.coin.user.annotation;

import java.lang.annotation.*;

/**
 * 校验网易云盾验证码
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckVerfiyCode {
	String value() default "";
}
