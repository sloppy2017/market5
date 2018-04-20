package com.c2b.coin.account.exception;

import com.c2b.coin.common.enumeration.ErrorMsgEnum;

public class AssetChangeException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8280749502419885623L;
	private  ErrorMsgEnum msg;

	public AssetChangeException(ErrorMsgEnum msg,String errorDetail) {
		super(errorDetail);
		this.msg = msg;
	}

	public ErrorMsgEnum getMsg() {
		return msg;
	}

	public void setMsg(ErrorMsgEnum msg) {
		this.msg = msg;
	}
    
}
