package com.c2b.coin.web.common.rest.bean;

import com.c2b.coin.web.common.enums.IRestResponseCode;

import java.util.Date;

/**
 * ResponseBean
 *
 * @author tangwei
 *
 */
public class RestResponseBean<T> {

  public enum Status {

    OK("ok"),
    ERROR("error");

    private String code;

    Status(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }

  }

  private String status;

  private String errCode;

  private String errMsg;

  private long ts;

  private T data;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getErrCode() {
    return errCode;
  }

  public void setErrCode(String errCode) {
    this.errCode = errCode;
  }

  public String getErrMsg() {
    return errMsg;
  }

  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }

  public long getTs() {
    return ts;
  }

  public void setTs(long ts) {
    this.ts = ts;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  private void fillResponse(IRestResponseCode responseCode, T data) {
    if (responseCode != null) {
      setErrCode(responseCode.getCode());
      setErrMsg(responseCode.getMessage());
    }
    setData(data);
  }

  public static <T> RestResponseBean<T> onSuccess() {
    RestResponseBean responseBean = new RestResponseBean();
    responseBean.setStatus(Status.OK.getCode());
    responseBean.setTs(new Date().getTime());
    responseBean.fillResponse(null, null);
    return responseBean;
  }

  public static <T> RestResponseBean<T> onSuccess(T data) {
    RestResponseBean responseBean = new RestResponseBean();
    responseBean.setStatus(Status.OK.getCode());
    responseBean.setTs(new Date().getTime());
    responseBean.fillResponse(null, data);
    return responseBean;
  }

  public static <T> RestResponseBean<T> onFailure() {
    RestResponseBean responseBean = new RestResponseBean();
    responseBean.setStatus(Status.ERROR.getCode());
    responseBean.setTs(new Date().getTime());
    return responseBean;
  }

  public static <T> RestResponseBean<T> onFailure(IRestResponseCode responseCode) {
    RestResponseBean responseBean = new RestResponseBean();
    responseBean.setStatus(Status.ERROR.getCode());
    responseBean.setTs(new Date().getTime());
    responseBean.fillResponse(responseCode, null);
    return responseBean;
  }

  public static <T> RestResponseBean<T> onFailure(String errCode, String errMsg) {
    RestResponseBean responseBean = new RestResponseBean();
    responseBean.setStatus(Status.ERROR.getCode());
    responseBean.setTs(new Date().getTime());
    responseBean.setErrCode(errCode);
    responseBean.setErrMsg(errMsg);
    return responseBean;
  }

}
