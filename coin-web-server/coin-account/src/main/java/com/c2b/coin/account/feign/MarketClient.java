package com.c2b.coin.account.feign;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.c2b.coin.market.api.MarketApi;

@FeignClient("coin-market")
public interface MarketClient extends MarketApi{

}
