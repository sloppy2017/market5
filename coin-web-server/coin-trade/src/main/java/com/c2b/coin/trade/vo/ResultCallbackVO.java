package com.c2b.coin.trade.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 类说明 撤单VO
 * 
 * @author Anne
 * @date 2017年10月24日
 */
public class ResultCallbackVO {

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
   * 成交的数量
   * 
   */
  private BigDecimal sumCount = BigDecimal.ZERO;

  /**
   * 剩余的数量
   */
  private BigDecimal residueCount = BigDecimal.ZERO;

  /**
   * 交易价格
   */
  private BigDecimal money = BigDecimal.ZERO;
  
  /**
   * 成交的全部金额
   */
  private BigDecimal sumMoney = BigDecimal.ZERO;
  
  /**
   * 成交均价
   */
  private BigDecimal averageMoney = BigDecimal.ZERO;
  
  /**
   * 交易类型：限价交易PriceDeal：市价交易MarketTransactions
   */
  private String genre;

  /**
   * 状态，0为正在撮合，1为撮合成功，2为撮合失败，3为撤单
   */
  private Integer status;
  
  /**
   * true为撤单成功，false为撤单失败 撤单失败原因为
   */
  private Boolean callBack;
  
  /**
   * 101为已经撮合成功, 102为超时失败,103未知,失败  104重复撤单,106撮合引擎未收到交易记录，撤单失败
   */
  private Integer code;
  
  /**
   * 错误信息
   */
  private String message;
  
  /**
   * 交易历史记录列表
   */
  private List<MatchInfoVO> list;

  public String getSeq() {
    return seq;
  }

  public void setSeq(String seq) {
    this.seq = seq;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
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

  public BigDecimal getMoney() {
    return money;
  }

  public void setMoney(BigDecimal money) {
    this.money = money;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Boolean getCallBack() {
    return callBack;
  }

  public void setCallBack(Boolean callBack) {
    this.callBack = callBack;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public List<MatchInfoVO> getList() {
    return list;
  }

  public void setList(List<MatchInfoVO> list) {
    this.list = list;
  }

  public BigDecimal getResidueCount() {
    return residueCount;
  }

  public void setResidueCount(BigDecimal residueCount) {
    this.residueCount = residueCount;
  }

  public BigDecimal getAverageMoney() {
    return averageMoney;
  }

  public void setAverageMoney(BigDecimal averageMoney) {
    this.averageMoney = averageMoney;
  }

  public BigDecimal getSumCount() {
    return sumCount;
  }

  public void setSumCount(BigDecimal sumCount) {
    this.sumCount = sumCount;
  }

  public BigDecimal getSumMoney() {
    return sumMoney;
  }

  public void setSumMoney(BigDecimal sumMoney) {
    this.sumMoney = sumMoney;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
  
}
