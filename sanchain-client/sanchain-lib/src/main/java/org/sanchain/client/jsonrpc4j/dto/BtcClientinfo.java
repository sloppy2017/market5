package org.sanchain.client.jsonrpc4j.dto;

import java.math.BigDecimal;

public class BtcClientinfo {
	/*
	{
	"version" : 80600,
	"protocolversion" : 70001,
	"walletversion" : 60000,
	"balance" : 0.00000000,
	"blocks" : 141201,
	"timeoffset" : 0,
	"connections" : 12,
	"proxy" : "",
	"difficulty" : 1805700.83619367,
	"testnet" : false,
	"keypoololdest" : 1388302949,
	"keypoolsize" : 101,
	"paytxfee" : 0.00000000,
	"unlocked_until" : 0,
	"errors" : ""
	}
	 */
	private BigDecimal version;
	private BigDecimal protocolversion;
	private BigDecimal walletversion;
	private BigDecimal balance;
	private Long blocks;
	private Long timeoffset;
	private Long connections;
	private String proxy;
	private BigDecimal difficulty;
	private Boolean testnet;
	private BigDecimal keypoololdest;
	private Long keypoolsize;
	private BigDecimal paytxfee;
	private Long unlocked_until;
	private String errors;
    private BigDecimal relayfee;

	public BigDecimal getVersion() {
		return version;
	}
	public void setVersion(BigDecimal version) {
		this.version = version;
	}
	public BigDecimal getProtocolversion() {
		return protocolversion;
	}
	public void setProtocolversion(BigDecimal protocolversion) {
		this.protocolversion = protocolversion;
	}
	public BigDecimal getWalletversion() {
		return walletversion;
	}
	public void setWalletversion(BigDecimal walletversion) {
		this.walletversion = walletversion;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public Long getBlocks() {
		return blocks;
	}
	public void setBlocks(Long blocks) {
		this.blocks = blocks;
	}
	public Long getTimeoffset() {
		return timeoffset;
	}
	public void setTimeoffset(Long timeoffset) {
		this.timeoffset = timeoffset;
	}
	public Long getConnections() {
		return connections;
	}
	public void setConnections(Long connections) {
		this.connections = connections;
	}
	public String getProxy() {
		return proxy;
	}
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	public BigDecimal getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(BigDecimal difficulty) {
		this.difficulty = difficulty;
	}
	public Boolean getTestnet() {
		return testnet;
	}
	public void setTestnet(Boolean testnet) {
		this.testnet = testnet;
	}
	public BigDecimal getKeypoololdest() {
		return keypoololdest;
	}
	public void setKeypoololdest(BigDecimal keypoololdest) {
		this.keypoololdest = keypoololdest;
	}
	public Long getKeypoolsize() {
		return keypoolsize;
	}
	public void setKeypoolsize(Long keypoolsize) {
		this.keypoolsize = keypoolsize;
	}
	public BigDecimal getPaytxfee() {
		return paytxfee;
	}
	public void setPaytxfee(BigDecimal paytxfee) {
		this.paytxfee = paytxfee;
	}
	public Long getUnlocked_until() {
		return unlocked_until;
	}
	public void setUnlocked_until(Long unlockedUntil) {
		unlocked_until = unlockedUntil;
	}
	public String getErrors() {
		return errors;
	}
	public void setErrors(String errors) {
		this.errors = errors;
	}

    public BigDecimal getRelayfee() {
        return relayfee;
    }

    public void setRelayfee(BigDecimal relayfee) {
        this.relayfee = relayfee;
    }
}
