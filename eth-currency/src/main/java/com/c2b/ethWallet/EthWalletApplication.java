package com.c2b.ethWallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

/**  
 * 类说明   
 *  
 * @author Anne  
 * @date 2017年10月27日 
 */
@SpringCloudApplication
@EnableHystrix
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.c2b")
@RefreshScope
@EnableEurekaClient
@EnableFeignClients
public class EthWalletApplication {

  public static void main(String[] args) {
    SpringApplication.run(EthWalletApplication.class, args);
  }

}
