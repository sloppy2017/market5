package com.c2b.coin.market;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@SpringCloudApplication
@EnableHystrix
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.c2b.coin"})
@RefreshScope
public class MarketApplication {
  public static void main(String[] args) {
    SpringApplication.run(MarketApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate;
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setReadTimeout(30000);
    requestFactory.setConnectTimeout(30000);

    // 添加转换器
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    messageConverters.add(new FormHttpMessageConverter());
    messageConverters.add(new MappingJackson2HttpMessageConverter());

    restTemplate = new RestTemplate(messageConverters);
    restTemplate.setRequestFactory(requestFactory);
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    return restTemplate;
  }
}
