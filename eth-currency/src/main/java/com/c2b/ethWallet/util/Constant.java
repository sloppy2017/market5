package com.c2b.ethWallet.util;


/**
 * 常量类
 * @Title: Constants.java 
 * @Package com.qkl.tfcc.api.common
 * @Description: 
 * @author kezhiyi   
 * @date 2016年8月17日 下午4:10:15 
 * @version V1.0
 */
public class Constant {
	
	public static final String SESSION_MANAGER = "user_name";
	public static final String OPERATIONS = "operations";
	public static final String MODULES = "modules";
	public static final int SUCCESS = 1;
	public static final int FAIL = 0;
	public static final String USING = "1";// 启用
	public static final int FORBIDDEN = 2;// 禁用
	public static final String LOGIN_USER = "loginUser";
	public static final String DEFAULT_PWD = "password";
	public static final int PAGE_SIZE = 20;
	public static final String CUR_SYS_CODE = "poc";
	public static final String DELETE = "9";	
	public static final String ROLE_ID_LIST = "roleIdList";
	public static final String VERSION_NO = "1.0.0";
	public static final String LOCKED ="1";
	public static final String UN_LOCKED ="0";
//	public static final Date  SYS_BUILDDATE =  DateUtil.getDateByString("2016-08-10");
	
	public static final int PIC_HEAD_HEIGHT = 122;//用户头像高
    public static final int PIC_HEAD_WIDTH = 122;//用户头像宽
    public static final String PIC_HEAD_PATH = "uploadfile/img/head/";//头像图片
    public static final String PIC_TEMP_PATH = "uploadtemp/img/head/";//头像图片临时文件路径
    public static final String PIC_QRCODE_PATH = "uploadfile/img/qrcode/";//二维码图片
    public static final String DP_PAY_QRCODE_PATH = "uploadfile/img/dp_pay_qrcode/";//店铺收款二维码地址
    public static final String IMG_PATH = "uploadfile/img/logo.png";//logo路径
    
    public static final String CURRENCY_BTC = "BTC";
    public static final String CURRENCY_LTC = "LTC";
    public static final String CURRENCY_ETH = "ETH";
    public static final String CURRENCY_ETC = "ETC";
    public static final String CURRENCY_SAN = "SAN";
    
    /**
     * 是否已归集：0未归集，1已归集
     */
    public static final String IS_CONCENTRATE = "1";
    public static final String IS_UNCONCENTRATE = "0";
    
    /**
     * 归集币种类型：比特币
     */
    public static final String 	CONCENTRATE_TYPE_BTC = "1";
    
    /**
     * 归集币种类型：以太坊
     */
    public static final String 	CONCENTRATE_TYPE_ETH = "2";
    
    /**
     * 归集币种类型：以太经典
     */
    public static final String 	CONCENTRATE_TYPE_ETC = "3";
    
    /**
     * 归集币种类型：莱特币
     */
    public static final String 	CONCENTRATE_TYPE_LTC = "4";
    
    /**
     * 归集币种类型：三界宝
     */
    public static final String 	CONCENTRATE_TYPE_SAN = "5";
    
    /**
     * 归集状态：0失败1成功2归集中
     */
    public static final String GATHER_STATUS_FAIL = "0";
    
    public static final String GATHER_STATUS_SUCCESS = "1";
    
    public static final String GATHER_STATUS_ING = "2";
    
    public static final String CREATE_WALLET_ERROR = "createWalletFail";
    
    public static final String HOT_WALLET_CODE_GROUP = "HOT_WALLET";
    
    public static final String COLD_WALLET_CODE_GROUP = "COLD_WALLET";
    
    /**
     * 冷热钱包状态：1有效；0无效
     */
    public static final String STATUS_VALID_VALUE = "1";
    
    public static final String STATUS_INVALID_VALUE = "0";
    
    /*****************token nama defined start********************/
    public static final String zgTokenName = "zg";
    
    public static final String omgTokenName = "OMG";
    
    public static final String sntTokenName = "SNT";
    
    public static final String gntTokenName = "GNT";
    
    public static final String powrTokenName = "PORW";
    
    public static final String ptkTokenName = "PTK";
    
    /*****************token nama defined end********************/
}
