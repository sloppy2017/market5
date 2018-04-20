package com.c2b.coin.market.api;

import com.c2b.coin.common.AjaxResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


public interface MarketApi {

  @RequestMapping(value = "/nologin/realtime/price/{commodity}/{money}",method = RequestMethod.POST)
  public AjaxResponse getRealTimePrice(@PathVariable(name="commodity") String commodity, @PathVariable(name="money") String money);
  }
