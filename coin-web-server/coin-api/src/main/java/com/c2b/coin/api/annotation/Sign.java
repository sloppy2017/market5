package com.c2b.coin.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 签名的注解，如果接口需要签名，那么在方法前使用此注解
 *
 * @auther: tangwei
 * @date: 2018/5/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sign {

}
