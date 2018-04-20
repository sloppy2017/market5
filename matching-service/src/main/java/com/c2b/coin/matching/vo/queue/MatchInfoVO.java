package com.c2b.coin.matching.vo.queue;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 类说明 交易成功后接收MQ的VO
 * @author Anne
 *
 */
public class MatchInfoVO {
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 买入的序列号
	 */
	private String buySeq;
	/**
	 * 卖出的序列号
	 */
	private String sellSeq;
	/**
	 * 成交数量
	 */
	private BigDecimal count;
	/**
	 * 成交金额
	 */
	private BigDecimal money;
	/**
	 * 成交的单价
	 */
	private BigDecimal changer;
	/**
	 * 买入的手续费
	 */
	private BigDecimal buyCharge;
	/**
	 * 卖出的手续费
	 */
	private BigDecimal sellCharge;
	/**
	 * 成交的流水号 全局唯一
	 */
	private String seq;
	/**
	 * 成交时间
	 */
	private Date pairDate;
	/**
	 * 买单挂出时间
	 */
	private Date busDate;
	/**
	 * 卖单挂出时间
	 */
	private Date sellDate;

	/**
	 * 买入的用户
	 */
	private String buyUser;
	/**
	 * 卖出的用户
	 */
	private String sellUser;

	/**
	 * 买入类型
	 */
	private String buyGenre;
	/**
	 * 卖出类型
	 */
	private String sellGenre;

	/**
	 * 用户id
	 */
	private Integer matchDealId;

	/**
	 * 交易对主键
	 */
	private String currency;
	/**
	 * 买入手续费
	 */
	private BigDecimal buyMoney;
	/**
	 * 卖出手续费
	 */
	private BigDecimal sellMoney;

	public String getBuySeq() {
		return buySeq;
	}

	public void setBuySeq(String buySeq) {
		this.buySeq = buySeq;
	}

	public String getSellSeq() {
		return sellSeq;
	}

	public void setSellSeq(String sellSeq) {
		this.sellSeq = sellSeq;
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

	public BigDecimal getChanger() {
		return changer;
	}

	public void setChanger(BigDecimal changer) {
		this.changer = changer;
	}

	public BigDecimal getBuyCharge() {
		return buyCharge;
	}

	public void setBuyCharge(BigDecimal buyCharge) {
		this.buyCharge = buyCharge;
	}

	public BigDecimal getSellCharge() {
		return sellCharge;
	}

	public void setSellCharge(BigDecimal sellCharge) {
		this.sellCharge = sellCharge;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public Date getPairDate() {
		return pairDate;
	}

	public void setPairDate(Date pairDate) {
		this.pairDate = pairDate;
	}

	public Date getBusDate() {
		return busDate;
	}

	public void setBusDate(Date busDate) {
		this.busDate = busDate;
	}

	public Date getSellDate() {
		return sellDate;
	}

	public void setSellDate(Date sellDate) {
		this.sellDate = sellDate;
	}

	public String getBuyUser() {
		return buyUser;
	}

	public void setBuyUser(String buyUser) {
		this.buyUser = buyUser;
	}

	public String getSellUser() {
		return sellUser;
	}

	public void setSellUser(String sellUser) {
		this.sellUser = sellUser;
	}

	public String getBuyGenre() {
		return buyGenre;
	}

	public void setBuyGenre(String buyGenre) {
		this.buyGenre = buyGenre;
	}

	public String getSellGenre() {
		return sellGenre;
	}

	public void setSellGenre(String sellGenre) {
		this.sellGenre = sellGenre;
	}

	public Integer getMatchDealId() {
		return matchDealId;
	}

	public void setMatchDealId(Integer matchDealId) {
		this.matchDealId = matchDealId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getBuyMoney() {
		return buyMoney;
	}

	public void setBuyMoney(BigDecimal buyMoney) {
		this.buyMoney = buyMoney;
	}

	public BigDecimal getSellMoney() {
		return sellMoney;
	}

	public void setSellMoney(BigDecimal sellMoney) {
		this.sellMoney = sellMoney;
	}

  @Override
  public String toString() {
    return "MatchInfoVO{" +
      "id=" + id +
      ", buySeq='" + buySeq + '\'' +
      ", sellSeq='" + sellSeq + '\'' +
      ", count=" + count +
      ", money=" + money +
      ", changer=" + changer +
      ", buyCharge=" + buyCharge +
      ", sellCharge=" + sellCharge +
      ", seq='" + seq + '\'' +
      ", pairDate=" + pairDate +
      ", busDate=" + busDate +
      ", sellDate=" + sellDate +
      ", buyUser='" + buyUser + '\'' +
      ", sellUser='" + sellUser + '\'' +
      ", buyGenre='" + buyGenre + '\'' +
      ", sellGenre='" + sellGenre + '\'' +
      ", matchDealId=" + matchDealId +
      ", currency='" + currency + '\'' +
      ", buyMoney=" + buyMoney +
      ", sellMoney=" + sellMoney +
      '}';
  }
}
