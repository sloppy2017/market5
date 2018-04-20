package com.c2b.coin.trade.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 类说明：下委托单成功后发MQ的VO
 *
 * @author Anne
 * @date 2017年10月23日
 */
public class ExchangeVO {


  /**
   * 交易唯一序列号 ，全局唯一
   *
   */
  private String seq;
  /**
   * 用户唯一ID
   */
  private String userId;
  /**
   * 交易类型 ：买入buy,or卖出sell,or撤单callback
   *
   *
   */
  private String type;
  /**
   * 交易对主键
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
  /**
   * 交易类型：限价交易PriceDeal：市价交易MarketTransactions
   */
  private String genre;
  /**
   * 成交金额
   */
  private BigDecimal makeMoney = BigDecimal.ZERO;
  /**
   * 剩余数量
   */
  private BigDecimal residueCount = BigDecimal.ZERO;
  /**
   * 成交数量
   */
  private BigDecimal makeCount = BigDecimal.ZERO;
  
  /**
   * 成交均价
   */
  private BigDecimal averageMoney = BigDecimal.ZERO;
  
  /**
   * 交易历史记录列表
   */
  private List<MatchInfoVO> list;

  public BigDecimal getMakeMoney() {
    return makeMoney;
  }

  public void setMakeMoney(BigDecimal makeMoney) {
    this.makeMoney = makeMoney;
  }

  public BigDecimal getResidueCount() {
    return residueCount;
  }

  public void setResidueCount(BigDecimal residueCount) {
    this.residueCount = residueCount;
  }

  public BigDecimal getMakeCount() {
    return makeCount;
  }

  public void setMakeCount(BigDecimal makeCount) {
    this.makeCount = makeCount;
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

  public String getUserId() {
      return userId;
  }

  public void setUserId(String userId) {
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

  public List<MatchInfoVO> getList() {
    return list;
  }

  public void setList(List<MatchInfoVO> list) {
    this.list = list;
  }

  public BigDecimal getAverageMoney() {
    return averageMoney;
  }

  public void setAverageMoney(BigDecimal averageMoney) {
    this.averageMoney = averageMoney;
  }

}
