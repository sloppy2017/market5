package com.c2b.coin.user.rest;

import com.c2b.coin.common.*;
import com.c2b.coin.common.enumeration.*;
import com.c2b.coin.user.annotation.LoginSecondVerfiy;
import com.c2b.coin.user.api.UserApi;
import com.c2b.coin.user.dto.GoogleBindDto;
import com.c2b.coin.user.dto.ModifyPayPwdDto;
import com.c2b.coin.user.dto.PayPwd;
import com.c2b.coin.user.entity.UserInfo;
import com.c2b.coin.user.entity.UserRegInfo;
import com.c2b.coin.user.mapper.UserOperationLogMapper;
import com.c2b.coin.user.mapper.UserRegInfoMapper;
import com.c2b.coin.user.service.MessageService;
import com.c2b.coin.user.service.UserInfoService;
import com.c2b.coin.user.service.VerifiyService;
import com.c2b.coin.user.util.Common;
import com.c2b.coin.user.util.MqUtil;
import com.c2b.coin.user.util.Operation;
import com.c2b.coin.cache.redis.RedisUtil;
import com.c2b.coin.web.common.BaseRest;
import com.c2b.coin.web.common.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Api("用户相关服务")
@RestController
public class UserRest extends BaseRest implements UserApi {
  @Autowired
  UserInfoService userInfoService;
  @Autowired
  RedisUtil redisUtil;
  @Autowired
  MqUtil mqUtil;
  @Autowired
  VerifiyService verifiyService;
  @Autowired
  MessageService messageService;
  @Autowired
  UserOperationLogMapper userOperationLogMapper;
  @Autowired
  UserRegInfoMapper userRegInfoMapper;

