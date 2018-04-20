package org.sanchain.client.jsonrpc4j.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
	"walletconflicts"
})

public class BtcTransactionBlock {
	
	//v0.86
	/*
	"account" : "Member5&3445340",         --获取地址的标签,用户ID
	"address" : "1DiAkJCYd5zAoHhWccrKoYHfK72b8sq84B", --收发地址,全球唯一
	"category" : "receive", --receive or send
	"amount" : 100.00000000, --金额
	"confirmations" : 313, --确认次数 >2确认 0-已发送未确认
	"blockhash" : "0000000000000002b84a18d3a16856d668f12b5ff0b7b430abfac08e7355aff8", --从这条开始搜索,全球唯一
	"blockindex" : 80, --
	"blocktime" : 1388126246, --通知产生的时间
	"txid" : "d05856da508713d01eb9968d594b29aa94e2cac476a8bd9bf9180561f40afb74", --这笔交易的ID,全球唯一
	"time" : 1388126012, --
	"timereceived" : 1388126012 --
	 */
	//v0.91
	/*
	 {
		"account" : "dddddd",
		"address" : "12JpjnKpLJ8usKANs6wgpK32bMeRsa1iok",
		"category" : "receive",
		"amount" : 0.00010000,
		"confirmations" : 6056,
		"blockhash" : "0000000000000000d75566b1e84a4a5eb72e04a1724f4084714cb7028ca2d620",
		"blockindex" : 121,
		"blocktime" : 1394175300,
		"txid" : "0bce459f6147b3bed2254fa2193d58caa6566513bcae1d85edc1ec7fcf0217c8",
		"walletconflicts" : [
		],
		"time" : 1394174987,
		"timereceived" : 1394174987
		},
	 */
	private String account;
	private String address;
	private String category;
	private BigDecimal amount;
	private BigDecimal fee;
	private Long confirmations;
	private String blockhash;
	private Long blockindex;
	private Long blocktime;
	private String txid;
	private Long time;
	private Long timereceived;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(Long confirmations) {
		this.confirmations = confirmations;
	}

	public String getBlockhash() {
		return blockhash;
	}

	public void setBlockhash(String blockhash) {
		this.blockhash = blockhash;
	}

	public Long getBlockindex() {
		return blockindex;
	}

	public void setBlockindex(Long blockindex) {
		this.blockindex = blockindex;
	}

	public Long getBlocktime() {
		return blocktime;
	}

	public void setBlocktime(Long blocktime) {
		this.blocktime = blocktime;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getTimereceived() {
		return timereceived;
	}

	public void setTimereceived(Long timereceived) {
		this.timereceived = timereceived;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
}
