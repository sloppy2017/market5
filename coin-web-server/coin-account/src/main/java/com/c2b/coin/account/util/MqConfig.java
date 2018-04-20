package com.c2b.coin.account.util;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

  @Bean
  public Queue messageQueue() {
    return new ActiveMQQueue("coin.message.queue");
  }
  
  @Bean
  public Queue assetLogQueue() {
	  return new ActiveMQQueue("coin.assetlog.queue");
  }
  
}
