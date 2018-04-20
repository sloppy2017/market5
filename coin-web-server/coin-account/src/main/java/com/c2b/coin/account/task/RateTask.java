package com.c2b.coin.account.task;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.c2b.coin.common.DateUtil;

/**
 * 刷新汇率
 * @author jianghongyan
 *
 */
@Service
public class RateTask {

	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Resource(name = "redisTemplate")
	protected RedisTemplate<Object,Object> redisTemplate;

	@Value("${wallet.legalCurrencyUrl}")
	private String legalCurrencyUrl;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public final static String USDT_BTC_RATE_KEY="USDT_BTC_RATE_KEY";
	
	private final static String CNY_USD_URL="http://api.fixer.io/latest?base=USD";
	
	public final static String CNY_USDT_RATE_KEY="CNY_USDT_RATE_KEY";
	
	public final static String USDT_ETH_RATE_KEY="USDT_ETH_RATE_KEY";
	@Scheduled(fixedDelay = 1800000,initialDelay=100)
	public void taskRefreshCNYRATE() {
		logger.info("go Task 30 Minute!" + DateUtil.getCurrentTimestamp());
		try {
			refreshCNYRATE();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("REFRESH CNY RATE FAIL:"+e.getMessage());
		}
		
	}

	@Scheduled(fixedDelay = 1800000,initialDelay=100 )
	public void taskRefreshUSDTRATE() {
		logger.info("go Task 30 Minute!" + DateUtil.getCurrentTimestamp());
		try {
			refreshUSDTRATE();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("REFRESH USDT RATE FAIL:"+e.getMessage());
		}
		
	}
	
	public void refreshCNYRATE() {
		String rateUrl=CNY_USD_URL;
		String res=restTemplate.getForObject(rateUrl, String.class);
		Map map=(Map) JSON.parse(res);	
		Map rateMap=(Map) map.get("rates");
		double cnyValue=Double.valueOf(rateMap.get("CNY").toString());
		redisTemplate.opsForValue().set(CNY_USDT_RATE_KEY, cnyValue);
	}
	public void refreshUSDTRATE() {
		String res = restTemplate.getForObject(legalCurrencyUrl + "?marketName=USDT-BTC&tickInterval=oneMin&_=" + DateUtil.getCurrentTimestamp(), String.class);
		Map<String, Object> resMap = (Map<String, Object>) JSON.parse(res);
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) resMap.get("result");
		Map<String, Object> cMap = resultList.get(0);
		BigDecimal btcRate = new BigDecimal(cMap.get("C").toString());
		redisTemplate.opsForValue().set(USDT_BTC_RATE_KEY, btcRate);
		
		res = restTemplate.getForObject(legalCurrencyUrl + "?marketName=USDT-ETH&tickInterval=oneMin&_=" + DateUtil.getCurrentTimestamp(), String.class);
		Map<String, Object> ETHResMap = (Map<String, Object>) JSON.parse(res);
		List<Map<String, Object>> ETHResultList = (List<Map<String, Object>>) ETHResMap.get("result");
		Map<String, Object> ETHMap = ETHResultList.get(0);
		BigDecimal ethRate = new BigDecimal(ETHMap.get("C").toString());
		redisTemplate.opsForValue().set(USDT_ETH_RATE_KEY, ethRate);
	}
	
}
