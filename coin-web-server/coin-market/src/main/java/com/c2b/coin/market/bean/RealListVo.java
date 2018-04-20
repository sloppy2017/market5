package com.c2b.coin.market.bean;

import java.math.BigDecimal;
import java.util.Date;

public class RealListVo {
	/**
	 * 币种
	 */
	private String currency;
	/**
	 * 总量
	 */
	private BigDecimal count;
	/**
	 * 价格
	 */
	private BigDecimal money;

	/**
	 * 全部卖出量
	 */
	private BigDecimal sumCount;
	private Date time;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public BigDecimal getSumCount() {
		return sumCount;
	}

	public void setSumCount(BigDecimal sumCount) {
		this.sumCount = sumCount;
	}
}
