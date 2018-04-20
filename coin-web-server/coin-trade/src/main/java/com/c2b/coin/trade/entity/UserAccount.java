package com.c2b.coin.trade.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "user_account")
public class UserAccount {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 账户币种
     */
    @Column(name = "currency_type")
    private Integer currencyType;

    /**
     * 总额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 可用余额
     */
    @Column(name = "available_amount")
    private BigDecimal availableAmount;

    /**
     * 冻结
     */
    @Column(name = "freezing_amount")
    private BigDecimal freezingAmount;

    /**
     * 账户状态
     */
    @Column(name = "account_type")
    private Integer accountType;

    /**
     * 账户地址
     */
    @Column(name = "account_address")
    private String accountAddress;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Long updateTime;

    /**
     * 创建时间
     */
    private Long createtime;

    /**
     * 获取id
     *
     * @return id - id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取账户币种
     *
     * @return currency_type - 账户币种
     */
    public Integer getCurrencyType() {
        return currencyType;
    }

    /**
     * 设置账户币种
     *
     * @param currencyType 账户币种
     */
    public void setCurrencyType(Integer currencyType) {
        this.currencyType = currencyType;
    }

    /**
     * 获取总额
     *
     * @return total_amount - 总额
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置总额
     *
     * @param totalAmount 总额
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取可用余额
     *
     * @return available_amount - 可用余额
     */
    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    /**
     * 设置可用余额
     *
     * @param availableAmount 可用余额
     */
    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    /**
     * 获取冻结
     *
     * @return freezing_amount - 冻结
     */
    public BigDecimal getFreezingAmount() {
        return freezingAmount;
    }

    /**
     * 设置冻结
     *
     * @param freezingAmount 冻结
     */
    public void setFreezingAmount(BigDecimal freezingAmount) {
        this.freezingAmount = freezingAmount;
    }

    /**
     * 获取账户状态
     *
     * @return account_type - 账户状态
     */
    public Integer getAccountType() {
        return accountType;
    }

    /**
     * 设置账户状态
     *
     * @param accountType 账户状态
     */
    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    /**
     * 获取账户地址
     *
     * @return account_address - 账户地址
     */
    public String getAccountAddress() {
        return accountAddress;
    }

    /**
     * 设置账户地址
     *
     * @param accountAddress 账户地址
     */
    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Long getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取创建时间
     *
     * @return createtime - 创建时间
     */
    public Long getCreatetime() {
        return createtime;
    }

    /**
     * 设置创建时间
     *
     * @param createtime 创建时间
     */
    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }
}
