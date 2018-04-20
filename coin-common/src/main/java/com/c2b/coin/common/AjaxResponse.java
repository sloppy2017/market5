package com.c2b.coin.common;

/**
 * @author MMM
 * @desc HTTP请求返回信息实体类
 * @project delicacy-cloud
 * @date 2017-02-07 9:21
 **/
public class AjaxResponse {
  private Object data;
  private Error error;
  private boolean success;

  public AjaxResponse(){};

  public AjaxResponse(Object data) {
    this.success = true;
    this.data = data;
  }
  public static AjaxResponse success(Object data) {
    return new AjaxResponse(data);
  }
  public AjaxResponse(String code, String message) {
    this.success = false;
    this.error = new Error(code, message);
  }
  public static AjaxResponse failure(String code, String message) {
    return new AjaxResponse(code, message);
  }
  public AjaxResponse(String message) {
    this.success = false;
    this.error = new Error("-1", message);
  }

  public static AjaxResponse failure(String message) {
    return new AjaxResponse(message);
  }

  public Error getError() {
    return this.error;
  }

  public Object getData() {
    return data;
  }

  public Boolean isSuccess() {
    return this.success;
  }

  public class Error {
    private String code;
    private String detail;

    public Error(String code, String detail) {
      this.code = code;
      this.detail = detail;
    }

    public Error() {
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getDetail() {
      return detail;
    }

    public void setDetail(String detail) {
      this.detail = detail;
    }
  }
}
