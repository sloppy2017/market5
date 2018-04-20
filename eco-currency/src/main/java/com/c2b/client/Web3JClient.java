package com.c2b.client;

import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
/**
 * 
* @ClassName: Web3JClient 
* @Description: TODO(获取web3j客户端) 
* @author dxm
* @date 2017年8月21日 下午5:43:11 
*
 */
@Component
public class Web3JClient {

	private static String ip = "http://139.219.97.30:8545";
    private Web3JClient(){}

    private volatile static Web3j web3j;

    public static Web3j getClient(){
        if(web3j==null){
            synchronized (Web3JClient.class){
                if(web3j==null){
                    web3j = Web3j.build(new HttpService(ip));
                }
            }
        }
        return web3j;
    }
}
