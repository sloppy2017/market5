package com.c2b.coin.gateway;

import com.google.common.base.Strings;
import feign.RequestInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;

@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
public class GatewayApplication {

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
      if (!Strings.isNullOrEmpty(sessionId)) {
        requestTemplate.header("Cookie", "SESSION=" + sessionId);
      }
    };
  }


  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }
}
