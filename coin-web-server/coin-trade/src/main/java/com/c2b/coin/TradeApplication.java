package com.c2b.coin;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.c2b.coin.common.Constants;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.jms.Queue;

/**
 *
 * @author Anne
 *
 */
@SpringCloudApplication
@EnableHystrix
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.c2b.coin" })
@RefreshScope
@EnableEurekaClient
@EnableFeignClients
@EnableTransactionManagement
public class TradeApplication {

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate;
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setReadTimeout(30000);
    requestFactory.setConnectTimeout(30000);

    // 添加转换器
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(new StringHttpMessageConverter(Charset
        .forName("UTF-8")));
    messageConverters.add(new FormHttpMessageConverter());
    messageConverters.add(new MappingJackson2HttpMessageConverter());

    restTemplate = new RestTemplate(messageConverters);
    restTemplate.setRequestFactory(requestFactory);
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    return restTemplate;
  }
  @Bean
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setKeepAliveSeconds(60);
    threadPoolTaskExecutor.setMaxPoolSize(50);
    threadPoolTaskExecutor.setCorePoolSize(20);
    threadPoolTaskExecutor.setQueueCapacity(100);
    return threadPoolTaskExecutor;
  }
  @Bean
  public Queue consignationQueue() {
    return new ActiveMQQueue(Constants.CONSIGNATION_SUCCESS_QUEUE_DESTINATION);
  }
  public static void main(String[] args) {
    SpringApplication.run(TradeApplication.class, args);
  }
}
