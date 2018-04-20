package com.dynamo.message.mq;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

@Configuration
public class MqConfig {

  @Bean
  public Queue messageQueue() {
    return new ActiveMQQueue("coin.message.queue");
  }

//  @Bean
//  public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ActiveMQConnectionFactory connectionFactory) {
//    DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
//    bean.setPubSubDomain(true);
//    bean.setConnectionFactory(connectionFactory);
//    return bean;
//  }
//  @Bean
//  public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ActiveMQConnectionFactory connectionFactory) {
//    DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
//    bean.setConnectionFactory(connectionFactory);
//    return bean;
//  }
//  @Bean
//  public JmsMessagingTemplate jmsMessagingTemplate(ActiveMQConnectionFactory connectionFactory){
//    return new JmsMessagingTemplate(connectionFactory);
//  }
}
