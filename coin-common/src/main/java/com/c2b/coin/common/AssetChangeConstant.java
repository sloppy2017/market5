package com.c2b.coin.common;

/**
 * 资产变动常量
 * @author jianhongyan
 *
 */
public class AssetChangeConstant {
	//充值
	public final static int DEPOSIT=1;
	//提现
	public final static int WITHDRAW=2;
	//增加
	public final static int INCREASE=3;
	//减少
	public final static int DECREASE=4;
	//冻结
	public final static int FREEZE=5;
	//解冻
	public final static int UNFREEZE=6;
	
	
	
	
	public final static int RESULT_PENDING=1;
	public final static int  RESULT_CONFIRM=2;
	public final static int  RESULT_FAILURE=3;
	
	
	public final static String  RESULT_RESULT_PENDING="PENDING";
	public final static String  RESULT_RESULT_CONFIRM="CONFIRM";
	public final static String  RESULT_RESULT_FAILURE="FAILURE";
	
	
	public final static int AUDIT_RESULT_WAITING=1;
	public final static int AUDIT_RESULT_SUCCESS=2;
	public final static int AUDIT_RESULT_FAILURE=3;
	
	
	public final static int STATUS_WAITING=1;
	public final static int STATUS_CONFIRMING=2;
	public final static int STATUS_SUCCESS=3;
	
	
	public final static int ACTIVITY_REGISTER=3;
	public final static int ACTIVITY_GIFT=4;
	public final static int ACTIVITY_DISCOUNT=5;
}
