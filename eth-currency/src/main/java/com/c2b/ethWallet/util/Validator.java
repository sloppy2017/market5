package com.c2b.ethWallet.util;

import java.util.regex.Pattern;

/**
 * 校验器：利用正则表达式校验邮箱、手机号等
 * 
 * @author zhangchunming
 * 
 */
public class Validator {
    /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";
 
    /**
     * 正则表达式：验证密码
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";
    
    /**
     * 正则表达式：验证密码
     */
    public static final String REGEX_PASSWORD_LETTER_NUMBER = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
    /**
     * 正则表达式：验证账号
     */
    public static final String REGEX_ACCOUNT_LETTER_NUMBER = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{1,}$";
 
    /**
     * 正则表达式：验证手机号
     */
//    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    public static final String REGEX_MOBILE = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(17[0-9]{1})|(14[0-9]{1}))+\\d{8})$";
    
 
    public static final String REGEX_PHONE_STR = "^(1\\d{10}(,)?)+$";
    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
 
    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";
 
    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";
 
    /**
     * 正则表达式：验证URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
 
    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
    /**
     * 正则表达式：验证正整数1-6位
     */
    public static final String REGEX_NUMBER6 = "^[1-9]\\d{5}$";
    /**
     * 正则表达式：验证正整数6位
     */
    public static final String REGEX_6NUMBER = "^[0-9]{6}$";
    /**
     * 正则表达式：验证正整数最高7位
     */
    public static final String REGEX_NUMBER_MAX7 = "^[1-9]\\d{0,6}$";
    /**
     * 正则表达式：验证整数
     */
    public static final String REGEX_INTEGER = "^[0-9]+$";
    /**
     * 正则表达式：验证金额,最多保留4位小数
     */
    public static final String REGEX_MONEY4 = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,4})?$";
    /**
     * 正则表达式：验证金额,最多保留3位小数
     */
    public static final String REGEX_MONEY3 = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,3})?$";
    /**
     * 正则表达式：验证金额,最多保留2位小数
     */
    public static final String REGEX_MONEY2 = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";
    /**
     * 正则表达式：验证金额
     */
    public static final String REGEX_MONEY = "^([1-9]{1}\\d*)$";
 
    /**
     * 校验用户名
     * 
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }
 
    /**
     * 校验密码
     * 
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }
    /**
     * 校验密码(由字母和数字组成)
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPasswordByLetterAndNum(String password) {
        return Pattern.matches(REGEX_PASSWORD_LETTER_NUMBER, password);
    }
    /**
     * @describe:校验账号是否由字母和数字组成
     * @author: zhangchunming
     * @date: 2017年3月6日下午5:17:45
     * @param account
     * @return: boolean
     */
    public static boolean isAccountByLetterAndNum(String account) {
        return Pattern.matches(REGEX_ACCOUNT_LETTER_NUMBER, account);
    }
 
    /**
     * 校验手机号
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }
    /**
     * @describe:校验手机号，多个手机号‘,’隔开，需与isMobile配合使用
     * @author: zhangchunming
     * @date: 2016年11月23日下午8:06:29
     * @param phoneStr
     * @return: boolean
     */
    public static boolean isPhoneStr(String phoneStr) {
        return Pattern.matches(REGEX_PHONE_STR, phoneStr);
    }
 
    /**
     * 校验邮箱
     * 
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }
 
    /**
     * 校验汉字
     * 
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }
 
    /**
     * 校验身份证
     * 
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }
 
    /**
     * 校验URL
     * 
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }
 
    /**
     * 校验IP地址
     * 
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }
    /**
     * @describe:校验1-6位正整数
     * @param str
     * @return: boolean
     */
    public static boolean isNumber6(String str) {
        return Pattern.matches(REGEX_NUMBER6, str);
    }
    /**
     * @describe:校验6位正整数
     * @param str
     * @return: boolean
     */
    public static boolean is6Number(String str) {
        return Pattern.matches(REGEX_6NUMBER, str);
    }
    /**
     * @describe:最大7位正整数
     * @author: zhangchunming
     * @date: 2016年9月20日下午9:40:37
     * @param str
     * @return: boolean
     */
    public static boolean isNumberMax7(String str) {
        return Pattern.matches(REGEX_NUMBER_MAX7, str);
    }
    /**
     * @describe:校验整数
     * @param str
     * @return: boolean
     */
    public static boolean isInteger(String str) {
        return Pattern.matches(REGEX_INTEGER, str);
    }
    /**
     * @describe:校验金额，最多可以保留四位小数
     * @author: zhangchunming
     * @date: 2016年10月8日下午4:39:24
     * @param str
     * @return: boolean
     */
    public static boolean isMoney4(String str) {
        return Pattern.matches(REGEX_MONEY4, str);
    }

    /**
     * @describe:校验金额，最多可以保留三位小数
     * @author: zhangchunming
     * @date: 2016年10月8日下午4:39:24
     * @param str
     * @return: boolean
     */
    public static boolean isMoney3(String str) {
        return Pattern.matches(REGEX_MONEY3, str);
    }
    /**
     * @describe:校验金额，最多可以保留2位小数
     * @author: zhangchunming
     * @date: 2017年1月6日下午18:39:24
     * @param str
     * @return: boolean
     */
    public static boolean isMoney2(String str) {
        return Pattern.matches(REGEX_MONEY2, str);
    }
    /**
     * @describe:校验金额
     * @author: zhangchunming
     * @date: 2016年11月1日下午4:13:42
     * @param str
     * @return: boolean
     */
    public static boolean isMoney(String str) {
        return Pattern.matches(REGEX_MONEY, str);
    }
    /**|
     * @describe:密码6-16位数,由数字，字母，特殊符号组成
     * @author: zhangchunming
     * @date: 2016年10月24日下午4:02:33
     * @param password
     * @return: boolean
     */
    public static boolean isPassword2(String str) {
        boolean is=true;
        //去除前后空格
        str=str.replaceAll("(^\\s*)|(\\s*$)","");
        //位数不对，设置为false
        if(str.length()<6 || str.length()>16){
            is=false;   
        };
        //全是数字
        String sz="^[0-9]{1,}$";
        //全是字母
        String zm="^[a-zA-Z]{1,}$";
        //全是特殊字符;
        String ts="^[`~!@#\\$%\\^\\&\\*\\(\\)_\\+<>\\?:\"\\{\\},\\.\\\\/\\;'\\[\\]]{1,}$"; 
        if(Pattern.matches(sz,str)){
            is=false;       
        };
        if(Pattern.matches(zm,str)){
            is=false;       
        };
        if(Pattern.matches(ts,str)){
            is=false;       
        };
        return is;  
    }
    public static void main(String[] args) {
       /* String username = "fdsdfsdj";
        System.out.println(Validator.isUsername(username));
        System.out.println(Validator.isChinese(username));
        String str = "10000000";
        System.out.println(Validator.isNumberMax7(str));*/
//        String phone = "13300100100";
//        System.out.println(Validator.isMobile(phone));//111107
//        System.out.println(Validator.is6Number("111102"));
        System.out.println(Validator.isAccountByLetterAndNum("1c"));
       /* String money = "120.0123";
        System.out.println(Validator.isMoney4(money));
        String password = " alkg  ";
        password=password.replaceAll("(^\\s*)|(\\s*$)","");*/
        
        
    }
}
