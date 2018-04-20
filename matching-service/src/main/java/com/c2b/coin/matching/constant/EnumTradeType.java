package com.c2b.coin.matching.constant;

/**
 * 交易类型枚举类
 * @author DingYi
 * @version 1.0.0
 * @since 2018年4月18日 下午6:22:46
 */
public enum EnumTradeType {

	buy("buy","买入"),sell("sell","卖出"),callback("callback","撤单");
	private String code;
	private String name;
	private EnumTradeType(String code, String name){
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
