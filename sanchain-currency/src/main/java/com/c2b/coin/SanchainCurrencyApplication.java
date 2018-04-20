package com.c2b.coin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringCloudApplication
@EnableHystrix
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.c2b.coin"})
@RefreshScope
@EnableEurekaClient
@EnableFeignClients
@EnableScheduling
public class SanchainCurrencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SanchainCurrencyApplication.class, args);
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
}
