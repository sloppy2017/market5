package com.c2b.coin.matching.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.Constants;
import com.c2b.coin.matching.constant.EnumConsignedType;
import com.c2b.coin.matching.constant.EnumTradeType;
import com.c2b.coin.matching.match.Matcher;
import com.c2b.coin.matching.vo.queue.ExchangeVO;

/**
 * 撮合消费者
 * @author DingYi
 * @version 1.0.0
 * @since 2018年4月18日 下午7:04:11
 */
@Component
public class MatchingConsumer {

	@Autowired
	private Matcher matcher;
	
	/**
	 *收集消息，进行撮合
	 */
	@JmsListener(destination = Constants.CONSIGNATION_SUCCESS_QUEUE_DESTINATION)
	public void collectConsignation(String exchangeVoMsg) {
		//收集消息
		ExchangeVO exchangeVo = (ExchangeVO) JSONObject.parse(exchangeVoMsg);
		//撮合交易
		matcher.match(exchangeVo.getCurrency(), exchangeVo.getMoney(), exchangeVo.getCount(), exchangeVo.getSeq(), EnumTradeType.getEnumTradeType(exchangeVo.getType()), EnumConsignedType.getEnumConsignedType(exchangeVo.getGenre()));
	}
	
	/**
	 * 消费撮合成功失败的信息，并对处理相关逻辑业务。
	 */
	@JmsListener(destination = "")
	public void consumerMatch() {
		
	}
}
