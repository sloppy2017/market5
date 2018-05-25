package com.c2b.coin.market.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.market.mapper.MatchMoneyMapper;
import com.c2b.coin.market.thread.KLineDataThread;
import com.c2b.coin.matching.model.Order;
import com.c2b.coin.cache.redis.RedisUtil;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

@Service
public class MarketService extends TaskServiceBase {

	@Autowired
	private MatchMoneyMapper matchMoneyMapper;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	RestTemplate restTemplate;

	@Value("${key.redis.data.realtime.buylist}")
	private String REDIS_REALTIME_BUY_KEY;

	@Value("${key.redis.data.realtime.selllist}")
	private String REDIS_REALTIME_SELL_KEY;

	@Value("${key.redis.data.realtime.trade}")
	private String REDIS_REALTIME_KEY;

	public BigDecimal getRealTimePrice(String commodity , String money){

		Map<String,Object> result =  this.matchMoneyMapper.getRealTimePriceBycurrency(commodity+"/"+money);
		if(result == null || result.size() == 0){
			return BigDecimal.ZERO;
		}else{
			return (BigDecimal) result.get("made_price");
		}
	}

	public BigDecimal getRealTimePrice(String tradePairs){
		Map<String,Object> result =  this.matchMoneyMapper.getRealTimePriceBycurrency(tradePairs);
		if(result == null || result.size() == 0){
			return BigDecimal.ZERO;
		}else{
			return (BigDecimal) result.get("money");
		}
	}

	public Map<String,Object> getAllRealTimePrice(){
		Map<String,Object> result = new HashMap<>();
		List<String> pairs = getTradePairs();
		for(String trade : pairs){
			result.put(trade,getRealTimePrice(trade));
		}
		return result;
	}

	public String getAll24HNewsInfo(){
		HashMap<String,Object> resultMap = new HashMap<>();
		String baseCoin = "";
		List tradeList = new ArrayList();

		List<Map<String,Object>> coins = this.matchMoneyMapper.getTradePairs();
		boolean isLeft = false;
		for(Map base : coins){
			if("".equals(baseCoin)){
				baseCoin = (String)base.get("money_coin_name");
			}
			if(base.get("money_coin_name").equals(baseCoin)){
				HashMap map = getCurrencyMap(base.get("commodity_coin_name")+"/"+baseCoin);
				map.put("pair",base.get("commodity_coin_name")+"/"+baseCoin);
				tradeList.add(map);
			}else{
				resultMap.put(baseCoin,tradeList);
				baseCoin = (String)base.get("money_coin_name");
				tradeList = new ArrayList();
				HashMap map = getCurrencyMap(base.get("commodity_coin_name")+"/"+baseCoin);
				map.put("pair",base.get("commodity_coin_name")+"/"+baseCoin);
				tradeList.add(map);
				isLeft = true;
			}
		}
		if(isLeft){
			resultMap.put(baseCoin,tradeList);
		}

		return JSONObject.toJSONString(resultMap);
	}

	public String get24HNewsInfo(String currency){

		try{
			HashMap resultMap = getCurrencyMap(currency);
			return JSONObject.toJSONString(resultMap);
		}catch (Exception e ){
			return "";
		}

	}

