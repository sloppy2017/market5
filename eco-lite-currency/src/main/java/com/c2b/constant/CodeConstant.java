package com.c2b.constant;


import java.util.HashMap;
import java.util.Map;

public interface CodeConstant {

	/**
	 * 通信成功通用码
	 */
	public final short SC_OK = 0;
	
	/**
	 * 通信失败通用码
	 */
	public final short SC_UNKNOWN = -1;
	
	/**
	 * 未登录未验证通过
	 */
	public final short UNLOGIN = 100;
	
	/**
	 * 查询失败
	 */
	public final short UNCHECK = -3;
	
	/**
	 * 提交失败
	 */
	public final short SUBMITERR = -4;
	
	/**
	 * 登录失败
	 */
	public final short LOGIN_FAILED = -5;
	
	/**
	 * 账号冻结
	 */
	public final short ERROR_FREEZE = -6;
	
	/**
	 * 用户在黑名单
	 */
	public final short ERROR_BLACK = -7;
	
	
	//超出登陆次数
	public final short ERROR_LIMIT = -8;
	
	/**
	 * 重复提交
	 */
	public final short ERROR_RESUBMITE = -9;
	
	/**
	 * 非法操作
	 */
	public final short WRONG_INPUT = -10;

	
	/**
	 * 短信验证码超时
	 */
	public final short SMS_OVERTIME = -12;
	
	/**
	 * 短信验证码匹配错误
	 */
	public final short SMS_ERROR = -13;
	
	/**
     * 短信验证码获取失败
     */
    public final short SMS_GET_ERROR = -14;
    
    /**
     * 传递参数有误
     */
    public final short PARAM_ERROR = -15;
    /**
     * 3次校验机会已用完，请明天再尝试！
     */
    public final short SMS_NUM_MAX = -16;
    
	/**
	 * 短信验证码为空
	 */
	public final short SMS_NULL = -17;
	
	
	/**
	 * 用户不存在
	 */
	public final short USER_NO_EXISTS=-18;
	
	
	/**
	 * 手机号为空
	 */
	public final short MOBILE_NULL = -19;
	
	/**
	 * 手机号已注册
	 */
	public final short MOBILE_EXISTS = -20;
	/**
	 * 用户名或密码错误
	 */
	public final short USERNAMEORPWD_ERROR = -21;
	
	/**
	 * 银行卡号已被使用
	 */
	public final short BANK_EXISTS = -22;
	/**
	 * 用户昵称已经存在
	 */
	public final short NICKNAME_EXISTS = -23;
	/**
	 * 用户手机号不存在
	 */
	public final short USERMOBILE_NOEXISTS=-24;
	/**
	 * 获取验证码手机号不是用户手机号
	 */
	public final short NOT_USERMOBILE = -25;
	/**
	 * JSON文件没有更新
	 */
	public final short NOT_UPDATEFILE = -26;
	
	/**
	 * 支付密码未设置
	 */
	public final short PAY_CODE_EMPTY = -27;
	
	/**
	 * 支付密码已经设置
	 */
	public final short PAY_CODE_EXIST = -28;
	
	/**
	 * 支付密码错误
	 */
	public final short PAY_CODE_ERROR = -29;
	
	/**
	 * 支付密码为空或者两次输入不一致
	 */
	public final short PAY_CODE_NULL = -30;

	
	/**
	 * 支付密码错误次数已达上限
	 */
	public final short  PAY_CODE_MAX =-31;		
	/**
	 * 手机号格式错误
	 */
	public final short  MOBILE_ERROR =-32;	
	/**
	 * 签名验证失败
	 */
	public final short  SIGN_ERROR =-33;   
	/**
	 * 系统异常
	 */
	public final short  SYS_ERROR =-34;     
	
	public final String PERMISSION_DENIED = "您的权限不足";
	
	public final Map<String, String> INVALID_PARAMS = new HashMap<String, String>(){
	    {
	        put("invalid params", "请求参数有误");  
	        put("sender and receiver must be different", "转账账户和接收账户不能相同");  
	        put("receiver not found", "接收账户未找到");  
	    }
	};
	
	public final String NOT_ENOUGH_BALANCE = "余额不足";
	
