package com.c2b.coin.account.entity.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class AssetTotalVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6025965299483478203L;
	private BigDecimal totalBTC;
	private double totalUSD;
	private BigDecimal availableBTC;
	private double availableUSD;
	private BigDecimal freezingBTC;
	private double freezingUSD;
	public BigDecimal getTotalBTC() {
		return totalBTC;
	}
	public void setTotalBTC(BigDecimal totalBTC) {
		this.totalBTC = totalBTC;
	}
	public BigDecimal getAvailableBTC() {
		return availableBTC;
	}
	public void setAvailableBTC(BigDecimal availableBTC) {
		this.availableBTC = availableBTC;
	}
	public BigDecimal getFreezingBTC() {
		return freezingBTC;
	}
	public void setFreezingBTC(BigDecimal freezingBTC) {
		this.freezingBTC = freezingBTC;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public double getTotalUSD() {
		return totalUSD;
	}
	public void setTotalUSD(double totalUSD) {
		this.totalUSD = totalUSD;
	}
	public double getAvailableUSD() {
		return availableUSD;
	}
	public void setAvailableUSD(double availableUSD) {
		this.availableUSD = availableUSD;
	}
	public double getFreezingUSD() {
		return freezingUSD;
	}
	public void setFreezingUSD(double freezingUSD) {
		this.freezingUSD = freezingUSD;
	}
	public AssetTotalVO(BigDecimal totalBTC, double totalUSD, BigDecimal availableBTC, double availableUSD,
			BigDecimal freezingBTC, double freezingUSD) {
		super();
		this.totalBTC = totalBTC;
		this.totalUSD = totalUSD;
		this.availableBTC = availableBTC;
		this.availableUSD = availableUSD;
		this.freezingBTC = freezingBTC;
		this.freezingUSD = freezingUSD;
	}
	public AssetTotalVO() {
		super();
	}
	
	
}
