package com.c2b.coin.account.entity;

import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "asset_change_log")
public class AssetChangeLog {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * user_id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * username
     */
    private String username;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    @Column(name="currency_type")
    private Integer currencyType;
    
    /**
     * 变动类型（增加，减少）
     */
    @Column(name = "change_type")
    private Integer changeType;

    /**
     * 业务类型（1充值，2提现，3买入，4卖出）
     */
    @Column(name = "biz_type")
    private Integer bizType;

    /**
     * 变动账户
     */
    @Column(name = "account_id")
    private Long accountId;

    /**
     * 变动前资产
     */
    @Column(name = "pre_balance")
    private BigDecimal preBalance;

    /**
     * 变动后资产
     */
    @Column(name = "after_balance")
    private BigDecimal afterBalance;

    /**
     * 变更时间
     */
    @Column(name = "update_time")
    private Long updateTime;

    /**
     * 费用类型
     */
    @Column(name = "fee_type")
    private Integer feeType;

    /**
     * 变动金额
     */
    @Column(name = "change_asset")
    private BigDecimal changeAsset;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取user_id
     *
     * @return user_id - user_id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置user_id
     *
     * @param userId user_id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取username
     *
     * @return username - username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置username
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取订单号
     *
     * @return order_no - 订单号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单号
     *
     * @param orderNo 订单号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取变动类型（增加，减少）
     *
     * @return change_type - 变动类型（增加，减少）
     */
    public Integer getChangeType() {
        return changeType;
    }

    /**
     * 设置变动类型（增加，减少）
     *
     * @param changeType 变动类型（增加，减少）
     */
    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    /**
     * 获取业务类型（1充值，2提现，3买入，4卖出）
     *
     * @return biz_type - 业务类型（1充值，2提现，3买入，4卖出）
     */
    public Integer getBizType() {
        return bizType;
    }

    /**
     * 设置业务类型（1充值，2提现，3买入，4卖出）
     *
     * @param bizType 业务类型（1充值，2提现，3买入，4卖出）
     */
    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    /**
     * 获取变动账户
     *
     * @return account_id - 变动账户
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * 设置变动账户
     *
     * @param accountId 变动账户
     */
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /**
     * 获取变动前资产
     *
     * @return pre_balance - 变动前资产
     */
    public BigDecimal getPreBalance() {
        return preBalance;
    }

    /**
     * 设置变动前资产
     *
     * @param preBalance 变动前资产
     */
    public void setPreBalance(BigDecimal preBalance) {
        this.preBalance = preBalance;
    }

    /**
     * 获取变动后资产
     *
     * @return after_balance - 变动后资产
     */
    public BigDecimal getAfterBalance() {
        return afterBalance;
    }

    /**
     * 设置变动后资产
     *
     * @param afterBalance 变动后资产
     */
    public void setAfterBalance(BigDecimal afterBalance) {
        this.afterBalance = afterBalance;
    }

    /**
     * 获取变更时间
     *
     * @return update_time - 变更时间
     */
    public Long getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置变更时间
     *
     * @param updateTime 变更时间
     */
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取费用类型
     *
     * @return fee_type - 费用类型
     */
    public Integer getFeeType() {
        return feeType;
    }

    /**
     * 设置费用类型
     *
     * @param feeType 费用类型
     */
    public void setFeeType(Integer feeType) {
        this.feeType = feeType;
    }

    /**
     * 获取变动金额
     *
     * @return change_asset - 变动金额
     */
    public BigDecimal getChangeAsset() {
        return changeAsset;
    }

    /**
     * 设置变动金额
     *
     * @param changeAsset 变动金额
     */
    public void setChangeAsset(BigDecimal changeAsset) {
        this.changeAsset = changeAsset;
    }

	public Integer getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(Integer currencyType) {
		this.currencyType = currencyType;
	}
    
    
}