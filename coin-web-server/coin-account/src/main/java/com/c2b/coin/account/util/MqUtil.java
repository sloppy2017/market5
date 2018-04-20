package com.c2b.coin.account.util;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
public class MqUtil {
  @Autowired
  private JmsMessagingTemplate jmsMessagingTemplate;

  public void sendMessageQueue(Queue queue,Object obj){
    this.jmsMessagingTemplate.convertAndSend(queue, JSON.toJSONString(obj));
  }
}
