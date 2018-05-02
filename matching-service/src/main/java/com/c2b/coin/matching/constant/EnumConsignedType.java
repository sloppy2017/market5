package com.c2b.coin.matching.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 交易方式枚举类
 * @author DingYi
 * @version 1.0.0
 * @since 2018年4月18日 下午6:23:11
 */
public enum EnumConsignedType {
	LIMIT("PriceDeal","限价交易"),MARKET("MarketTransactions","市价交易");
	private String code;
	private String name;
	private EnumConsignedType(String code, String name){
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
	
	
	public static EnumConsignedType getEnumConsignedType(String code) {
		for(EnumConsignedType enumConsignedType:EnumConsignedType.values()) {
			if(StringUtils.equals(code, enumConsignedType.getCode())) {
				return enumConsignedType;
			}
		}
		return null;
		
	}
	
}