  @ApiOperation(value = "生成谷歌验证码地址",notes = "{\"data\": {\"qrcode\": \"二维码\",\"sk\": \"密钥\",\n" +
    "    \"username\": \"用户名\"},\"error\": null,\"success\": true}", httpMethod = "POST")
  @RequestMapping(value = "/googleauth", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse getGoogleAuthAddress() {
    return writeObj(userInfoService.setGoogleSk(getUserId()));
  }


  @ApiOperation(value = "短信二次验证码校验", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "code", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/sms-verify-code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @LoginSecondVerfiy(value = "sms")
  public AjaxResponse smsVerifyCode(@RequestParam(name = "code", required = true) String code) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    if (verifiyService.verfiyCode(userInfo, Common.VerfiyType.SECOND_VALID_SMS, code)) {
      return writeObj(null);
    } else {
      return writeObj(ErrorMsgEnum.USER_VERFIY_CODE_ERROR);
    }
  }

  @ApiOperation(value = "发送绑定手机验证码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "mobile", value = "mobile", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "regionCode", value = "regionCode", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/send-bind-mobile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse sendBindMobile(@RequestParam(name = "regionCode", required = true) String regionCode,@RequestParam(name = "mobile", required = true) String mobile) {
    List<UserInfo> userInfoList = userInfoService.findUserInfoByMobile(mobile);
    if (null != userInfoList && userInfoList.size() != 0){
      return writeObj(ErrorMsgEnum.MOBILE_EXISTS);
    }
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    userInfo.setMobile(mobile);
    userInfo.setRegionCode(regionCode);
    messageService.sendSmsAndCache(userInfo,userInfo.getUsername(), MessageEnum.SMS_BIND_MOBILE, getLocal());
    return AjaxResponse.success(null);
  }

  @ApiOperation(value = "发送提币手机验证码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "mobile", value = "mobile", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/send-withdraw", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse sendWithdrawSms() {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    messageService.sendSmsAndCache(userInfo,userInfo.getUsername(), MessageEnum.SMS_WITHDRAW, getLocal());
    return AjaxResponse.success(null);
  }

  @ApiOperation(value = "发送解绑手机验证码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/send-unbind-mobile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse sendUnBindMobile() {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    messageService.sendSmsAndCache(userInfo,userInfo.getUsername(), MessageEnum.SMS_UNBIND_MOBILE, getLocal());
    return AjaxResponse.success(null);
  }

  @ApiOperation(value = "GOOGLE二次验证码校验", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "code", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/google-verify-code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.LOGIN_VERFIY)
  @LoginSecondVerfiy(value = "google")
  public AjaxResponse googleVerifyCode(@RequestParam(name = "code", required = true) String code) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    if (verifiyService.verfiyCode(userInfo, Common.VerfiyType.SECOND_VALID_GOOGLE, code)) {
      return writeObj(null);
    } else {
      return writeObj(ErrorMsgEnum.USER_VERFIY_CODE_ERROR);
    }
  }

  @Override
  protected String getUserId() {
    return super.getUserId();
  }

  @ApiOperation(value = "获取用户信息", notes = "{\"data\": {\"twoValid\": 0/1 是否开启二次验证（0/1 未开启/已开启）,\"lastLoginTime\": \"上次登录时间\",\"isAuth\": 是否实名认证（0/1/2/3 未认证/认证中/已认证/认证失败）,\n" +
    "    \"phone\": \"手机号\",\"payPwd\": 是否设置资金密码(0/1 未设置/已设置),\"mobile\": 是否绑定手机（0/1 未绑定/已绑定）,\"google\": 是否绑定google（0/1 未绑定/已绑定）,\"username\": \"用户名\"\n" +
    "  },\"error\": null,\"success\": true}", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse getUserInfo() {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    String phone = StringUtils.isEmpty(userInfo.getMobile()) ? "":userInfo.getMobile() ;
    Map<String, Object> result = new HashMap<>();
    result.put("username", userInfo.getUsername());
    result.put("twoValid", userInfo.getTwoValid());
    result.put("isAuth", StringUtils.isEmpty(userInfo.getIsAuth()) ? 0 : userInfo.getIsAuth());
    result.put("payPwd", StringUtils.isEmpty(userInfo.getPayPwd()) ? 0 : 1);
    result.put("mobile", StringUtils.isEmpty(phone) ? 0 : 1);
    result.put("phone", phone.length() < 5 ? "": phone.substring(0 ,2) +"*****" + phone.substring(phone.length()-2,phone.length()));
    result.put("email", StringUtils.isEmpty(userInfo.getEmail()) ? "" : userInfo.getEmail().replaceAll("(\\w{2}).+(\\w{2}@+)", "$1****$2"));
    result.put("google", userInfo.getGoogleBindStatus());
    Map<String, Object> loginLog = userOperationLogMapper.findLastLoginLog(userInfo.getUsername());
    if (null != loginLog){
      result.put("lastLoginTime", DateUtil.formateDate(new Date(Long.parseLong(String.valueOf(loginLog.get("create_time")))), DateUtil.DATE_FULL_STR));
    }else{
      result.put("lastLoginTime", DateUtil.formateDate(DateUtil.getCurrentDate(), DateUtil.DATE_FULL_STR));
      result.put("lastLoginAdd", "");
    }
    return writeObj(result);
  }

  @ApiOperation(value = "开启二次验证", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/open-second-valid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.OPEN_SECOND_VALID)
  public AjaxResponse secondValid() {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    if (StringUtils.isEmpty(userInfo.getMobile()) && StatusEnum.UNBIND.getStatusCode() == userInfo.getGoogleBindStatus()) {
      return writeObj(ErrorMsgEnum.USER_OPEN_SECOND_FAULIRE);
    } else {
      userInfo.setTwoValid(SecondValidStatusEnum.OPEN.ordinal());
      userInfoService.updateUser(userInfo);
      return writeObj(null);
    }
  }

  @ApiOperation(value = "关闭二次验证", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "verifyType", value = "验证类型（google/sms 1/0）", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/close-second-valid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.CLOSE_SECOND_VALID)
  public AjaxResponse closeSecondValid(@RequestParam(value = "verifyType") String verifyType, @RequestParam(value = "verifyCode")String verifyCode) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    boolean flag = false;
    if ("0".equals(verifyType)){
      flag = verifiyService.verfiyCode(userInfo, Common.VerfiyType.SECOND_VALID_SMS, verifyCode);
    }else{
      flag = verifiyService.verfiyCode(userInfo, Common.VerfiyType.SECOND_VALID_GOOGLE, verifyCode);
    }
    if (!flag){
      return writeObj(ErrorMsgEnum.USER_VERFIY_CODE_ERROR);
    }
    if ("0".equals(userInfo.getTwoValid())) {
      return writeObj(ErrorMsgEnum.USER_CLOSE_SECOND_FAULIRE);
    } else {
      userInfo.setTwoValid(SecondValidStatusEnum.CLOSE.ordinal());
      userInfoService.updateUser(userInfo);
      return writeObj(null);
    }
  }

  @ApiOperation(value = "绑定手机", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "code", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/mobile/bind", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.BIND_MOBILE)
  public AjaxResponse bindMobile(@RequestParam(required = true) String code) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    return writeObj(userInfoService.bindMobile(userInfo, code));
  }

  @ApiOperation(value = "解绑 手机", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "code", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/mobile/unbind", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.UNBIND_MOBILE)
  public AjaxResponse unBindMobile(@RequestParam(required = true) String code) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    return writeObj(userInfoService.unBindMobile(userInfo, "1", code, getLocal()));
  }

  @ApiOperation(value = "绑定GOOGLE", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/google/bind", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.BIND_GOOGLE)
  public AjaxResponse bindGoogle(@RequestBody GoogleBindDto googleBindDto) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    if (!userInfo.getPassword().equals(EncryptUtil.encrypt(googleBindDto.getPassword()))) {
      return writeObj(ErrorMsgEnum.USER_LOGIN_PWD_ERROR);
    }
    return writeObj(userInfoService.bindGoogle(userInfo, "0", googleBindDto.getCode(), getLocal()));
  }

  @ApiOperation(value = "解绑 GOOGLE", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "code", value = "code", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/google/unbind", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.UNBIND_GOOGLE)
  public AjaxResponse unbindGoogle(@RequestParam(required = true) String code) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    return writeObj(userInfoService.bindGoogle(userInfo, "1", code, getLocal()));
  }

  @ApiOperation(value = "验证交易密码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "payPwd", value = "资金密码", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/check-paypwd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse checkPayPwd(@RequestParam String payPwd) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    if (EncryptUtil.encrypt(payPwd).equals(userInfo.getPayPwd())) {
      return writeObj(null);
    } else {
      return writeObj(ErrorMsgEnum.USER_PAYPWD_ERROR);
    }
  }

