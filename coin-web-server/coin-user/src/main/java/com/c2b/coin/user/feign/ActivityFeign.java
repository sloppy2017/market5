package com.c2b.coin.user.feign;

import com.c2b.coin.account.api.ActivityClient;
import com.c2b.coin.common.AjaxResponse;
import org.springframework.cloud.netflix.feign.FeignClient;

import java.lang.annotation.Repeatable;
import java.math.BigDecimal;

@FeignClient("coin-account")
public interface ActivityFeign extends ActivityClient {

}
