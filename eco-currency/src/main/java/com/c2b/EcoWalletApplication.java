package com.c2b;


import java.io.File;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


//import com.ecochain.task.Ethereum.EtherHotWalletTool;
import com.c2b.wallet.util.WalletAsync;

@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@EnableConfigurationProperties
//@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients
public class EcoWalletApplication {
    
    static private String wallertRootPath = "/data/ecocurrency";
    
    static Logger log = LoggerFactory.getLogger(EcoWalletApplication.class);
    
    public static NetworkParameters params = MainNetParams.get();
//    public static NetworkParameters params = TestNet3Params.get();
    
//    static String userName ="ecotest";
    
    static String userName ="eco";

    @Autowired
    private WalletAsync walletAsync;

    /*@Autowired
    private EtherHotWalletTool etherHotWalletTool;*/
    
    public static WalletAppKit kit = new WalletAppKit(params, 
            new File(wallertRootPath + File.separatorChar + userName), userName );
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EcoWalletApplication.class, args);
        log.info("EcoWalletApplication-----started--//TODO  共享session所有方法全部加上security的登陆验证");
        
//        EcoWalletService ecoWalletService = context.getBean(EcoWalletService.class);
//        EcoWalletMapper mapper = context.getBean(EcoWalletMapper.class);
//        
//        List<EcoWallet> ecoWallets = ecoWalletService.listAllEcoWallets();
//        if (ecoWallets != null ){
//            for (EcoWallet ecoWallet : ecoWallets) {
//                ecoWallet.setPrivateKey(AES.encrypt(ecoWallet.getPrivateKey().getBytes()));
//                mapper.updateByPrimaryKey(ecoWallet);
//            }
//        }
        
        EcoWalletApplication.kit.startAsync();
        EcoWalletApplication.kit.awaitRunning();

        WalletAsync walletAsync = context.getBean(WalletAsync.class);
        walletAsync.run();

        System.out.println("==========EcoWalletApplication start==="
                + "提币手续费：三界宝的0.1%每次，莱特币0.03个LTC/次。"
                + "比特币0.002个BTC/次。以太坊0.01个ETH/次。========");

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


