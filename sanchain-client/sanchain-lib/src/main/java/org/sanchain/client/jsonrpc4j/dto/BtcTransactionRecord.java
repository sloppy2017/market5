package org.sanchain.client.jsonrpc4j.dto;

import java.math.BigDecimal;
import java.util.Date;


public class BtcTransactionRecord {
    private String blockid;

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

    private String listsinceblock;

    private String lastblock;

    private Short state;

    private String userid;

    private String depositid;

    private String errcode;

    private Date createdate;

    private Date lastupdatedate;

    private String rev1;

    private String rev2;

    private String rev3;

    private String rev4;

    private String rev5;

    private String rev6;

    private String loginName;//登录名
    
    public enum StateEnum {
		//未处理 
		notdeal(Short.valueOf("1")),
		// 已处理
		deal(Short.valueOf("2"));

		private Short value;

		private StateEnum(Short value) {
			this.value = value;
		}

		public Short getValue() {
			return this.value;
		}

		public static StateEnum get(Short value) {
			for (StateEnum o : StateEnum.values()) {
				if (o.getValue().compareTo(value) == 0) {
					return o;
				}
			}
			return null;
		}
	}
    
    public String getBlockid() {
        return blockid;
    }

    public void setBlockid(String blockid) {
        this.blockid = blockid == null ? null : blockid.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
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
        this.blockhash = blockhash == null ? null : blockhash.trim();
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
        this.txid = txid == null ? null : txid.trim();
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

    public String getListsinceblock() {
        return listsinceblock;
    }

    public void setListsinceblock(String listsinceblock) {
        this.listsinceblock = listsinceblock == null ? null : listsinceblock.trim();
    }

    public String getLastblock() {
        return lastblock;
    }

    public void setLastblock(String lastblock) {
        this.lastblock = lastblock == null ? null : lastblock.trim();
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    public String getDepositid() {
        return depositid;
    }

    public void setDepositid(String depositid) {
        this.depositid = depositid == null ? null : depositid.trim();
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode == null ? null : errcode.trim();
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Date getLastupdatedate() {
        return lastupdatedate;
    }

    public void setLastupdatedate(Date lastupdatedate) {
        this.lastupdatedate = lastupdatedate;
    }

    public String getRev1() {
        return rev1;
    }

    public void setRev1(String rev1) {
        this.rev1 = rev1 == null ? null : rev1.trim();
    }

    public String getRev2() {
        return rev2;
    }

    public void setRev2(String rev2) {
        this.rev2 = rev2 == null ? null : rev2.trim();
    }

    public String getRev3() {
        return rev3;
    }

    public void setRev3(String rev3) {
        this.rev3 = rev3 == null ? null : rev3.trim();
    }

    public String getRev4() {
        return rev4;
    }

    public void setRev4(String rev4) {
        this.rev4 = rev4 == null ? null : rev4.trim();
    }

    public String getRev5() {
        return rev5;
    }

    public void setRev5(String rev5) {
        this.rev5 = rev5 == null ? null : rev5.trim();
    }

    public String getRev6() {
        return rev6;
    }

    public void setRev6(String rev6) {
        this.rev6 = rev6 == null ? null : rev6.trim();
    }

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
}