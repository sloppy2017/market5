package com.c2b.wallet.entity;



import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.ToString;
@ToString // 所有属性toString
@Data //设置所有属性的 get set 方法
public class WithdrawLog {
	

    private String id;

    private String account;

    private String syscode;

    private BigDecimal money;

    private String status;

    private String currency;

    private String toAddress;

    private String orderNo;

    private Date createTime;

    private Date updateTime;

    private BigDecimal free;

    private String txHash;

    private String withdrawMsg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getSyscode() {
        return syscode;
    }

    public void setSyscode(String syscode) {
        this.syscode = syscode == null ? null : syscode.trim();
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress == null ? null : toAddress.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public BigDecimal getFree() {
        return free;
    }

    public void setFree(BigDecimal free) {
        this.free = free;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public String getWithdrawMsg() {
        return withdrawMsg;
    }

    public void setWithdrawMsg(String withdrawMsg) {
        this.withdrawMsg = withdrawMsg == null ? null : withdrawMsg.trim();
    }
}