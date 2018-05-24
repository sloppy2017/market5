package com.c2b.coin.api.controller.v1.trade;

import com.c2b.coin.api.annotation.Sign;
import com.c2b.coin.web.common.rest.RestBaseController;
import com.c2b.coin.web.common.rest.bean.RestResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/account")
@Api(value = "/v1/account", description = "用户资产")
public class ConsignationController extends RestBaseController {

    @Sign
    @RequestMapping(value = "get", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "获取用户资产")
    public RestResponseBean get(@ApiParam(required = true, name = "orderId", value = "订单ID") @RequestParam(required = false, defaultValue = "") String orderId) {
        return onSuccess();
    }

}
