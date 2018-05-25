package com.c2b.coin.user.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.*;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.common.enumeration.StatusEnum;
import com.c2b.coin.common.enumeration.UserStatusEnum;
import com.c2b.coin.user.annotation.CheckUserStatus;
import com.c2b.coin.user.annotation.CheckVerfiyCode;
import com.c2b.coin.user.aspect.UserStatus;
import com.c2b.coin.user.dto.*;
import com.c2b.coin.user.entity.UserInfo;
import com.c2b.coin.user.service.MessageService;
import com.c2b.coin.user.service.UserIdService;
import com.c2b.coin.user.service.UserInfoService;
import com.c2b.coin.user.util.Operation;
import com.c2b.coin.web.common.BaseRest;
import com.c2b.coin.web.common.IPUtils;
import com.c2b.coin.cache.redis.RedisUtil;
import com.c2b.coin.web.common.UserAgent;
import com.c2b.coin.web.common.annotation.SysLog;
import cz.mallat.uasparser.UserAgentInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/nologin")
public class NoLoginRest extends BaseRest {
  @Autowired
  private UserInfoService userInfoService;
  @Autowired
  RedisUtil redisUtil;
  @Autowired
  MessageService messageService;
  @Autowired
  UserIdService userIdService;
  @Value("${coin.login.maxErrorTimes}")
  private int loginMaxErrorTimes;


