package com.c2b.coin.account.feign;


import java.io.Serializable;

import com.c2b.coin.common.CodeConstant;



/**
 * 封装ajax返回
 * @author guowei
 *
 */
public class ClientAjaxResponse implements Serializable {

	private static final long serialVersionUID = -7145191916102005908L;

	private short errorCode ;

	private String message;

	private Object data;

	private Boolean success;

	public ClientAjaxResponse (){

	}

	public ClientAjaxResponse (String message, Object data, Boolean success){
	    this.message = message;
	    this.data = data;
	    this.success = success;
	}

	public ClientAjaxResponse (String message, Object data, Boolean success,short errorCode){
        this.message = message;
        this.data = data;
        this.success = success;
        this.errorCode = errorCode;
    }

	public static ClientAjaxResponse success(String message, Object data){
		ClientAjaxResponse ar = new ClientAjaxResponse(message, data, true);
	    ar.setErrorCode(CodeConstant.SC_OK);
	    return ar;
	}

	public static ClientAjaxResponse falied(String message, Object data){
		ClientAjaxResponse ar = new ClientAjaxResponse(message, data, false);
        ar.setErrorCode(CodeConstant.SC_UNKNOWN);
        return ar;
    }

	public static ClientAjaxResponse falied(String message){
		ClientAjaxResponse ar = new ClientAjaxResponse(message, null, false);
        ar.setErrorCode(CodeConstant.SC_UNKNOWN);
        return ar;
    }

	public static ClientAjaxResponse falied(String message,short errorCode){
        return new ClientAjaxResponse(message, null, false,errorCode);
    }

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public short getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(short paramError) {
		this.errorCode = paramError;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "AjaxResponse [errorCode=" + errorCode + ", coin.message=" + message
				+ ", data=" + data + ", success=" + success + "]";
	}

}
