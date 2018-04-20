package com.c2b.coin.user.annotation;

import java.lang.annotation.*;

/**
 * 检查用户状态注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckUserStatus {
	String[] value() default "";
	String username() default "";
}
