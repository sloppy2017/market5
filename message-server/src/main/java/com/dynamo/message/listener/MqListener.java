package com.dynamo.message.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import javax.jms.Message;
import javax.jms.Session;
import com.alibaba.fastjson.JSONObject;
import com.dynamo.message.service.MessageService;
import com.dynamo.message.vo.MessageVo;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class MqListener extends MessageListenerAdapter {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MessageService messageService;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;

	// @JmsListener(destination = "coin.message.queue", concurrency = "10-20")
	// public void sendEmail(String text) {
	// taskExecutor.execute(new Runnable() {
	// @Override
	// public void run() {
	// logger.info(text);
	// MessageVo vo = JSONObject.parseObject(text, MessageVo.class);
	// messageService.sendMessage(vo);
	// }
	// });
	// }

	@Override
	@JmsListener(destination = "coin.message.queue", concurrency = "10-20")
	public void onMessage(Message message, Session session) {
		try {
			TextMessage textMessage = (TextMessage) message;
			logger.info(textMessage.getText());
			MessageVo vo = JSONObject.parseObject(textMessage.getText(), MessageVo.class);
			messageService.sendMessage(vo);
		} catch (Exception e) {
			logger.error(e.getMessage() + " message:{}",message.toString());
		}
	}
}