	private HashMap getCurrencyMap(String currency) {

		Map<String,Object> newMap =  matchMoneyMapper.getRealTimePriceBycurrency(currency);
		Map<String,Object> oldMap =  matchMoneyMapper.get24HOldPriceBycurrencyTime(currency);

		BigDecimal maxPrice = BigDecimal.ZERO ,minPrice = BigDecimal.ZERO ,newPrice,
				oldPrice ,volume = BigDecimal.ZERO ;
		Map<String,Object> map1 = matchMoneyMapper.get24HMaxMinSumPriceBycurrency(currency);
		if(map1!=null && map1.size()>0 ){
			maxPrice = (BigDecimal) map1.get("max");
			minPrice = (BigDecimal) map1.get("min");
			volume = (BigDecimal) map1.get("sum");
		}

		newPrice = newMap != null && newMap.size()>0 ? (BigDecimal) newMap.get("made_price"):BigDecimal.ZERO;
		oldPrice = oldMap != null && oldMap.size()>0 ? (BigDecimal) oldMap.get("made_price"):BigDecimal.ZERO;

		BigDecimal amountIncrease = (oldPrice==null || BigDecimal.ZERO == oldPrice) ? BigDecimal.ZERO : newPrice.subtract(oldPrice).divide(oldPrice,8,BigDecimal.ROUND_HALF_DOWN);
		HashMap resultMap = new HashMap();
		resultMap.put("max",maxPrice);
		resultMap.put("min",minPrice);
		resultMap.put("new",newPrice);
		resultMap.put("old",oldPrice);
		resultMap.put("volume", volume );
		resultMap.put("increase",amountIncrease);
		return resultMap;
	}

	public JSONArray getAgencyDataByType(String currencyType , String type , Integer max){
		if(StringUtils.isEmpty(type)){
			return null;
		}
//		Object message = null;
		LinkedList<Order> list =null;
		HashMap<String ,Object> map = new HashMap<>();
		map.put("count",0);
		map.put("money",0);
		map.put("time","");
		map.put("sumcount",0);

		String nullJson = JSONObject.toJSONString(map);
		if("BUY".equals(type)){
			Object object = redisUtil.getObject(REDIS_REALTIME_BUY_KEY+currencyType.toUpperCase());
			list = (LinkedList<Order>) object;
		}else if ("SELL".equals(type)){
			Object object = redisUtil.getObject(REDIS_REALTIME_SELL_KEY+currencyType.toUpperCase());
			list = (LinkedList<Order>)object;
		}
		JSONArray jsonArray = new JSONArray();
		Map<String,Object> messageMap = null;
		if(list!=null && list.size()!=0) {

			BigDecimal sumCount = BigDecimal.ZERO;
			if("BUY".equals(type)) {//买盘由大到小取十个档位
				messageMap = new TreeMap<>(new Comparator<String>(){
		            public int compare(String o1,String o2){
		                return  o2.compareTo(o1); //用正负表示大小值
		            }
		        });
				for(int i = list.size()-1;i>=0;i--) {
					Order order = list.get(i);
					String price =order.getPrice().toString();
					if(messageMap.size()<10) {
						Map<String,Object> orderMap = (Map<String, Object>) messageMap.get(price);
						if(orderMap ==null ){//盘口数据为空，放入盘口
							orderMap = new HashMap<String,Object>();
							sumCount = sumCount.add(order.getAmount());
							orderMap.put("count", order.getAmount());
							orderMap.put("money", order.getPrice());
							orderMap.put("time", order.getTimestamp());
							orderMap.put("sumCount", sumCount);
							messageMap.put(price, orderMap);
						}else {
							sumCount = sumCount.add(order.getAmount());
							orderMap.put("count", new BigDecimal(orderMap.get("count").toString()).add(order.getAmount()));
							orderMap.put("sumCount", sumCount);
						}
					}
				}
			}else if ("SELL".equals(type)){//卖盘由小到大取十个档位
				messageMap = new TreeMap<>(new Comparator<String>(){
		            public int compare(String o1,String o2){
		                return  o1.compareTo(o2); //用正负表示大小值
		            }
		        });
				for (Order order : list) {
					String price =order.getPrice().toString();
					if(messageMap.size()<10) {
						Map<String,Object> orderMap = (Map<String, Object>) messageMap.get(price);
						if(orderMap ==null ) {//盘口数据为空，放入盘口
							orderMap = new HashMap<String,Object>();
							sumCount = sumCount.add(order.getAmount());
							orderMap.put("count", order.getAmount());
							orderMap.put("money", order.getPrice());
							orderMap.put("time", order.getTimestamp());
							orderMap.put("sumCount", sumCount);
							messageMap.put(price, orderMap);
						}else {
							sumCount = sumCount.add(order.getAmount());
							orderMap.put("count", new BigDecimal(orderMap.get("count").toString()).add(order.getAmount()));
							orderMap.put("sumCount", sumCount);
						}
					}
				}
			}
			//把MassageMap组装成数组
			for (Entry<String,Object> entry: messageMap.entrySet()) {
				jsonArray.add(entry.getValue());
			}
		}
		return  jsonArray;
	}

