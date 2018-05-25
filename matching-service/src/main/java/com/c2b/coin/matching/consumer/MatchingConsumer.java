package com.c2b.coin.matching.consumer;

import java.math.BigDecimal;
import java.util.Map;

import com.coin.config.cache.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.Constants;
import com.c2b.coin.matching.constant.EnumConsignedType;
import com.c2b.coin.matching.constant.EnumTradeType;
import com.c2b.coin.matching.match.Matcher;
import com.c2b.coin.matching.vo.queue.ExchangeVO;
import org.springframework.util.StringUtils;

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

  @Autowired
  RedisUtil redisUtil;

	/**
	 *收集消息，进行撮合
	 */
	@JmsListener(destination = Constants.CONSIGNATION_SUCCESS_QUEUE_DESTINATION)
	public void collectConsignation(String exchangeVoMsg) {
		//收集消息
		Map<String,Object> map = (Map)JSONObject.parse(exchangeVoMsg);

		String type = (String)map.get("type");//值为  buy  或者  sell
		String seq = (String)map.get("seq"); // 委托单号
		if(!StringUtils.isEmpty(type)&&!StringUtils.isEmpty(seq)&&!redisUtil.isMember(type, seq)){// 判断redis 如果不是成员，可以进行撮合，如果是成员说明已经撮合过不在撮合
        redisUtil.sadd(type,seq);//存放到撮合redis中进行撮合
        //撮合交易
        matcher.match(map.get("currency").toString(), new BigDecimal(map.get("money").toString()), new BigDecimal(map.get("count").toString()) , map.get("seq").toString(), EnumTradeType.getEnumTradeType(map.get("type").toString()), EnumConsignedType.getEnumConsignedType(map.get("genre").toString()));
		}

	}

//	/**
//	 * 消费撮合成功失败的信息，并对处理相关逻辑业务。
//	 */
//	@JmsListener(destination = "")
//	public void consumerMatch() {
//
//	}
}
