package com.c2b.coin.common;

public class Constants {
  public static final String PROJECT_NAME = "PARK.ONE";

  public static final String ENCODING_UTF8 = "UTF-8";

  public static final Long DEFAULT_ACTIVITY_CODE = 0L;
  public static final String DEFAULT_INVITE_CODE = "0";

  public static final String REGEX_EMAIL = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
  public static final String REGEX_PASSWORD = "^.*(?=.{8,20})(?=.*\\d)(?=.*[A-Z]).*$";
  public static final String REGEX_PAY_PWD = "^\\d{6}$";
  public static final String REGEX_MOBILE = "^[0-9]{6,15}$";
  public static final String REGEX_REGION = "^[0-9]{1,4}";
  public static final String REGXP_FOR_HTML = "<([^>\"\']*)>";

  public static final String HTTP_HEADER_RESET_TOKEN = "x-reset-pwd_token";
  public static final String HTTP_HEADER_ACTIVE_TOKEN = "x-active-user-token";
  public static final String HTTP_HEADER_USER_ID = "x-access-userid";
  public static final String HTTP_HEADER_USER_NAME = "x-access-username";
  public static final String HTTP_HEADER_ACTIVITY_ID = "x-activity-id";
  public static final String DEFAULT_ACTIVITY_ID = "0";
  public static final String HTTP_HEADER_DEVICE_ID = "x-device-id";
  public static final String DEFAULT_DEVICE_ID = "0";
  public static final String HTTP_HEADER_CHANNEL_NAME = "x-access-channel";
  public static final String DEFAULT_CHANNEL_NAME = "0";
  public static final String HTTP_HEADER_APP_VERSION = "x-app-version";
  public static final String DEFAULT_APP_VERSION = "1.0.0";
  public static final String HTTP_HEADER_TOKEN = "token";
  public static final String DEFAULT_HTTP_HEADER_TOKEN = "0";


  /*====================redis key======================*/
  public static final String REDIS_USER_TKOEN_KEY = "coin:user:token:";
  public static final int REDIS_USER_TOKEN_KEY_EXPIRE = 7 * 24 * 60 * 60;
  public static final String REDIS_SECOND_VALID_SMS_CODE_KEY = "coin:second_valid:sms_code:";
  public static final int REDIS_SECOND_VALID_SMS_CODE_KEY_EXPIRE = 7 * 24 * 60 * 60;
  public static final String REDIS_BIND_MOBILE_KEY = "coin:bind:mobile:";
  public static final int REDIS_BIND_MOBILE_KEY_EXPIRE = 10 * 60;
  public static final String REDIS_UNBIND_MOBILE_KEY = "coin:unbind:mobile:";
  public static final int REDIS_UNBIND_MOBILE_KEY_EXPIRE = 10 * 60;
  public static final String REDIS_ACTIVE_EMAIL_KEY = "coin:bind:email:";
  public static final int REDIS_EMAIL_KEY_EXPIRE = 24  * 10 * 60;
  public static final String REDIS_RESET_PASSWORD_KEY = "coin:reset:password:";
  public static final String REDIS_USER_LOGIN_VERFIY_TYPE = "coin:user:login:verfiy:";
  public static final String REDIS_USER_LOGIN_SECOND_VERFIY_KEY = "coin:user:login:second:verfiy:";
  public static final int REDIS_APP_USER_LOGIN_SECOND_VERFIY_KEY = 7 * 24 * 60 * 60;
  public static final int REDIS_PC_USER_LOGIN_SECOND_VERFIY_KEY = 7 * 60 * 60;
  public static final String REDIS_USER_CHANGE_PAY_PWD_KEY = "coin:user:change:paypwd:";
  public static final int REDIS_USER_CHANGE_PAY_PWD_KEY_EXPIRE = 24 * 60 * 60;
  public static final String REDIS_WITHDRAW_SMS_CODE_KEY = "coin:withdraw:sms_code:";
  public static final int REDIS_WITHDRAW_SMS_CODE_KEY_EXPIRE = 10 * 60;
  public static final String REDIS_REG_SMS_CODE_KEY = "coin:register:sms:";
  public static final int REDIS_REG_SMS_CODE_KEY_EXPIRE = 10 * 60;
  public static final String REDIS_SMS_RESET_PWD_KEY = "coin:sms:reset:pwd:";
  public static final int REDIS_SMS_RESET_PWD_KEY_EXPIRE = 10 * 60;

  public static final String REDIS_USER_LOGIN_ERROR_TIMES_KEY = "coin:user:login:error:";
  public static final int REDIS_USER_LOGIN_ERROR_TIMES_KEY_EXPIRE = 24 * 60 * 60;

  public static final String REDIS_USER_TOKEN_LIST_KEY = "coin:user:tokenlist:";
  public static final String REDIS_USER_ACCESS_KEY = "coin:user:accesskey:";
  public static final String REDIS_USER_ACCESS_KEY_REQUEST_COUNT = "coin:user:accesskey:request:count:";


  /*====================queue destination======================*/
  public static final String ASSET_CHANGE_QUEUE_DESTINATION = "c2b.account.asset";
  public static final String TRADE_SUCCESS_DEAL_QUEUE_DESTINATION = "Consumer.trade.VirtualTopic.com.coin.married.deal";
  public static final String CONSIGNATION_SUCCESS_QUEUE_DESTINATION = "com.coin.consignation";
  public static final String TRADE_END_QUEUE_DESTINATION = "Consumer.trade.VirtualTopic.com.coin.married.deal.sum";



  public static final Integer User_AUTH_NO = 0;          //未认证
  public static final Integer User_AUTH_CHECKING = 1;   //审核中
  public static final Integer User_AUTH_SUCCESS = 2;    //审核成功
  public static final Integer User_AUTH_FAIL = 3;       //审核失败

  public static final int USER_NEWS_DEPOSIT=3;//充币
  public static final int USER_NEWS_WITHDRAW=4;//提币




  public static final String NOTICE_NEWS_CHANGE_PAY_PWD = "NEWS_CHANGE_PAY_PWD";
  public static final String NOTICE_NEWS_AUTH_SUCCESS = "NEWS_AUTH_SUCCESS";
  public static final String NOTICE_NEWS_ACOUNT_CHARGE = "NEWS_ACOUNT_CHARGE";
  public static final String NOTICE_NEWS_ACCOUNT_WITHDRAW = "NEWS_ACOUNT_WITHDRAW";

  public static final String COLLECT_CONSIGNATION = "COLLECT_CONSIGNATION";//撮合幂等性
}
