package com.c2b.coin.web.common.rest;

import com.c2b.coin.web.common.rest.bean.RestResponseBean;
import com.c2b.coin.web.common.enums.IRestResponseCode;

/**
 * BaseController
 *
 * @author tangwei
 */
public class RestBaseController {

	public RestResponseBean onFailure() {
		return RestResponseBean.onFailure();
	}

	public RestResponseBean onFailure(IRestResponseCode responseCode) {
		return responseCode == null ? onFailure() : RestResponseBean.onFailure(responseCode);
	}

	public RestResponseBean onFailure(String errCode, String errMsg) {
		return RestResponseBean.onFailure(errCode, errMsg);
	}

  public RestResponseBean onSuccess() {
    return onSuccess(null);
  }

	public <T> RestResponseBean<T> onSuccess(T data) {
		return RestResponseBean.onSuccess(data);
	}

}
