package com.c2b.coin.api.controller.v1.account;

import com.c2b.coin.account.api.AccountClient;
import com.c2b.coin.api.annotation.Sign;
import com.c2b.coin.api.controller.BaseController;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.rest.bean.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/account")
@Api(value = "/v1/account", description = "资产")
public class UserAccountController extends BaseController {

  @Autowired
  private AccountClient accountClient;

  @Sign
  @GetMapping(value = "/asset/total")
  @ApiOperation(value = "获取用户总资产")
  public ResponseBean getAssetTotal() {
    AjaxResponse response = accountClient.getAssetTotal(Long.parseLong(getThreadContextMap().getUserId()));
    if (response.isSuccess()) {
      return onSuccess(response.getData());
    } else {
      return onFailure(ErrorMsgEnum.SERVER_BUSY);
    }
  }

  @Sign
  @GetMapping(value = "/asset/list")
  @ApiOperation(value = "获取用户列表")
  public ResponseBean getAssetList() {
    return onSuccess(accountClient.getAssetList(Long.parseLong(getThreadContextMap().getUserId())));
  }

}
