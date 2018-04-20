package com.c2b.coin.trade.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 
 */
public class OrderLog implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 委托单号
     */
    private String consignationNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 成交价
     */
    private BigDecimal madePrice;

    /**
     * 成交量
     */
    private BigDecimal madeCount;

    /**
     * 成交时间
     */
    private Long madeTime;

    /**
     * 交易币种
     */
    private String bizType;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 成交前委托量
     */
    private BigDecimal preConsignationCount;

    /**
     * 成交后委托量
     */
    private BigDecimal afterConsignationCount;

    /**
     * 总委托量
     */
    private BigDecimal consignationCount;

    /**
     * 委托价
     */
    private BigDecimal consignationPrice;
    
    /**
     * 成交均价
     */
    private BigDecimal madeAveragePrice;

    /**
     * 委托时间
     */
    private Long consignationTime;

    /**
     * remark
     */
    private String remark;

    /**
     * 成交手续费
     */
    private BigDecimal poundage;

    /**
     * 订单类型(限价交易/市价交易)
     */
    private Integer orderType;

    private Integer userId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConsignationNo() {
        return consignationNo;
    }

    public void setConsignationNo(String consignationNo) {
        this.consignationNo = consignationNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getMadePrice() {
        return madePrice;
    }

    public void setMadePrice(BigDecimal madePrice) {
        this.madePrice = madePrice;
    }

    public BigDecimal getMadeCount() {
        return madeCount;
    }

    public void setMadeCount(BigDecimal madeCount) {
        this.madeCount = madeCount;
    }

    public Long getMadeTime() {
        return madeTime;
    }

    public void setMadeTime(Long madeTime) {
        this.madeTime = madeTime;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getPreConsignationCount() {
        return preConsignationCount;
    }

    public void setPreConsignationCount(BigDecimal preConsignationCount) {
        this.preConsignationCount = preConsignationCount;
    }

    public BigDecimal getAfterConsignationCount() {
        return afterConsignationCount;
    }

    public void setAfterConsignationCount(BigDecimal afterConsignationCount) {
        this.afterConsignationCount = afterConsignationCount;
    }

    public BigDecimal getConsignationCount() {
        return consignationCount;
    }

    public void setConsignationCount(BigDecimal consignationCount) {
        this.consignationCount = consignationCount;
    }

    public BigDecimal getConsignationPrice() {
        return consignationPrice;
    }

    public void setConsignationPrice(BigDecimal consignationPrice) {
        this.consignationPrice = consignationPrice;
    }

    public Long getConsignationTime() {
        return consignationTime;
    }

    public void setConsignationTime(Long consignationTime) {
        this.consignationTime = consignationTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getPoundage() {
        return poundage;
    }

    public void setPoundage(BigDecimal poundage) {
        this.poundage = poundage;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getUserId() {
      return userId;
    }

    public void setUserId(Integer userId) {
      this.userId = userId;
    }

    public BigDecimal getMadeAveragePrice() {
      return madeAveragePrice;
    }

    public void setMadeAveragePrice(BigDecimal madeAveragePrice) {
      this.madeAveragePrice = madeAveragePrice;
    }
    
}