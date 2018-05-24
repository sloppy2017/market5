package com.c2b.coin.web.common.rest;

import com.c2b.coin.web.common.rest.bean.ResponseBean;
import com.c2b.coin.web.common.enums.IResponseCode;

/**
 * BaseController
 *
 * @author tangwei
 */
public class BaseController {

	public ResponseBean onFailure() {
		return ResponseBean.onFailure();
	}

	public ResponseBean onFailure(IResponseCode responseCode) {
		return responseCode == null ? onFailure() : ResponseBean.onFailure(responseCode);
	}

	public ResponseBean onFailure(String errCode, String errMsg) {
		return ResponseBean.onFailure(errCode, errMsg);
	}

  public ResponseBean onSuccess() {
    return onSuccess(null);
  }

	public <T> ResponseBean<T> onSuccess(T data) {
		return ResponseBean.onSuccess(data);
	}

}
