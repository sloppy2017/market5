package com.c2b.coin.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.servlet.MultipartConfigElement;

@SpringCloudApplication
@EnableHystrix
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.c2b.coin"})
@RefreshScope
public class FileApplication {
  public static void main(String[] args) {
    SpringApplication.run(FileApplication.class, args);
  }

//  @Bean
//  public MultipartConfigElement multipartConfigElement() {
//    MultipartConfigFactory factory = new MultipartConfigFactory();
//    //// 设置文件大小限制 ,超了，页面会抛出异常信息，这时候就需要进行异常信息的处理了;
//    factory.setMaxFileSize("128KB"); //KB,MB
//    /// 设置总上传数据总大小
//    factory.setMaxRequestSize("256KB");
//    return factory.createMultipartConfig();
//  }
}
