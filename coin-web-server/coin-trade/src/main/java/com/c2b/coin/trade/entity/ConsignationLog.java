package com.c2b.coin.trade.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "consignation_log")
public class ConsignationLog {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 委托人id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 委托人
     */
    private String username;

    /**
     * 委托单号
     */
    @Column(name = "consignation_no")
    private String consignationNo;

    /**
     * 交易对(如BTC/ETH)
     */
    @Column(name = "biz_type")
    private String bizType;

    /**
     * 交易类型
     */
    @Column(name = "trade_type")
    private String tradeType;

    /**
     * 委托价
     */
    @Column(name = "consignation_price")
    private BigDecimal consignationPrice;

    /**
     * 委托量
     */
    @Column(name = "consignation_count")
    private BigDecimal consignationCount;

    /**
     * 已成交量
     */
    @Column(name = "made_count")
    private BigDecimal madeCount;

    /**
     * 已成交总额
     */
    @Column(name = "made_price")
    private BigDecimal madePrice;

    /**
     * 委托状态
     */
    @Column(name = "consignation_status")
    private Integer consignationStatus;

    /**
     * 数据状态
     */
    @Column(name = "data_status")
    private Integer dataStatus;

    /**
     * 委托时间
     */
    @Column(name = "create_time")
    private Long createTime;

    /**
     * 最后成交时间
     */
    @Column(name = "made_time")
    private Long madeTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Long updateTime;

    private String remark;

    /**
     * 可解冻数量
     */
    @Transient
    private BigDecimal ufzAmount;

    /**
     * 成交价
     */
    @Transient
    private BigDecimal price;

    /**
     * 成交均价
     */
    @Column(name = "made_average_price")
    private BigDecimal madeAveragePrice;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取委托人id
     *
     * @return user_id - 委托人id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置委托人id
     *
     * @param userId 委托人id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取委托人
     *
     * @return username - 委托人
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置委托人
     *
     * @param username 委托人
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取委托单号
     *
     * @return consignation_no - 委托单号
     */
    public String getConsignationNo() {
        return consignationNo;
    }

    /**
     * 设置委托单号
     *
     * @param consignationNo 委托单号
     */
    public void setConsignationNo(String consignationNo) {
        this.consignationNo = consignationNo;
    }

    /**
     * 获取交易对(如BTC/ETH)
     *
     * @return biz_type - 交易对(如BTC/ETH)
     */
    public String getBizType() {
        return bizType;
    }

    /**
     * 设置交易对(如BTC/ETH)
     *
     * @param bizType 交易对(如BTC/ETH)
     */
    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    /**
     * 获取交易类型
     *
     * @return trade_type - 交易类型
     */
    public String getTradeType() {
        return tradeType;
    }

    /**
     * 设置交易类型
     *
     * @param tradeType 交易类型
     */
    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    /**
     * 获取委托价
     *
     * @return consignation_price - 委托价
     */
    public BigDecimal getConsignationPrice() {
        return consignationPrice;
    }

    /**
     * 设置委托价
     *
     * @param consignationPrice 委托价
     */
    public void setConsignationPrice(BigDecimal consignationPrice) {
        this.consignationPrice = consignationPrice;
    }

    /**
     * 获取委托量
     *
     * @return consignation_count - 委托量
     */
    public BigDecimal getConsignationCount() {
        return consignationCount;
    }

    /**
     * 设置委托量
     *
     * @param consignationCount 委托量
     */
    public void setConsignationCount(BigDecimal consignationCount) {
        this.consignationCount = consignationCount;
    }

    /**
     * 获取已成交量
     *
     * @return made_count - 已成交量
     */
    public BigDecimal getMadeCount() {
        return madeCount;
    }

    /**
     * 设置已成交量
     *
     * @param madeCount 已成交量
     */
    public void setMadeCount(BigDecimal madeCount) {
        this.madeCount = madeCount;
    }

    /**
     * 获取已成交总额
     *
     * @return made_price - 已成交总额
     */
    public BigDecimal getMadePrice() {
        return madePrice;
    }

    /**
     * 设置已成交总额
     *
     * @param madePrice 已成交总额
     */
    public void setMadePrice(BigDecimal madePrice) {
        this.madePrice = madePrice;
    }

    /**
     * 获取委托状态
     *
     * @return consignation_status - 委托状态
     */
    public Integer getConsignationStatus() {
        return consignationStatus;
    }

    /**
     * 设置委托状态
     *
     * @param consignationStatus 委托状态
     */
    public void setConsignationStatus(Integer consignationStatus) {
        this.consignationStatus = consignationStatus;
    }

    /**
     * 获取数据状态
     *
     * @return data_status - 数据状态
     */
    public Integer getDataStatus() {
        return dataStatus;
    }

    /**
     * 设置数据状态
     *
     * @param dataStatus 数据状态
     */
    public void setDataStatus(Integer dataStatus) {
        this.dataStatus = dataStatus;
    }

    /**
     * 获取委托时间
     *
     * @return create_time - 委托时间
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * 设置委托时间
     *
     * @param createTime 委托时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取最后成交时间
     *
     * @return made_time - 最后成交时间
     */
    public Long getMadeTime() {
        return madeTime;
    }

    /**
     * 设置最后成交时间
     *
     * @param madeTime 最后成交时间
     */
    public void setMadeTime(Long madeTime) {
        this.madeTime = madeTime;
    }

    /**
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Long getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
     */
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getUfzAmount() {
      return ufzAmount;
    }

    public void setUfzAmount(BigDecimal ufzAmount) {
      this.ufzAmount = ufzAmount;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public void setPrice(BigDecimal price) {
      this.price = price;
    }

    public BigDecimal getMadeAveragePrice() {
      return madeAveragePrice;
    }

    public void setMadeAveragePrice(BigDecimal madeAveragePrice) {
      this.madeAveragePrice = madeAveragePrice;
    }

}
