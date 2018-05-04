package com.c2b.coin.account.entity.vo;

import java.io.Serializable;
import java.math.BigDecimal;


public class AccountAssetVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6457536789225273682L;

	private int currencyType;
	private String currencyName;
	private String currencyFullName;
	private BigDecimal totalAmount;
	private BigDecimal availableAmount;
	private BigDecimal freezingAmount;
	private int chargeStatus;
	private int withdrawStatus;
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getCurrencyFullName() {
		return currencyFullName;
	}
	public void setCurrencyFullName(String currencyFullName) {
		this.currencyFullName = currencyFullName;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}
	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}
	public BigDecimal getFreezingAmount() {
		return freezingAmount;
	}
	public void setFreezingAmount(BigDecimal freezingAmount) {
		this.freezingAmount = freezingAmount;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getCurrencyType() {
		return currencyType;
	}
	public void setCurrencyType(int currencyType) {
		this.currencyType = currencyType;
	}
	
	public AccountAssetVO(int currencyType, String currencyName, String currencyFullName, BigDecimal totalAmount,
			BigDecimal availableAmount, BigDecimal freezingAmount, int chargeStatus, int withdrawStatus) {
		super();
		this.currencyType = currencyType;
		this.currencyName = currencyName;
		this.currencyFullName = currencyFullName;
		this.totalAmount = totalAmount;
		this.availableAmount = availableAmount;
		this.freezingAmount = freezingAmount;
		this.chargeStatus = chargeStatus;
		this.withdrawStatus = withdrawStatus;
	}
	public AccountAssetVO() {
		super();
	}
	public int getChargeStatus() {
		return chargeStatus;
	}
	public void setChargeStatus(int chargeStatus) {
		this.chargeStatus = chargeStatus;
	}
	public int getWithdrawStatus() {
		return withdrawStatus;
	}
	public void setWithdrawStatus(int withdrawStatus) {
		this.withdrawStatus = withdrawStatus;
	}
	
	
	
}
