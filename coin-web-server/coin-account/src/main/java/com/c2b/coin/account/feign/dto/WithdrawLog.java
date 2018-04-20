package com.c2b.coin.account.feign.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

public class WithdrawLog {
	
	@ApiModelProperty(value = "主键")
    private String id;

	@ApiModelProperty(value = "账号")
    private String account;

	@ApiModelProperty(value = "系统编码")
    private String syscode;

	@ApiModelProperty(value = "提币金额")
    private BigDecimal money;

	@ApiModelProperty(value = "提币状态")
    private String status;

	@ApiModelProperty(value = "提币种类")
    private String currency;

	@ApiModelProperty(value = "提币地址")
    private String toAddress;

	@ApiModelProperty(value = "订单号")
    private String orderNo;

	@ApiModelProperty(value = "创建时间")
    private Date createTime;

	@ApiModelProperty(value = "更新时间")
    private Date updateTime;

	@ApiModelProperty(value = "矿工费")
    private BigDecimal free;

	@ApiModelProperty(value = "交易hash")
    private String txHash;

	@ApiModelProperty(value = "提现json")
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
