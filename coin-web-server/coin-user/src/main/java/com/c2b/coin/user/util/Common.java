package com.c2b.coin.user.util;

public class Common {
  public enum VerfiyType {
    SECOND_VALID_SMS, // 二次验证短信验证码
    SECOND_VALID_GOOGLE, // 二次验证google验证码
    BIND_MOBILE_VALID_SMS, // 绑定手机验证码
    WITHDRAW_SMS,
    CLOSE_SECOND_VALID_SMS, // 关闭二次验证
    QUICK_REGISTER_SMS,
    UNBIND_MOBILE_VALID_SMS; // 绑定google验证码

  }
}
