package com.c2b.coin.sanchain.exception;

import com.c2b.coin.common.enumeration.ErrorMsgEnum;

public class SanchainException extends RuntimeException{

	 /**
		 * 
		 */
		private static final long serialVersionUID = -8280749502419885623L;
		private  ErrorMsgEnum msg;

		public SanchainException(ErrorMsgEnum msg,String errorDetail) {
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
