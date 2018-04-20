package com.c2b.coin.account.feign;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.c2b.coin.user.api.UserApi;

@FeignClient("coin-user")
public interface UserClient extends UserApi{

}
