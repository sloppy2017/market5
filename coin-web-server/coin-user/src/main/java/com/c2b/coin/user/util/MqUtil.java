package com.c2b.coin.user.util;

import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component
public class MqUtil {
  @Autowired
  private JmsMessagingTemplate jmsMessagingTemplate;
  @Autowired
  private Queue messageQueue;

  public void sendMessageQueue(Object obj){
    this.jmsMessagingTemplate.convertAndSend(this.messageQueue, JSON.toJSONString(obj));
  }
}