	public final String USER_NOT_FOUND = "未找到转账用户";
	/**
     * 推介人不存在
     */
	public final short PARENT_USERCODE = -35;
	/**
     * 密码错误次数超过3次
     */
	public final short ERROR_PASSWORD_TIMES = -36;
	/**
     * 验证码错误
     */
	public final short ERROR_VCODE = -37;
	/**
     * 验证码失效
     */
    public final short OVERTIME_VCODE = -38;
    /**
     * 用户地址默认地址设置失败
     */
    public final short ERROR_DEFAULT_ADDRESS = -39;
    /**
     * 无转账权限
     */
    public final short ERROR_NO_TRANSFER = -40;
    /**
     * 无提现权限
     */
    public final short ERROR_NO_WITHDRAWAL = -41;
    /**
     * 转账时对方账户不存在
     */
    public final short ERROR_NO_ACCOUNT = -42;
    /**
     * 超出上限
     */
    public final short ERROR_OVER_UPLIMIT = -43;
    /**
     * 低于下限
     */
    public final short ERROR_LOWER_LOWLIMIT = -44;
    /**
     * 支付失败
     */
    public final short ERROR_PAY = -45;
	/**
	 * 推介人没有权限
	 */
    public final short PARENT_NO_PRI = -46;
	/**
	 * 注册失败
	 */
	public final short REGISTER_FAIL = -47;
	/**
     * 密码错误
     */
	public final short ERROR_PASSWORD = -48;
	/**
     * 修改失败
     */
	public final short UPDATE_FAIL = -49;
	/**
     * 余额不足
     */
	public final short BALANCE_NOT_ENOUGH = -50;
	/**
     * 签名认证失败
     */
	public final short ERROR_SIGN = -51;
	/**
     * 删除失败
     */
    public final short DELETE_FAIL = -52;
    /**
     * 订单不存在
     */
    public final short ORDER_NO_EXISTS = -53;
    /**
     * 用户被禁用
     */
    public final short USER_DISABLE = -54;
    /**
     * 信息不存在
     */
    public final short NO_EXISTS = -55;
    /**
     * 已支付
     */
    public final short pay_finish = -56;
    /**
     * 充值金额计算有误
     */
    public final short ERROR_MONEY = -57;
    /**
     * 充值费率有误
     */
    public final short ERROR_FATE = -58;
    /**
     * 短信黑名单
     */
    public final short ERROR_SMS_BLACK = -59;
    /**
     * 功能禁用
     */
    public final short ERROR_DISABLE = -60;
    /**
     * 没有充值权限
     */
    public final short ERROR_NO_RECHARGE = -61;
    /**
     * 上笔订单处理中，请稍候再试！
     */
    public final short TRANSFERING_EXISTS = -62;
    /**
     * 不能给自己账户转钱
     */
    public final short ERROR_TRANSFER_SELF = -63;
    /**
     * 其他未知错误
     */
    public final short ERROR_OTHER_MSG = -64;
    /**
     * 系统参数配置有误
     */
    public final short SYS_PARAM_ERROR = -65;
    /**
     * 短信发送失败
     */
    public final short SMS_SEND_FALI = -66;
	/**
	 * Redis缓存无key
	 */
	public final short SYS_REDISKEY_ERROR = -67;
	/**
	 * 秒杀商品无库存
	 */
	public final short SYS_HOTGOODSKILL_ERROR = -68;
	/**
	 * 秒杀商品同一天同用户重复下单
	 */
	public final short SYS_HOTGOODSREPEATKILL_ERROR = -69;
	/**
	 * 已消费
	 */
	public final short CONSUMER_FINISH = -70;
	/**
	 * 未付款
	 */
	public final short NO_PAY = -71;
	/**
	 * 没有下单权限
	 */
	public final short ERROE_NO_ORDER = -72;
	/**
	 * 密码不能为空
	 */
	public final short ERROE_PASSWORD_NULL = -73;
	/**
     * 密码应为6-16位数
     */
	public final short ERROE_PASSWORD_6_16 = -74;
	/**
	 * 密码应包含字母和数字
	 */
	public final short ERROE_PASSWORD_LETTER_NUM = -75;
	
	/**
	 * 确认密码不能为空
	 */
	public final short ERROE_CONFIRM_PASSWORD_NULL = -76;
	/**
	 * 两次密码不一致
	 */
	public final short ERROE_PASSWORD_DIFFERENT = -77;
	/**
	 * 邀请码不能为空
	 */
	public final short ERROE_INVITATION_NULL = -78;
	/**
	 * 邀请码为6位数
	 */
	public final short ERROE_INVITATION_6NUM = -79;
	/**
	 * 新密码不能与旧密码一致
	 */
	public final short ERROE_PASSWORD_NEW_OLD = -80;
	/**
	 * 转账到三界链钱包返回错误
	 */
	public final short ERROE_SJL_TRANSFER = -81;
	
