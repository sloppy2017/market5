package com.c2b.coin.user.api;

import com.c2b.coin.common.AjaxResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequestMapping
public interface UserApi {
  @RequestMapping(value="/check/payPwd",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  AjaxResponse checkPayPwdAndVerifyCode(@RequestParam("userId") Long userId,@RequestParam("payPwd") String payPwd,@RequestParam("verifyType") String verifyType,@RequestParam("verifyCode") String verifyCode);
  @RequestMapping(value="/check/withdraw",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  AjaxResponse checkWithdraw(@RequestParam("userId") Long userId);

  @RequestMapping(value="/client/user",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  Map<String,Object> findBuserByUserId(@RequestParam("userId") Long userId);

  @RequestMapping(value = "/client/user/region", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  String getUserRegion(@RequestParam("userId") String userId);
}
