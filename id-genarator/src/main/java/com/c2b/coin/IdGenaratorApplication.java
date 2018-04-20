package com.c2b.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringCloudApplication
@EnableHystrix
@EnableAutoConfiguration
@RefreshScope
@EnableEurekaClient
@EnableFeignClients
public class IdGenaratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdGenaratorApplication.class, args);
	}
}
