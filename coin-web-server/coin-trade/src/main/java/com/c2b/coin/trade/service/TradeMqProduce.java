package com.c2b.coin.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.trade.vo.ExchangeVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.math.BigDecimal;

@Service
public class TradeMqProduce {

  private static Logger log = Logger.getLogger(TradeMqProduce.class);
  @Autowired
  private JmsTemplate jmsQueueTemplate;
  @Autowired // 也可以注入JmsTemplate，JmsMessagingTemplate对JmsTemplate进行了封装
  private JmsMessagingTemplate jmsMessagingTemplate;
  @Autowired
  Queue consignationQueue;
  @Autowired
  ThreadPoolTaskExecutor taskExecutor;

  public Destination destination(String queueName) {
    try {
      return jmsQueueTemplate.getConnectionFactory().createConnection().createSession(true, Session.SESSION_TRANSACTED).createQueue(queueName);
    } catch (JMSException e) {
      log.error("消息队列destination创建失败，queueName=" + queueName);
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 向指定队列发送消息
   * 发送字符串信息
   */
  public void sendMessage(Queue queue, final String msg) {
//    Destination destination = destination(queueName);
    try {
      log.info("向队列" + queue.getQueueName() + "发送了消息------------" + msg);
      jmsMessagingTemplate.convertAndSend(queue, msg);
    } catch (JMSException e) {
      e.printStackTrace();
    }

  }

  /**
   * 向默认队列发送消息
   */
  public void sendMessage(final String msg) {
    String destination = jmsQueueTemplate.getDefaultDestination().toString();
    log.info("向队列" + destination + "发送了消息------------" + msg);
    jmsQueueTemplate.send(new MessageCreator() {
      @Override
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(msg);
      }
    });
  }

  public void sendMessage(String userId, BigDecimal consignationCount, BigDecimal consignationPrice, String genre, String currency, String type, String orderNo) {
    taskExecutor.execute(new Runnable() {
      @Override
      public void run() {
        ExchangeVO exchangeVo = new ExchangeVO();
        exchangeVo.setUserId(userId.toString());
        exchangeVo.setCount(consignationCount);
        exchangeVo.setMoney(consignationPrice);
        exchangeVo.setGenre(genre);
        exchangeVo.setCurrency(currency);
        exchangeVo.setType(type);
        exchangeVo.setSeq(orderNo);
        sendMessage(consignationQueue, JSONObject.toJSONString(exchangeVo));
      }
    });
  }


}