  @ApiOperation(value = "检测用户是否存在", httpMethod = "POST", notes = "")
  @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataType = "string")
  @RequestMapping(value = "/check-user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse checkUser(@RequestParam(required = true) String username) throws UnsupportedEncodingException {
    if (StringUtils.isEmpty(username) || (!Pattern.matches(Constants.REGEX_EMAIL, username) && !Pattern.matches(Constants.REGEX_MOBILE, username))) {
      writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    List<UserInfo> userInfoList = userInfoService.findUserByUsernameOrEmailOrMobile(username);
    if (userInfoList.size() != 0) {
      return writeObj(ErrorMsgEnum.USER_EXITS);
    }
    return writeObj(null);
  }

  @ApiOperation(value = "检测用户是否存在", httpMethod = "POST", notes = "")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "validate", value = "验证码", required = true, paramType = "query", dataType = "string")
  })
  @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataType = "string")
  @RequestMapping(value = "/v2/check-user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @CheckVerfiyCode(value = "#validate")
  public AjaxResponse checkUser(@RequestParam(required = true) String username, @RequestParam(required = true) String validate) {
    if (StringUtils.isEmpty(username) || (!Pattern.matches(Constants.REGEX_EMAIL, username) && !Pattern.matches(Constants.REGEX_MOBILE, username))) {
      writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    List<UserInfo> userInfoList = userInfoService.findUserByUsernameOrEmailOrMobile(username);
    if (userInfoList.size() != 0) {
      return writeObj(ErrorMsgEnum.USER_EXITS);
    }
    return writeObj(null);
  }

  @ApiOperation(value = "检测用户是否激活", httpMethod = "POST", notes = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiImplicitParam(name = "username", value = "用户名", required = true, paramType = "query", dataType = "string")
  @RequestMapping(value = "/check-active", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse checkUserActive(@RequestParam(required = true) String username) {
    List<UserInfo> userInfoList = userInfoService.findUserByUsernameOrEmailOrMobile(username);
    if (userInfoList.size() == 0) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    UserInfo userInfo = userInfoList.get(0);
    if (userInfo.getUserStatus() != UserStatusEnum.NORMAL.getStatusCode()) {
      return writeObj(ErrorMsgEnum.USER_NOT_ACTIVE);
    }
    return AjaxResponse.success(null);
  }

  @ApiOperation(value = "登录接口", httpMethod = "POST", notes = "返回值字段含义：{\"data\": {\"twoValid\": 0/1 未开启/已开启,\"googleStatus\": 0/1 未绑定/已绑定,\"isAuth\": 0/1/2 未认证/认证中/已认证,\"loginTime\": 1510800875275,\"lastVerfiyType\": \"google\",\n" +
    "    \"mobileStatus\": 0/1 未绑定/已绑定,\"id\": 用户id,\"terminal\": \"登录终端\",\"username\": \"用户名\",\"token\": \"dd0ec808433ed0da0c82c6595a41de65\"},\"error\": null,\"success\": true}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @SysLog(value = Operation.LOGIN, username = "#loginDto.username")
  @CheckVerfiyCode(value = "#loginDto.validate")
  @CheckUserStatus(value = {UserStatus.USER_ACTIVE, UserStatus.USER_FREEZE, UserStatus.USER_LOGIN_ERROR_TIMES}, username = "#loginDto.username")
  public AjaxResponse login(@RequestBody LoginDto loginDto) throws UnsupportedEncodingException {
    List<UserInfo> userInfoList = userInfoService.findUserByUsernameOrEmailOrMobile(loginDto.getUsername());
    UserInfo userInfo = userInfoList.get(0);
    if (!userInfo.getPassword().equals(EncryptUtil.encrypt(loginDto.getPassword()))) {
      Long times = redisUtil.incr(Constants.REDIS_USER_LOGIN_ERROR_TIMES_KEY + userInfo.getUsername());
      redisUtil.expire(Constants.REDIS_USER_LOGIN_ERROR_TIMES_KEY + userInfo.getUsername(), Constants.REDIS_USER_LOGIN_ERROR_TIMES_KEY_EXPIRE);
      return writeFailureObjWithParam(ErrorMsgEnum.USER_LOGIN_ERROR, loginMaxErrorTimes - times.intValue());
    }
    redisUtil.delKey(Constants.REDIS_USER_LOGIN_ERROR_TIMES_KEY + userInfo.getUsername());
    Map<String,Object> map = createLoginMap(userInfo, loginDto.getUsername());
    return writeObj(map);
  }

  @ApiOperation(value = "激活用户", notes = "", httpMethod = "POST")
  @RequestMapping(value = "/active-user", method = RequestMethod.POST)
  @SysLog(value = Operation.ACTIVE_USER)
  public String activeUser(@RequestParam String token) {
    String redisresult = redisUtil.get(Constants.REDIS_ACTIVE_EMAIL_KEY + token);
    if (StringUtils.isEmpty(redisresult)) {
      return writeJson(ErrorMsgEnum.USER_ACTIVE_EMAIL_EXPIRE);
    }
    String userId = JSONObject.parseObject(redisresult).getString("userId");
    int result = userInfoService.activeUser(userId);
    if (result < 1) {
      return writeJson(ErrorMsgEnum.USER_ACTIVE_FAILURE);
    }
    redisUtil.delKey(Constants.REDIS_ACTIVE_EMAIL_KEY + token);
    return writeJson(null);
  }

  @ApiOperation(value = "发送注册短信", notes = "", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "mobile", value = "mobile", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "regionCode", value = "regionCode", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/send-mobile-regcode", method = RequestMethod.POST)
  public AjaxResponse sendMobileRegCode(@RequestParam(name = "regionCode", required = true) String regionCode, @RequestParam(name = "mobile", required = true) String mobile) {
    if (!Pattern.matches(Constants.REGEX_MOBILE, mobile) || !Pattern.matches(Constants.REGEX_REGION, regionCode))
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    List<UserInfo> userInfoList = userInfoService.findUserInfoByMobile(mobile);
    if (null != userInfoList && userInfoList.size() != 0) {
      return writeObj(ErrorMsgEnum.MOBILE_EXISTS);
    }
    messageService.sendRegSmsAndCache(regionCode, mobile, getLocal());
    return AjaxResponse.success(null);
  }

  @ApiOperation(value = "注册", notes = "", httpMethod = "POST")
  @RequestMapping(value = "/register", method = RequestMethod.POST)
  @CheckVerfiyCode(value = "#regDto.validate")
  public AjaxResponse registerUser(@RequestBody RegDto regDto) throws UnsupportedEncodingException {
    if (!RegDtoValid.validRegParam(regDto)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    List<UserInfo> userInfos = userInfoService.findUserByUsernameOrEmailOrMobile(regDto.getUsername());
    if (userInfos.size() != 0) {
      return writeObj(ErrorMsgEnum.USER_EXITS);
    }
    String regionCode = "";
    if (Pattern.matches(Constants.REGEX_MOBILE, regDto.getUsername())) {
      String redisResult = redisUtil.get(Constants.REDIS_REG_SMS_CODE_KEY + regDto.getUsername());
      if (StringUtils.isEmpty(redisResult) || !regDto.getSmsCode().equals(JSONObject.parseObject(redisResult).getString("token"))) {
        return writeObj(ErrorMsgEnum.USER_VALIDATE_CODE_ERROR);
      }
      regionCode = JSONObject.parseObject(redisResult).getString("regionCode");
    }
    boolean flag = false;
    if (StringUtils.isEmpty(regDto.getPassword())) {
      regDto.setPassword(RandNumUtil.getRandNum(8));
      flag = true;
    }
    String ip = IPUtils.getIpAddr(request);
    UserInfo userInfo = userInfoService.saveUser(userIdService.next(), Pattern.matches(Constants.REGEX_MOBILE, regDto.getUsername()) ? regDto.getUsername() : "", Pattern.matches(Constants.REGEX_EMAIL, regDto.getUsername()) ? regDto.getUsername() : "",
      regDto.getPassword(), regDto.getInviteCode(), getDeviceId(), ip, getChannel(), getAppVersion(), getActivityId(), "", UserAgent.userAgent(request),regionCode, getLocal());
    Map<String,Object> map = null;
    if (Pattern.matches(Constants.REGEX_EMAIL, regDto.getUsername())) {
      messageService.sendActiveEmail(userInfo, getLocal());
    } else {
      if (flag) {
        Map<String, Object> param = new HashMap<>();
        param.put("token", regDto.getPassword());
        messageService.sendQuickRegSuccessSms(userInfo, regDto.getPassword(), getLocal());
      }
      map = createLoginMap(userInfo, regDto.getUsername());
    }
    return writeObj(map);
  }

  @ApiOperation(value = "发送激活邮件", notes = "", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "toUser", value = "toUser", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/send-active-email", method = RequestMethod.POST)
  public AjaxResponse sendActiveEmail(@RequestParam String username) {
    List<UserInfo> list = userInfoService.findUserByUsernameOrEmailOrMobile(username);
    if (list.size() == 0) {
      return writeObj(null);
    }
    UserInfo userInfo = list.get(0);
    if (userInfo.getUserStatus().equals(UserStatusEnum.NOT_ACTTIVE.getStatusCode())) {
      messageService.sendActiveEmail(userInfo, getLocal());
      return writeObj(null);
    } else {
      return writeObj(ErrorMsgEnum.USER_ALREADY_ACTIVE);
    }

  }

  @ApiOperation(value = "再次发送找回密码邮件", notes = "不需要验证码", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "toUser", value = "toUser", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/resend-forgetpwd-email", method = RequestMethod.POST)
  @CheckUserStatus(value = {UserStatus.USER_ACTIVE, UserStatus.USER_FREEZE}, username = "#username")
  public AjaxResponse sendRestPwdEmail(@RequestParam(value = "username") String username) {
    List<UserInfo> list = userInfoService.findUserByUsernameOrEmailOrMobile(username);
    if (list.size() == 0) {
      return writeObj(null);
    }
    messageService.sendResetPwdEmail(list.get(0).getEmail(), getLocal());
    return writeObj(null);
  }

  @ApiOperation(value = "再次发送找回密码邮件", notes = "不需要验证码", httpMethod = "POST")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "regionCode", value = "regionCode", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "mobile", value = "mobile", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/resend-forgetpwd-sms", method = RequestMethod.POST)
  @CheckUserStatus(value = {UserStatus.USER_ACTIVE, UserStatus.USER_FREEZE}, username = "#mobile")
  public AjaxResponse sendRestPwdSms(@RequestParam(value = "regionCode") String regionCode, @RequestParam(value = "mobile") String mobile) {
    List<UserInfo> list = userInfoService.findUserByUsernameOrEmailOrMobile(mobile);
    if (list.size() == 0) {
      return writeObj(null);
    }
    messageService.sendResetPwdSms(regionCode, mobile, getLocal());
    return writeObj(null);
  }

  @ApiOperation(value = "发送找回密码信息", notes = "", httpMethod = "POST")
  @RequestMapping(value = "/forgetpwd", method = RequestMethod.POST)
  @CheckVerfiyCode(value = "#forgetPwdDto.validate")
  @CheckUserStatus(value = {UserStatus.USER_ACTIVE, UserStatus.USER_FREEZE}, username = "#forgetPwdDto.username")
  public AjaxResponse forgetPwd(@RequestBody ForgetPwdDto forgetPwdDto) throws UnsupportedEncodingException {
    if (Pattern.matches(Constants.REGEX_EMAIL, forgetPwdDto.getUsername())) {
      messageService.sendResetPwdEmail(forgetPwdDto.getUsername(), getLocal());
    } else {
      messageService.sendResetPwdSms(forgetPwdDto.getRegiconCode(), forgetPwdDto.getUsername(), getLocal());
    }
    return writeObj(null);
  }

  @ApiOperation(value = "短息找回密码", notes = "", httpMethod = "POST")
  @RequestMapping(value = "/sms-forgetpwd", method = RequestMethod.POST)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "smsCode", value = "短信验证码", required = true, paramType = "query", dataType = "string")
  })
  @CheckUserStatus(value = {UserStatus.USER_ACTIVE, UserStatus.USER_FREEZE}, username = "#mobile")
  public AjaxResponse smsResetPwd(@RequestParam(required = true) String mobile, @RequestParam(required = true) String smsCode) {
    return writeObj(userInfoService.smsResetPwd(mobile, smsCode, getLocal()));
  }

  @ApiOperation(value = "重置密码", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @RequestMapping(value = "/reset-pwd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse resetPwd(@RequestBody ResetPwdDto resetPwdDto) {
    String token = resetPwdDto.getToken();
    String result = redisUtil.get(Constants.REDIS_RESET_PASSWORD_KEY + token);
    if (StringUtils.isEmpty(result)) {
      writeObj(ErrorMsgEnum.USER_RESET_PWD_EMAIL_EXPIRE);
    }
    String username = JSONObject.parseObject(result).getString("email");
    List<UserInfo> userInfos = userInfoService.findUserByUsernameOrEmailOrMobile(username);
    UserInfo userInfo = userInfos.get(0);
    userInfo.setPassword(EncryptUtil.encrypt(resetPwdDto.getPassword()));
    userInfoService.updateUser(userInfo);
    redisUtil.delKey(Constants.REDIS_RESET_PASSWORD_KEY + token);
    return writeObj(null);
  }

  /**
   * 创建登录返回map
   * @param userInfo
   * @param loginUserName
   * @return
   */
  private Map<String,Object> createLoginMap(UserInfo userInfo, String loginUserName){
    Map<String, Object> map = new HashMap<>();
    map.put("username", userInfo.getUsername());
    map.put("email", StringUtils.isEmpty(userInfo.getEmail()) ? "" : userInfo.getEmail());
    map.put("mobile", StringUtils.isEmpty(userInfo.getMobile()) ? "" : userInfo.getMobile());
    map.put("isAuth", userInfo.getIsAuth());
    map.put("twoValid", userInfo.getTwoValid());
    map.put("googleStatus", userInfo.getGoogleBindStatus());
    map.put("mobileStatus", StringUtils.isEmpty(userInfo.getMobile()) ? StatusEnum.UNBIND.getStatusCode() : StatusEnum.BIND.getStatusCode());
    UserAgentInfo userAgentInfo = UserAgent.userAgent(request);
    String token = userInfoService.loginToken(userInfo, map, userAgentInfo);
    map.put("token", token);
    map.put("lastVerfiyType", redisUtil.get(Constants.REDIS_USER_LOGIN_VERFIY_TYPE + userInfo.getUsername()));
    try {
      HttpSession session = request.getSession();
      session.setAttribute("user", JSON.toJSONString(userInfo));
      session.setAttribute("token", token);
    } catch (Exception e) {
      logger.error("======================添加session 失败");
    }
    if (Pattern.matches(Constants.REGEX_EMAIL, loginUserName)) {
      messageService.sendLoginEmail(userInfo, getLocal());
    } else {
      Map<String,Object> param = new HashMap<>();
      param.put("token", DateUtil.formateDate(DateUtil.getCurrentDate(), DateUtil.DATE_FULL_STR));
      messageService.sendMessage(userInfo.getUsername(), userInfo.getRegionCode(), userInfo.getMobile(), param, MessageEnum.SMS_LOGIN_SUCCESS, getLocal());
    }
    return map;
  }
}