  @ApiOperation(value = "修改登录密码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "oldPwd", value = "旧密码", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "newPwd", value = "新密码", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "confrimPwd", value = "确认密码", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/modify/loginpwd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.MODIFY_LOGIN_PWD)
  public AjaxResponse modifyLoginPwd(@RequestParam(required = true) String oldPwd, @RequestParam(required = true) String newPwd, @RequestParam(required = true) String confrimPwd) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    return writeObj(userInfoService.modifyLoginPwd(userInfo, oldPwd, newPwd, confrimPwd, getLocal()));
  }

  @ApiOperation(value = "修改资金密码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/modify/paypwd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.MODIFY_PAY_PWD)
  public AjaxResponse modifyPayPwd(@RequestBody ModifyPayPwdDto modifyPayPwdDto) {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    return writeObj(userInfoService.modifyPayPwd(userInfo, modifyPayPwdDto.getOldPwd(), modifyPayPwdDto.getNewPwd(), modifyPayPwdDto.getConfrimPwd(), getLocal()));
  }

  @ApiOperation(value = "校验资金密码（供后台服务使用，前端勿用）", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query", dataType = "long"),
    @ApiImplicitParam(name = "payPwd", value = "资金密码", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "verifyType", value = "验证类型（google/sms 1/0）", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, paramType = "query", dataType = "string")
  })
  @Override
  public AjaxResponse checkPayPwdAndVerifyCode(Long userId, String payPwd, String verifyType, String verifyCode) {

    UserInfo userInfo = userInfoService.findUserById(String.valueOf(userId));
    boolean flag = false;
    if ("0".equals(verifyType)){
      flag = verifiyService.verfiyCode(userInfo, Common.VerfiyType.WITHDRAW_SMS, verifyCode);
    }else{
      flag = verifiyService.verfiyCode(userInfo, Common.VerfiyType.SECOND_VALID_GOOGLE, verifyCode);
    }
    if (!flag){
      return writeObj(ErrorMsgEnum.USER_VERFIY_CODE_ERROR);
    }
    if (EncryptUtil.encrypt(payPwd).equals(userInfo.getPayPwd())) {
      return writeObj(null);
    } else {
      return writeObj(ErrorMsgEnum.USER_PAYPWD_ERROR);
    }
  }

