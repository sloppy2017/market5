package com.c2b.coin.matching.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.c2b.coin.matching.constant.EnumTradeType;

/**
 * 
 * @author DingYi
 * @version 1.0.0
 * @since 2018年4月19日 下午9:50:19
 */
public class Order implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1658521996348367066L;

	private BigDecimal price;
	private BigDecimal amount;
	private long timestamp;
	private String orderNo;
	private EnumTradeType enumTradeType;
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public EnumTradeType getEnumTradeType() {
		return enumTradeType;
	}
	public void setEnumTradeType(EnumTradeType enumTradeType) {
		this.enumTradeType = enumTradeType;
	}
	
	
}