	/**
	 * 您尚未关联店铺
	 */
	public final short ERROE_DP_NO_EXISTS = -82;
	/**
	 * 短信发送太频繁
	 */
	public final short ERROE_SMS_FREQUENTLY = -83;
	/**
	 * 短信发送量超出限制
	 */
	public final short ERROE_SMS_OVER = -84;
	/**
	 * 不可重复抢红包
	 */
	public final short ERROE_REPEATROBREDPACKAGE = -85;
	/**
	 * 红包已抢光
	 */
	public final short ERROE_NOREDPACKAGE = -86;
	
	/**
     * 已取消
     */
    public final short ERROR_CALCEL = -89;
	/**
	 * 抢红包活动未开启
	 */
	public final short ERROR_UNOPENREDPACKAGE = -90;
	/**
	 * 店铺已关闭
	 */
	public final short ERROR_STORE_CLOSE = -91;
	/**
     * 抢红包活动已结束
     */
    public final short ERROR_RED_PACKETS_END = -92;
    /**
     * 红包已抢完
     */
    public final short ERROR_RED_PACKETS_NOHAS = -93;
    /**
	 * 没有权限
	 */
	public final short ERROR_NO_PERMISSION = -94;
	/**
	 * 没有用户类型信息
	 */
	public final short ERROR_NO_USERTYPE = -95;
	/**
	 * 用户只能秒杀一单
	 */
	public final short ERROR_NO_REPEATSECKILL = -96;
	/**
	 * 用户只能秒杀一单每单一件商品
	 */
	public final short ERROR_NO_REPEATSECKILLGOODS = -97;
	/**
	 * 店铺秒杀商铺无价格信息
	 */
	public final short ERROR_STOREGOODSNOPRICE = -98;
	/**
     * 手机号已绑定，不能重复绑定
     */
	public final short ERROR_ALREADY_BOUND = -99;
	/**
     * 处理中，请稍后
     */
	public final short ERROR_PROCESSING = -100;
	/**
	 * 禁止转入
	 */
	public final short DISABLE_TURN_IN = -101;
	/**
	 * 消费码错误
	 */
	public final short ERROR_CHECK_CODE = -102;
	/**
	 * 秒杀活动未开启
	 */
	public final short ERROR_SECKILL_UNOPEN = -103;

	/**
	 * 秒杀活动结束
	 */
	public final short ERROR_ACTIVITE_END = -104;
	/**
     * 没有符合条件的手机号
     */
    public final short ERROR_PHONE_NULL = -105;

	/**
	 * 没有秒杀活动
	 */
	public final short ERROR_NO_ACTIVITY = -106;

	/**
	 * 下单商品sku信息与数据库不符
	 */
	public final short ERROR_SKU_NOT_SAME = -107;
	/**
	 * 您没有此权限
	 */
	public final short NO_PERMISSION = -108;
	
	/**
     * 用户账号已存在
     */
    public final short ACCOUNT_EXISTS = -109;
    /**
     * 身份验证失败
     */
    public final short AUTHENTICATION_FAIL = -110;
    /**
     * 邮箱不能为空
     */
    public final short MAIL_NULL = -111;
	/**
	 * 转账接收人信息不全
	 */
	public final short NEED_ACCEPT_INTO = -112;
	/**
	 * 转账用户还有资产未转账完成
	 */
	public final short NOT_COMPLETE_PROCESS = -113;
	/**
	 * 转账用户资产不足
	 */
	public final short NOT_ENOUGH_MONEY = -114;
	/**
	 * 不可给自己转账
	 */
	public final short NOT_TRANSFER_SELF = -115;

	/**
	 * 数据库无此商品
	 */
	public final short ERROR_INFO_NOT_SAME = -116;

	/**
	 * 区块链接口发生错误
	 */
	public final short ERROR_BLOCKCHAIN =117;

	/**
	 * 人民币不正确
	 */
	public final short ERROR_RMB =-118;
	/**
     * 银行卡号已绑定
     */
	public final short ERROR_BANK_BINDED =-119;
	
	/**
     * 未登录未验证通过
     */
    public final short APPUNLOGIN = -2;
}