  @ApiOperation(value = "设置资金密码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/paypwd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.SET_PAY_PWD)
  public AjaxResponse setPayPwd(@RequestBody PayPwd payPwd) {
    if (!Pattern.matches(Constants.REGEX_PAY_PWD, payPwd.getPayPwd())){
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    UserInfo userInfo = userInfoService.findUserById(String.valueOf(getUserId()));
    if (!StringUtils.isEmpty(payPwd.getLoginPwd()) && !EncryptUtil.encrypt(payPwd.getLoginPwd()).equals(userInfo.getPassword())) {
      return writeObj(ErrorMsgEnum.USER_LOGIN_PWD_ERROR);
    }
    if (!payPwd.getPayPwd().equals(payPwd.getConfirmPwd())) {
      return writeObj(ErrorMsgEnum.USER_PWD_NOT_SAME);
    }
    userInfo.setPayPwd(EncryptUtil.encrypt(payPwd.getPayPwd()));
    userInfo.setPayPwd(EncryptUtil.encrypt(payPwd.getPayPwd()));
    int result = userInfoService.updateUser(userInfo);
    if (result > 0) {
      return writeObj(null);
    }
    return writeObj(ErrorMsgEnum.USER_SET_PAYPWD_ERROR);
  }

  @ApiOperation(value = "发送二次验证短信验证码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/send-second-valid-sms", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse sendSecondValidSmsCode() {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    Map<String, Object> param = new HashMap<>();
    String random = RandNumUtil.getRandNum(6);
    param.put("token", random);
    messageService.sendMessage(userInfo.getUsername(), userInfo.getRegionCode(), userInfo.getMobile(), param, MessageEnum.SMS_LOGIN_VERFIY, getLocal());
    redisUtil.set(Constants.REDIS_SECOND_VALID_SMS_CODE_KEY + userInfo.getMobile(), random, Constants.REDIS_SECOND_VALID_SMS_CODE_KEY_EXPIRE);
    return writeObj(null);
  }

  @ApiOperation(value = "检测用户是否可提现", notes = "{\"data\": {\"twoValid\": 0/1 未开启/已开启,\"isAuth\": 0/1/2/3 未认证/审核中/已认证/认证失败,\"payPwd\": 0/1/2 未设置/24小时内未修改过/ 24小时内修改过},\n\"error\": null,\"success\": true}",httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/check-withdraw", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse checkWithdraw() {
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    Map<String, Object> result = new HashMap<>();
    result.put("isAuth", userInfo.getIsAuth());
    result.put("twoValid", userInfo.getTwoValid());
    String payPwd = redisUtil.get(Constants.REDIS_USER_CHANGE_PAY_PWD_KEY + getUserId());
    result.put("payPwd", StringUtils.isEmpty(userInfo.getPayPwd()) ? 0 : (StringUtils.isEmpty(payPwd) ? 1 : 2));
    return writeObj(result);
  }

  @ApiOperation(value = "检测用户是否可提现(供后端服务调用，前端勿用)", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Override
  public AjaxResponse checkWithdraw(Long userId) {
    UserInfo userInfo = userInfoService.findUserById(String.valueOf(userId));
    if (null == userInfo) {
      return writeObj(ErrorMsgEnum.USER_NOT_EXIST);
    }
    if (StringUtils.isEmpty(userInfo.getPayPwd())) {
      return writeObj(ErrorMsgEnum.USER_NOT_SET_PAY_PWD);
    }
    if (StringUtils.isEmpty(userInfo.getTwoValid()) || userInfo.getTwoValid().equals(StatusEnum.INVALID.getStatusCode())) {
      return writeObj(ErrorMsgEnum.USER_NOT_OPEN_SECOND_VALID);
    }
    if (StringUtils.isEmpty(userInfo.getIsAuth()) || !userInfo.getIsAuth().equals(AuthStatusEnum.AUTH_SUCCESS.getStatusCode())) {
      return writeObj(ErrorMsgEnum.USER_AUTH_FAILURE);
    }
    String result = redisUtil.get(Constants.REDIS_USER_CHANGE_PAY_PWD_KEY + userId);
    if (!StringUtils.isEmpty(result)) {
      return writeObj(ErrorMsgEnum.USER_ALREADY_CAHNGE_PAY_PWD);
    }
    return writeObj(null);
  }

  @Override
  public Map<String, Object> findBuserByUserId(Long userId) {
    return userInfoService.findUser(String.valueOf(userId));
  }

  @Override
  public String getUserRegion(String userId) {
    UserRegInfo userRegInfo = new UserRegInfo();
    userRegInfo.setUserId(Long.parseLong(userId));
    return userRegInfoMapper.selectOne(userRegInfo).getRemark();
  }

  @ApiOperation(value = "获取用户消息列表",notes = "{\"data\": {\"no_read\": 未读消息数量,\"data\": {\"pageNum\": 1,\"pageSize\": 1,\"size\": 1,\"orderBy\": null,\"startRow\": 1,\"endRow\": 1,\"total\": 2,\n" +
    "      \"pages\": 2,\"list\": [{\"is_read\": 是否已读（0/1 未读/已读）,\"createtime\": 发送时间,\"user_id\": 用户id,\"is_del\": 是否删除(0/1 未删除/已删除),\"id\": 1,\"type\": 消息类型,\"title\": \"消息标题\",\"content\": \"消息内容\"," +
    "       \"is_top\": 是否置顶,\"username\": \"用户名\",\"status\": 0}],\"firstPage\": 1,\"prePage\": 0,\"nextPage\": 2,\"lastPage\": 2,\"isFirstPage\": true,\"isLastPage\": false,\"hasPreviousPage\": false,\"hasNextPage\": true,\n" +
    "      \"navigatePages\": 8,\"navigatepageNums\": [1,2]}},\"error\": null,\"success\": true}",httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/news", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse findUserNews(@RequestParam("pageNo") String pageNo, @RequestParam("pageSize") String pageSize) {
    Map<String,Object> map = new HashMap<>();
    map.put("data", userInfoService.findUserNews(getUserId(), pageNo, pageSize));
    map.put("no_read", userInfoService.countNoRead(getUserId()));
    return writeObj(map);
  }

  @ApiOperation(value = "清空用户消息列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/news/clear", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse clearUserNews() {
    userInfoService.clearUserNews(getUserId());
    return writeObj(null);
  }

  @ApiOperation(value = "全部已读用户消息列表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/news/read-all", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse readAllUserNews() {
    userInfoService.readAllNews(getUserId());
    return writeObj(null);
  }
  @ApiOperation(value = "已读消息通知", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/news/read", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse readUserNews(@RequestParam(value = "id") String id) {
    userInfoService.readNews(getUserId(),id);
    return writeObj(null);
  }

  @ApiOperation(value = "登出", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse logout(){
    try {
      redisUtil.delKey(getToken());
      request.getSession().removeAttribute("user");
    } catch (Exception e){
      logger.error(e.getMessage());
    }
    return writeObj(null);
  }

}
