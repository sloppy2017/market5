package com.c2b.coin.user.rest;

import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.RegexUtil;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.user.service.IUserAccessService;
import com.c2b.coin.web.common.BaseRest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("用户AccessKey服务")
@RestController
@RequestMapping("/accesskey")
public class UserAccessRest extends BaseRest {

  @Autowired
  private IUserAccessService iUserAccessService;

  @ApiOperation(value = "创建AccessKey")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "userId", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "allowIp", value = "allowIp", paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "remark", value = "remark", paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public AjaxResponse create(@RequestParam(name = "userId", required = false, defaultValue = "") String userId,
                             @RequestParam(name = "allowIp", required = false, defaultValue = "") String allowIp,
                             @RequestParam(name = "remark", required = false, defaultValue = "") String remark) {
    if (!RegexUtil.isId(userId)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (!RegexUtil.ipsRegex(allowIp)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (remark.length() > 255) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    return writeObj(iUserAccessService.create(Long.parseLong(userId), allowIp, remark));
  }

  @ApiOperation(value = "查询用户的AccessKey列表")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "userId", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/list", method = RequestMethod.POST)
  public AjaxResponse create(@RequestParam(name = "userId", required = false, defaultValue = "") String userId) {
    if (!RegexUtil.isId(userId)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    return writeObj(iUserAccessService.findByUserId(Long.parseLong(userId)));
  }

  @ApiOperation(value = "创建AccessKey")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "userId", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "allowIp", value = "allowIp", paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "remark", value = "remark", paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public AjaxResponse update(@RequestParam(name = "userId", required = false, defaultValue = "") String userId,
                             @RequestParam(name = "id", required = false, defaultValue = "") String id,
                             @RequestParam(name = "allowIp", required = false, defaultValue = "") String allowIp,
                             @RequestParam(name = "remark", required = false, defaultValue = "") String remark) {
    if (!RegexUtil.isId(userId)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (!RegexUtil.isId(id)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (!RegexUtil.ipsRegex(allowIp)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (remark.length() > 255) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    iUserAccessService.update(Long.parseLong(userId), Integer.parseInt(id), allowIp, remark);
    return writeObj(null);
  }

  @ApiOperation(value = "修改AccessKey")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "userId", value = "userId", required = true, paramType = "query", dataType = "string"),
    @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query", dataType = "string")
  })
  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  public AjaxResponse delete(@RequestParam(name = "userId", required = false, defaultValue = "") String userId,
                             @RequestParam(name = "id", required = false, defaultValue = "") String id) {
    if (!RegexUtil.isId(userId)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (!RegexUtil.isId(id)) {
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    iUserAccessService.delete(Long.parseLong(userId), Integer.parseInt(id));
    return writeObj(null);
  }

}
