package com.c2b;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.c2b.wallet.util.LiteWalletAsync;

@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@EnableConfigurationProperties
//@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients
public class LiteWalletApplication {
    
    static Logger logger = LoggerFactory.getLogger(LiteWalletApplication.class);
    
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(LiteWalletApplication.class, args);
        LiteWalletAsync walletAsync = context.getBean(LiteWalletAsync.class);
        walletAsync.run();
        logger.info("-----LiteWalletApplication-----start----success------------");
    }
    
    private CorsConfiguration buildConfig() {  
        CorsConfiguration corsConfiguration = new CorsConfiguration();  
        corsConfiguration.addAllowedOrigin("*");  
        corsConfiguration.addAllowedHeader("*");  
        corsConfiguration.addAllowedMethod("*");  
        return corsConfiguration;  
    }  
      
    /** 
     * 跨域过滤器 
     * @return 
     */  
    @Bean  
    public CorsFilter corsFilter() {  
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();  
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);  
    } 
    
    /*@Bean
    public HttpSessionStrategy httpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }*/
    
}


