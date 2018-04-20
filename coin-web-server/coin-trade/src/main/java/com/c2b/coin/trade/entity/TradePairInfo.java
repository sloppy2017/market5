package com.c2b.coin.trade.entity;

import java.io.Serializable;

/**
 * @author
 */
public class TradePairInfo implements Serializable {
  /**
   * 主键
   */
  private Long id;

  /**
   * 商品币
   */
  private Long commodityCoin;

  /**
   * 金钱币
   */
  private Long moneyCoin;

  /**
   * 商品币
   */
  private String commodityCoinName;

  /**
   * 金钱币
   */
  private String moneyCoinName;

  public String getCommodityCoinName() {
    return commodityCoinName;
  }

  public void setCommodityCoinName(String commodityCoinName) {
    this.commodityCoinName = commodityCoinName;
  }

  public String getMoneyCoinName() {
    return moneyCoinName;
  }

  public void setMoneyCoinName(String moneyCoinName) {
    this.moneyCoinName = moneyCoinName;
  }

  /**
   * 数据状态(0：未删除 1：已删除)
   */
  private Integer dataStatus;

  /**
   * 创建时间
   */
  private Long createTime;

  /**
   * 修改时间
   */
  private Long updateTime;

  /**
   * 备注
   */
  private String remark;

  private static final long serialVersionUID = 1L;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCommodityCoin() {
    return commodityCoin;
  }

  public void setCommodityCoin(Long commodityCoin) {
    this.commodityCoin = commodityCoin;
  }

  public Long getMoneyCoin() {
    return moneyCoin;
  }

  public void setMoneyCoin(Long moneyCoin) {
    this.moneyCoin = moneyCoin;
  }

  public Integer getDataStatus() {
    return dataStatus;
  }

  public void setDataStatus(Integer dataStatus) {
    this.dataStatus = dataStatus;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }
}