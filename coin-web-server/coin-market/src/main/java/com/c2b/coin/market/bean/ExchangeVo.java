package com.c2b.coin.market.bean;

import com.c2b.coin.common.DateUtil;

import java.math.BigDecimal;
import java.util.Date;

public class ExchangeVo {
	/**
	 * 交易唯一序列号 ，全局唯一
	 *
	 */
	private Integer id;
	private String seq;
	/**
	 * 用户唯一ID
	 */
	private Integer userId;
	/**
	 * 交易类型 ：买入buy,or卖出sell,or撤单callback
	 *
	 *
	 */
	private String type;
	/**
	 * 交易的币种; BTC:ETH:OMG
	 *
	 */
	private String currency;
	/**
	 * 交易数量
	 *
	 */
	private BigDecimal count = BigDecimal.ZERO;

	/**
	 * 交易价格
	 */
	private BigDecimal money = BigDecimal.ZERO;
	private Date date = DateUtil.getCurrentDate();

	/**
	 * 交易类型：限价交易PriceDeal：市价交易MarketTransactions
	 */
	private String genre;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
