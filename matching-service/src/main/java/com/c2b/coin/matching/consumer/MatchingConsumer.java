package com.c2b.coin.matching.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.c2b.coin.common.Constants;
import com.c2b.coin.matching.vo.queue.ExchangeVO;

/**
 * 撮合消费者
 * @author DingYi
 * @version 1.0.0
 * @since 2018年4月18日 下午7:04:11
 */
@Component
public class MatchingConsumer {

	/**
	 *收集消息，排序后放入队列。
	 */
	@JmsListener(destination = Constants.CONSIGNATION_SUCCESS_QUEUE_DESTINATION)
	public void collectConsignation(ExchangeVO exchangeVo) {
		//收集消息
		//插入数据库
		//整理信息后  放入买卖队列
	}
	
	/**
	 * 消费撮合成功失败的信息，并对处理相关逻辑业务。
	 */
	@JmsListener(destination = "")
	public void consumerMatch() {
		
	}
}
