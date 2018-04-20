package com.c2b.coin.user.rest;


import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.user.entity.UserInfo;
import com.c2b.coin.user.service.UserAuthService;
import com.c2b.coin.user.service.UserInfoService;
import com.c2b.coin.user.vo.AuthVo;
import com.c2b.coin.web.common.BaseRest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class UserAuthRest extends BaseRest {

  @Autowired
  private UserInfoService userInfoService;

  @Autowired
  private UserAuthService userAuthService;

  @ApiOperation(value = "提交身份审核", httpMethod = "POST")
  @RequestMapping(value = "/identityAuth", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse identityAuth(@RequestBody AuthVo authVo) {
    if (matchHtml(authVo.getCardId(), false) || matchHtml(authVo.getFirstName(), false) || matchHtml(authVo.getLastName(), false)
      || matchHtml(authVo.getImgFrontUrl(), false) || matchHtml(authVo.getImgBehindURL(), false) || matchHtml(authVo.getImgHandheldUrl(), true))
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    logger.debug("ImgFrontUrl::::::"+authVo.getImgFrontUrl());
    UserInfo userInfo = userInfoService.findUserById(getUserId());
    boolean success = this.userAuthService.saveUserAuth(authVo, userInfo);
    if (success) {
      if (authVo.getRegionType() == 0) {
        try {
          this.userAuthService.runcheck(authVo, userInfo.getId(), userInfo.getUsername(), getLocal());
        } catch (Exception e) {
          e.printStackTrace();
          return writeObj(ErrorMsgEnum.USER_AUTH_FAILURE);
        }
      }
      return writeObj("Identity Auth success");
    } else {
      return writeObj(ErrorMsgEnum.USER_AUTH_FAILURE);
    }
  }

  @ApiOperation(value = "获取身份审核信息", notes = "{\"data\": {\"cardId\": \"证件id\",\"statusName\": \"审核状态\",\"name\": \"证件姓名\",\"typeName\": \"证件类型\",\n" +
    "    \"type\": 证件类型ID,\"status\": 审核状态(0/1/2/3 未认证/审核中/审核成功/审核失败)},\"error\": null,\"success\": true}", httpMethod = "POST")
  @RequestMapping(value = "/identityInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse identityInfo() {

    UserInfo userInfo = userInfoService.findUserById(getUserId());
    List<HashMap> mapList = null;
    if (userInfo.getId() == null) {
      return writeObj(ErrorMsgEnum.USER_NOT_EXIST);
    }
    mapList = this.userAuthService.getIdentityAuthInfo(userInfo.getId());

    if (mapList == null || mapList.size() == 0) {
      return writeObj(ErrorMsgEnum.USER_AUTH_FAILURE);
    }

    Integer status = (Integer) mapList.get(0).get("auth_status");
    String name = ((String) mapList.get(0).get("first_name")).concat("**");
//     + mapList.get(0).get("last_name")
    Integer type = (Integer) mapList.get(0).get("card_type");
    String cardId = ((String) mapList.get(0).get("card_id")).replaceAll("(\\w{2}).+(\\w{2})", "$1****$2");

    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("status", status);
    map.put("statusName", status == Constants.User_AUTH_NO ? "未审核" : status == Constants.User_AUTH_CHECKING ?
      "审核中" : status == Constants.User_AUTH_SUCCESS ? "审核成功" : status == Constants.User_AUTH_FAIL ? "审核失败" : "其他");
    map.put("name", name);
    map.put("type", type);
    map.put("typeName", type == 1 ? "身份证" : type == 2 ? "护照" : type == 3 ? "驾驶证" : "其他");
    map.put("cardId", cardId);

    String json = JSONObject.toJSONString(map);
    JSONObject jsonObject = JSONObject.parseObject(json);

    return AjaxResponse.success(jsonObject);
  }

  private boolean matchHtml(String str, boolean isAbleNull) {
    if (StringUtils.isEmpty(str)){
      return !isAbleNull;
    }
    Pattern pattern = Pattern.compile(Constants.REGXP_FOR_HTML);
    Matcher matcher = pattern.matcher(str);
    return matcher.find();
  }
}