	public Calendar getCalendar(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND,-5);
		return calendar;
	}

	/**
	 *
	 * @param type
	 * @param json
	 * @return
	 */
	public StringBuilder handleNowData(String type,String json){
		Calendar calendar = getCalendar();
		StringBuilder key = new StringBuilder();
		key.append("{\"");
		if(type.toUpperCase().equals("RT")){
			key.append(calendar.get(Calendar.YEAR))
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)))
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""))
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE)+""))
			.append(completionStr(calendar.get(Calendar.SECOND)+""))
			.append("\":").append(json).append("}");
		}
		if(type.toUpperCase().equals("1M")){
			key.append(calendar.get(Calendar.YEAR))
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)))
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""))
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE) +""))
			.append("\":").append(json).append("}");
		}
		if(type.toUpperCase().equals("5M")){
			key.append(calendar.get(Calendar.YEAR))
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)))
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""))
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE)/5*5 +""))
			.append("\":").append(json).append("}");
		}
		if(type.toUpperCase().equals("15M")){
			key.append(calendar.get(Calendar.YEAR))
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)))
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""))
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE)/15*15+""))
			.append("\":").append(json).append("}");
		}
		if(type.toUpperCase().equals("30M")){
			key.append(calendar.get(Calendar.YEAR))
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)))
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""))
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE)/30*30+""))
			.append("\":").append(json).append("}");
		}
		if(type.toUpperCase().equals("60M")){
			key.append(calendar.get(Calendar.YEAR))
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)))
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""))
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append("00").append("\":").append(json).append("}");
		}
		if(type.toUpperCase().equals("1D")){
			//      calendar.add(Calendar.DAY_OF_MONTH,-1);
			key.append(calendar.get(Calendar.YEAR))
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)))
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""))
			.append("\":").append(json).append("}");
		}
		if(type.toUpperCase().equals("1W")){
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			key.append(calendar.get(Calendar.YEAR))
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)))
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""))
			.append("\":").append(json).append("}");
		}
		if(type.toUpperCase().equals("1MO")){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			key.append(calendar.get(Calendar.YEAR))
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)))
			.append("\":").append(json).append("}");
		}
		return key;
	}

	public StringBuilder getJsonByFile(String type,String commodity,String money){
		Calendar calendar = getCalendar();
		StringBuilder fileName = new StringBuilder();
		if(type.toUpperCase().equals("RT")){
			fileName.append("REAL_TIME/").append(commodity).append("/").append(money).append("/")
			.append(calendar.get(Calendar.YEAR)).append("/")
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1))).append("/")
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+"")).append("/")
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE)+""))
			.append(completionStr(calendar.get(Calendar.SECOND)+""));
		}
		if(type.toUpperCase().equals("1M")){
			fileName.append("1MINUTES/").append(commodity).append("/").append(money).append("/")
			.append(calendar.get(Calendar.YEAR)).append("/")
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1))).append("/")
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+"")).append("/")
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE) +""));
		}
		if(type.toUpperCase().equals("5M")){
			fileName.append("5MINUTES/").append(commodity).append("/").append(money).append("/")
			.append(calendar.get(Calendar.YEAR)).append("/")
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1))).append("/")
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+"")).append("/")
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE)/5*5 +""));
		}
		if(type.toUpperCase().equals("15M")){
			fileName.append("15MINUTES/").append(commodity).append("/").append(money).append("/")
			.append(calendar.get(Calendar.YEAR)).append("/")
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1))).append("/")
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+"")).append("/")
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE)/15*15+""));
		}
		if(type.toUpperCase().equals("30M")){
			fileName.append("30MINUTES/").append(commodity).append("/").append(money).append("/")
			.append(calendar.get(Calendar.YEAR)).append("/")
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1))).append("/")
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+"")).append("/")
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append(completionStr(calendar.get(Calendar.MINUTE)/30*30+""));
		}
		if(type.toUpperCase().equals("60M")){
			fileName.append("60MINUTES/").append(commodity).append("/").append(money).append("/")
			.append(calendar.get(Calendar.YEAR)).append("/")
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1))).append("/")
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+"")).append("/")
			.append(completionStr(calendar.get(Calendar.HOUR_OF_DAY)+""))
			.append("00");
		}
		if(type.toUpperCase().equals("1D")){
			//      calendar.add(Calendar.DAY_OF_MONTH,-1);
			fileName.append("1DAY/").append(commodity).append("/").append(money).append("/")
			.append(calendar.get(Calendar.YEAR)).append("/")
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1))).append("/")
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""));
		}
		if(type.toUpperCase().equals("1W")){
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			fileName.append("1WEEK/").append(commodity).append("/").append(money).append("/")
			.append(calendar.get(Calendar.YEAR)).append("/")
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1))).append("/")
			.append(completionStr(calendar.get(Calendar.DAY_OF_MONTH)+""));
		}
		if(type.toUpperCase().equals("1MO")){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			fileName.append("1MONTH/").append(commodity).append("/").append(money).append("/")
			.append(calendar.get(Calendar.YEAR)).append("/")
			.append(completionStr(String.valueOf(calendar.get(Calendar.MONTH ) + 1)));
		}

		logger.debug("file path : "+fileName.toString());
		return fileName;
	}


	public String readFile(String fileName){
		List<String> list = null;
		try {
			list = Files.readLines(new File(fileName), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return String.valueOf(list);
	}

	public String requestJson(String url){
		return restTemplate.getForObject(url,String.class);
	}
	public String requestJsonByRedis(String type1,String type2,String commodity ,String money ){
		String redis_hash = "",redis_key = "";
		if("NOW".equals(type2.toUpperCase()) && "RT".equals(type1.toUpperCase())){
			redis_key = REDIS_REALTIME_KEY + commodity +"/"+ money;
			return (String)stringRedisTemplate.opsForValue().get(redis_key);
		}
		if("BEFORE".equals(type2.toUpperCase())){
			redis_hash = KLineDataThread.REDIS_KEY_BEFORE + commodity +"/"+ money;
		}
		if("NOW".equals(type2.toUpperCase())){
			redis_hash = KLineDataThread.REDIS_KEY_NOW + commodity +"/"+ money;
		}
		if("RT".equals(type1.toUpperCase())){
			redis_key = "REALTIME";
		}
		if("1M".equals(type1.toUpperCase())){
			redis_key = "1MINUTES";
		}
		if("5M".equals(type1.toUpperCase())){
			redis_key = "5MINUTES";
		}
		if("15M".equals(type1.toUpperCase())){
			redis_key = "15MINUTES";
		}
		if("30M".equals(type1.toUpperCase())){
			redis_key = "30MINUTES";
		}
		if("60M".equals(type1.toUpperCase())){
			redis_key = "60MINUTES";
		}
		if("1D".equals(type1.toUpperCase())){
			redis_key = "1DAY";
		}
		if("1W".equals(type1.toUpperCase())){
			redis_key = "1WEEK";
		}
		if("1MO".equals(type1.toUpperCase())){
			redis_key = "1MONTH";
		}

		return (String)stringRedisTemplate.opsForHash().get(redis_hash,redis_key);
	}

	private String completionStr(String str){
		return StringUtils.leftPad(str,2,"0");
	}
}
