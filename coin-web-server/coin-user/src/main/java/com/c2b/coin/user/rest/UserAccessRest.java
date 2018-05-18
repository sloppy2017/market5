package com.c2b.coin.user.rest;

import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.RegexUtil;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.user.service.IUserAccessService;
import com.c2b.coin.web.common.BaseRest;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Api("用户AccessKey")
@RestController
@RequestMapping("/accesskey")
public class UserAccessRest extends BaseRest {

  @Autowired
  private IUserAccessService iUserAccessService;

  @ApiOperation(value = "创建AccessKey")
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public AjaxResponse create(
    @ApiParam(name = "allowIp", value = "IP白名单,多个以逗号分隔") @RequestParam(name = "allowIp", required = false, defaultValue = "") String allowIp,
    @ApiParam(name = "remark", value = "备注") @RequestParam(name = "remark", required = false, defaultValue = "") String remark) {
    if (StringUtils.isNotEmpty(allowIp) && !RegexUtil.ipsRegex(allowIp)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (remark.length() > 255) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    return writeObj(iUserAccessService.create(Long.parseLong(getUserId()), allowIp, remark));
  }

  @ApiOperation(value = "查询用户的AccessKey列表")
  @RequestMapping(value = "/list", method = RequestMethod.POST)
  public AjaxResponse create() {
    return writeObj(iUserAccessService.findByUserId(Long.parseLong(getUserId())));
  }

  @ApiOperation(value = "修改AccessKey")
  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public AjaxResponse update(
    @ApiParam(name = "accessKeyId", value = "accessKeyId", required = true) @RequestParam(name = "accessKeyId", required = false, defaultValue = "") String accessKeyId,
    @ApiParam(name = "allowIp", value = "IP白名单,多个以逗号分隔") @RequestParam(name = "allowIp", required = false, defaultValue = "") String allowIp,
    @ApiParam(name = "remark", value = "备注") @RequestParam(name = "remark", required = false, defaultValue = "") String remark) {
    if (!accessKeyIdRegex(accessKeyId)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (!RegexUtil.ipsRegex(allowIp)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (remark.length() > 255) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    iUserAccessService.update(Long.parseLong(getUserId()), accessKeyId, allowIp, remark);
    return writeObj(null);
  }

  @ApiOperation(value = "删除AccessKey")
  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  public AjaxResponse delete(
    @ApiParam(name = "accessKeyId", value = "accessKeyId", required = true) @RequestParam(name = "accessKeyId", required = false, defaultValue = "") String accessKeyId) {
    if (!accessKeyIdRegex(accessKeyId)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    iUserAccessService.delete(Long.parseLong(getUserId()), accessKeyId);
    return writeObj(null);
  }

  private static final Pattern accessKeyIdPattern = Pattern.compile("[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}");
  public static boolean accessKeyIdRegex(String s) {
    return accessKeyIdPattern.matcher(s).matches();
  }

}
