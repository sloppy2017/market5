package com.c2b.coin.common;

import java.util.HashMap;
import java.util.Map;

public enum MessageEnum {
  SMS_BIND_MOBILE(MessageTypeEnum.SMS,"SMS_BIND_MOBILE",0,"绑定手机"),
  SMS_FORGOT_PASSWORD(MessageTypeEnum.SMS,"SMS_FORGOT_PASSWORD",1,"找回密码"),
  SMS_LOGIN_VERFIY(MessageTypeEnum.SMS,"SMS_LOGIN_VERFIY",2,"登录二次验证"),
  GOOGLE_CODE(MessageTypeEnum.GOOGLE,"GOOGLE_CODE",3,"谷歌验证码"),
  EMAIL_ACTIVE(MessageTypeEnum.EMAIL,"EMAIL_ACTIVE", 4, "激活邮件"),
  EMAIL_RESET_PASSWORD(MessageTypeEnum.EMAIL,"EMAIL_RESET_PASSWORD", 5, "重置密码邮件"),
  SMS_UNBIND_MOBILE(MessageTypeEnum.SMS,"SMS_UNBIND_MOBILE",6,"解绑手机"),
  EMAIL_CHANGE_LOGIN_PWD(MessageTypeEnum.EMAIL,"EMAIL_CHANGE_LOGIN_PWD", 8 ,"修改登录密码"),
  EMAIL_AUTH_FAILURE(MessageTypeEnum.EMAIL, "EMAIL_AUTH_FAILURE", 9 ,"认证失败"),
  EMAIL_AUTH_SUCCESS(MessageTypeEnum.EMAIL, "EMAIL_AUTH_SUCCESS", 10, "认证成功"),
  EMAIL_CHANGE_PAY_PWD(MessageTypeEnum.EMAIL,"EMAIL_CHANGE_PAY_PWD", 11, "修改资金密码"),
  EMAIL_CHARGE_COIN(MessageTypeEnum.EMAIL, "EMAIL_CHARGE_COIN", 12, "充币成功"),
  EMAIL_UNBIND_GOOGLE(MessageTypeEnum.EMAIL, "EMAIL_UNBIND_GOOGLE", 13, "解绑google"),
  EMAIL_UNBIND_MOBILE(MessageTypeEnum.EMAIL, "EMAIL_UNBIND_MOBILE", 14, "解绑手机"),
  EMAIL_WITHDRAW_APPLY(MessageTypeEnum.EMAIL, "EMAIL_WITHDRAW_APPLY", 15, "提现申请"),
  EMAIL_WITHDRAW_FAILURE(MessageTypeEnum.EMAIL, "EMAIL_WITHDRAW_FAILURE", 16, "提现失败"),
  EMAIL_WITHDRAW_SUCCESS(MessageTypeEnum.EMAIL, "EMAIL_WITHDRAW_SUCCESS", 17, "提现成功"),
  SMS_WITHDRAW(MessageTypeEnum.SMS,"SMS_WITHDRAW",18,"提币短信"),
  SMS_QUICK_REGISTER(MessageTypeEnum.SMS,"SMS_QUICK_REGISTER",19,"快速注册短信"),
  SMS_QUICK_REGISTER_SUCCESS(MessageTypeEnum.SMS,"SMS_QUICK_REGISTER_SUCCESS",20,"快速注册成功短信"),
  SMS_RESET_PASSWORD(MessageTypeEnum.SMS,"SMS_RESET_PASSWORD", 21, "重置密码短信"),
  SMS_RESET_PASSWORD_SUCCESS(MessageTypeEnum.SMS,"SMS_RESET_PASSWORD_SUCCESS", 22,"手机重置短信成功"),
  EMAIL_LOGIN_SUCCESS(MessageTypeEnum.EMAIL,"EMAIL_LOGIN_SUCCESS", 23, "邮箱登录成功"),
  SMS_LOGIN_SUCCESS(MessageTypeEnum.SMS, "SMS_LOGIN_SUCCESS", 24, "手机登录成功"),
  SMS_ACTIVITY_REGISSTER_CHARGE_COIN(MessageTypeEnum.SMS, "SMS_ACTIVITY_REGISSTER_CHARGE_COIN", 25, "注册活动送币"),
  SMS_ACTIVITY_TRANSFER_CHARGE_COIN(MessageTypeEnum.SMS, "SMS_ACTIVITY_TRANSFER_CHARGE_COIN", 26, "注册交易送币"),
  ;
	private MessageTypeEnum messageTypeEnum;
	private String desc;// 唯一标识
	private int code;
	private String name;


	private MessageEnum(MessageTypeEnum messageTypeEnum, String name, int code, String desc) {
		this.messageTypeEnum = messageTypeEnum;
		this.desc = desc;
		this.code = code;
		this.name = name;
	}

	public MessageTypeEnum getMessageTypeEnum() {
		return messageTypeEnum;
	}

	public void setMessageTypeEnum(MessageTypeEnum messageTypeEnum) {
		this.messageTypeEnum = messageTypeEnum;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private static final Map<String, MessageEnum> messageEnumMap = new HashMap<>();
	static {
		for (MessageEnum messageEnum : MessageEnum.values()) {
			messageEnumMap.put(messageEnum.getDesc(),messageEnum);
		}
	}

	public static MessageEnum getMessageEnum(String desc){
		return messageEnumMap.get(desc);
	}
}
