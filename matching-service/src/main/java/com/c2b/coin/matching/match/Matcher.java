package com.c2b.coin.matching.match;

import java.math.BigDecimal;
import java.util.*;

import javax.jms.Queue;

import com.c2b.coin.cache.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.matching.constant.EnumConsignedType;
import com.c2b.coin.matching.constant.EnumTradeType;
import com.c2b.coin.matching.match.biz.BizAdaptor;
import com.c2b.coin.matching.model.Order;
import com.c2b.coin.matching.vo.queue.ExchangeVO;
import com.c2b.coin.matching.vo.queue.MatchInfoVO;
import com.c2b.coin.matching.vo.queue.ResultCallbackVO;



/**
 * 交易撮合机
 * 撮合机制的原则是
 * 1.价格优先
 * 2.时间优先
 * @author DingYi
 * @version 1.0.0
 * @since 2018年4月19日 下午3:53:23
 */
@Component
@Scope("prototype")
public final class Matcher implements BizAdaptor{

	@Autowired
	private RedisUtil redisUtil;
//	@Autowired
//	private OrderMap orderMap;

	@Value("${key.redis.data.realtime.buylist}")
	private String BUY_PREFIX ;
	@Value("${key.redis.data.realtime.selllist}")
	private String SELL_PREFIX ;

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired // 也可以注入JmsTemplate，JmsMessagingTemplate对JmsTemplate进行了封装
	private JmsMessagingTemplate jmsMessagingTemplate;
	@Autowired
	Queue tradeOnceQueue;
	@Autowired
	Queue endMatchQueue;


	private final static BigDecimal fee = new BigDecimal("0.001");
	/**
	 *
	 * @param tradePair 交易对
	 * @param price 价格
	 * @param amount 数量
	 * @param orderNo 订单号
	 * @param enumTradeType 交易类型
	 * @param enumConsignedType 交易方式
	 */
	public void match(String  tradePair,BigDecimal price,BigDecimal amount,String orderNo,EnumTradeType enumTradeType,EnumConsignedType enumConsignedType) {
		//根据交易类型
		switch (enumTradeType) {
		case buy:
			matchBuy(tradePair,price,amount,orderNo,enumConsignedType);
			break;
		case sell:
			matchSell(tradePair,price,amount,orderNo,enumConsignedType);
			break;
		default:
			break;
		}

	}

	/**
	 * 撤单事件
	 * @param tradePair
	 * @param orderNo
	 */
	public ResultCallbackVO matchCallback(String tradePair,String orderNo) {
		//根据订单号获取队列中的值
		//遍历两层队列
		ResultCallbackVO rcvo=new ResultCallbackVO();
		LinkedList<Order> buyList = getBuyList(tradePair);
		int count = 0;
		for (Order order : buyList) {
			if(order.getOrderNo().equals(orderNo)) {
				buyList.remove(order);
				count++;
				rcvo.setCallBack(true);
				rcvo.setCode(101);
				rcvo.setSumMoney(order.getPrice());
				rcvo.setResidueCount(order.getAmount());
				redisUtil.set(BUY_PREFIX+tradePair.toUpperCase(), buyList);
			}
		}
		LinkedList<Order> sellList = getSellList(tradePair);
		for (Order order : sellList) {
			if(order.getOrderNo().equals(orderNo)) {
				sellList.remove(order);
				count++;
				rcvo.setCallBack(true);
				rcvo.setCode(101);
				rcvo.setSumMoney(order.getPrice());
				rcvo.setResidueCount(order.getAmount());
				redisUtil.set(SELL_PREFIX+tradePair.toUpperCase(), buyList);
			}
		}
		if(count==0) {//撤单失败
			rcvo.setCallBack(false);
			rcvo.setCode(103);
		}
		return rcvo;
	}


	/**
	 * 撮合卖方挂单交易
	 * @param tradePair
	 * @param price
	 * @param amount
	 * @param orderNo
	 * @param enumConsignedType
	 */
	private void matchSell(String tradePair, BigDecimal price, BigDecimal amount, String orderNo,
			EnumConsignedType enumConsignedType) {
		//撮合卖方挂单
		switch (enumConsignedType) {
		case LIMIT:
			matchSellLimit(tradePair,price,amount,orderNo,enumConsignedType.getCode());
			break;
		case MARKET:
      matchSellMarket(tradePair,price,amount,orderNo,enumConsignedType.getCode());
			break;
		default:
			break;
		}
	}



	/**
	 * 撮合买方挂单交易
	 * @param tradePair
	 * @param price
	 * @param amount
	 * @param orderNo
	 * @param enumConsignedType
	 */
	private void matchBuy(String tradePair, BigDecimal price, BigDecimal amount, String orderNo,
			EnumConsignedType enumConsignedType) {
		//撮合买方挂单
		switch (enumConsignedType) {
		case LIMIT:
			matchBuyLimit(tradePair,price,amount,orderNo,enumConsignedType.getCode());
			break;
		case MARKET:
			matchBuyMarket(tradePair,price,amount,orderNo,enumConsignedType.getCode());
			break;
		default:
			break;
		}
	}

	/**
	 * 撮合买方限价交易
	 * @param tradePair
	 * @param price
	 * @param amount
	 * @param orderNo
	 */
	private void matchBuyLimit(String tradePair, BigDecimal price, BigDecimal amount, String orderNo,String buyGenre) {
		//撮合买方限价交易
		//根据交易对拿取对手方(卖方)队列
		LinkedList sellList = getSellList(tradePair);
		//命中对手盘,撮合限价交易
		boolean hitResult = hitBuyLimitMatchList(tradePair,sellList,price,amount,orderNo,BigDecimal.ZERO,new ArrayList<Object>(),buyGenre);
		//如果没命中，获取己方盘,进行入盘操作
		if(!hitResult) {
			//入盘操作
			pushBuyList(tradePair,price,amount,orderNo);
		}
	}


	/**
	 * 放入买盘操作
	 * @param tradePair
	 * @param price
	 * @param amount
	 * @param orderNo
	 */
	private void pushBuyList(String tradePair, BigDecimal price, BigDecimal amount, String orderNo) {
		//生成Order
		Order order = createOrder(tradePair, price, amount, orderNo, EnumTradeType.buy);
		//获取买方队列
		LinkedList<Order> buyList = getBuyList(tradePair);
		//二分法寻找插入位置
		int index = searchIndex(price,buyList);
		buyList.add(index, order);
		redisUtil.set(BUY_PREFIX+tradePair.toUpperCase(), buyList);
	}

	private int searchIndex(BigDecimal price, LinkedList<Order> buyList) {
		if(buyList.size() == 0) {//如果没有元素
			return 0;
		}
		if(buyList.size()==1) {//如果元素数量为1
			Order order = buyList.getFirst();
			if(order.getPrice().compareTo(price)>=0){//如果新元素小于原有元素
				return 0;
			}else {
				return 1;
			}
		}
		Order firstOrder = buyList.getFirst();
		if(firstOrder.getPrice().compareTo(price)>=0) {
			//如果小于第一个元素，放入首位
			return 0;
		}
		Order lastOrder = buyList.getLast();
		if(lastOrder.getPrice().compareTo(price)<=0) {
			//如果大于第一个元素，放入末位
			return buyList.size();
		}
		int start = 0;
		int end = buyList.size()-1;
		while (start <= end) {
			int middle = (start + end) / 2;
			Order frontOrder = buyList.get(middle);
			Order backendOrder =buyList.get(middle+1);
			if (price.compareTo(frontOrder.getPrice()) <=0 && price.compareTo(backendOrder.getPrice())<= 0) {
				end = middle - 1;
			} else if (price.compareTo(frontOrder.getPrice()) >=0 && price.compareTo(backendOrder.getPrice())>= 0) {
				start = middle + 1;
			} else if(price.compareTo(frontOrder.getPrice()) >=0 && price.compareTo(backendOrder.getPrice())<= 0){
				return middle+1;
			}
		}
		return -1;
	}

	/**
	 * 撮合买方市价交易
	 * @param tradePair
	 * @param price
	 * @param amount
	 * @param orderNo
	 */
	private void matchBuyMarket(String tradePair, BigDecimal price, BigDecimal amount, String orderNo,String buyGenre) {
		//撮合买方市价交易
		//根据交易对拿取对手方(卖方)队列
		LinkedList<Order> sellList = getSellList(tradePair);
		//撮合市价交易
		hitBuyMarketMatchList(tradePair,sellList,price,amount,orderNo,price.multiply(amount),new ArrayList<Object>(),buyGenre);
	}

	/**
	 * 撮合买单市价交易
	 * @param sellList
	 * @param price
	 * @param amount
	 * @param orderNo
	 */
	private void hitBuyMarketMatchList(String tradePair,LinkedList<Order> sellList, BigDecimal price, BigDecimal amount, String orderNo,BigDecimal makeMoney,List<Object> detailList,String buyGenre) {
		//从低往高撮合
		Order order = sellList.getFirst();
		if(order.getAmount().compareTo(amount)>0) {//对手盘数量大于此次需要撮合的数量
			//修改对手盘单中的剩余数量
			order.setAmount(order.getAmount().subtract(amount));
			//撮合订单
			matchOrder(tradePair,sellList,order,orderNo,order.getPrice(),amount,EnumTradeType.buy,buyGenre);
			//发送撮合完成队列  成交均价  已成单为全部money  type currency orderN
			ExchangeVO exchangeVO = new ExchangeVO();
			exchangeVO.setSeq(orderNo);
			exchangeVO.setCurrency(tradePair);
			exchangeVO.setMakeMoney(makeMoney);
			exchangeVO.setType("buy");
			BigDecimal sumMoney = BigDecimal.ZERO;
			BigDecimal sumAmount = BigDecimal.ZERO;
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("price", order.getPrice());
			map.put("amount", amount);
			detailList.add(map);
			for (Object object : detailList) {
				Map detailMap = (Map) object;
				BigDecimal detailPrice = (BigDecimal) detailMap.get("price");
				BigDecimal detailAmount = (BigDecimal) detailMap.get("amount");
				sumMoney= sumMoney.add(detailPrice.multiply(detailAmount));
				sumAmount = sumAmount.add(detailAmount);
			}
			exchangeVO.setAverageMoney(sumMoney.divide(sumAmount));
			jmsTemplate.convertAndSend(endMatchQueue, JSONObject.toJSON(exchangeVO));
		}else {//对手盘数量小于此次需要撮合的数量,直接撮合，移除元素，并递归调用下一次撮合
			//移除元素
			sellList.removeFirst();
			//撮合订单
			matchOrder(tradePair,sellList,order,orderNo,order.getPrice(),order.getAmount(),EnumTradeType.buy, buyGenre);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("price", order.getPrice());
			map.put("amount", order.getAmount());
			detailList.add(map);
			//递归调用
			hitBuyMarketMatchList(tradePair,sellList, order.getPrice(), order.getAmount(), orderNo,makeMoney,detailList, buyGenre);
		}
	}

	/**
	 * 限价交易命中买盘
	 * @param sellList
	 * @param price
	 * @param amount
	 * @param orderNo
	 * @return
	 */
	private boolean hitBuyLimitMatchList(String tradePair,LinkedList<Order> sellList, BigDecimal price, BigDecimal amount, String orderNo,BigDecimal makeMoney,List<Object> detailList,String buyGenre) {

		Order order = null;
		//最低卖盘与价格比较
		if(sellList.size()==0) {
			return false;
		}else {
			order = sellList.get(0);
		}
		if(order.getPrice().compareTo(price)<=0) {//命中卖盘
			if(order.getAmount().compareTo(amount)>=0) {//对手盘数量大于此次需要撮合的数量
				//修改对手盘单中的剩余数量
				order.setAmount(order.getAmount().subtract(amount));
				//撮合订单
				matchOrder(tradePair,sellList,order,orderNo,order.getPrice(),amount,EnumTradeType.buy,buyGenre);
				//发送撮合完成队列  成交均价  已成单为全部money  type currency orderN
				ExchangeVO exchangeVO = new ExchangeVO();
				exchangeVO.setSeq(orderNo);
				exchangeVO.setCurrency(tradePair);
				exchangeVO.setMakeMoney(makeMoney.add(amount.multiply(price)));
				exchangeVO.setType("buy");
				BigDecimal sumMoney = BigDecimal.ZERO;
				BigDecimal sumAmount = BigDecimal.ZERO;
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("price", order.getPrice());
				map.put("amount", amount);
				detailList.add(map);
				for (Object object : detailList) {
					Map detailMap = (Map) object;
					BigDecimal detailPrice = (BigDecimal) detailMap.get("price");
					BigDecimal detailAmount = (BigDecimal) detailMap.get("amount");
					sumMoney= sumMoney.add(detailPrice.multiply(detailAmount));
					sumAmount = sumAmount.add(detailAmount);
				}
				exchangeVO.setAverageMoney(sumMoney.divide(sumAmount));
				jmsTemplate.convertAndSend(endMatchQueue, JSONObject.toJSON(exchangeVO));
			}else {//对手盘数量小于此次需要撮合的数量,直接撮合，移除元素，并递归调用下一次撮合
				//移除元素
				sellList.removeFirst();
				//撮合订单
				matchOrder(tradePair,sellList,order,orderNo,order.getPrice(),order.getAmount(),EnumTradeType.buy,buyGenre);
				//递归调用
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("price", order.getPrice());
				map.put("amount", order.getAmount());
				detailList.add(map);
				hitBuyLimitMatchList(tradePair,sellList, order.getPrice(), order.getAmount(), orderNo,makeMoney.add(order.getPrice().multiply(order.getAmount())),detailList,buyGenre);
			}
			return true;
		}
		return false;
	}

	/**
	 * 撮合卖方限价交易
	 * @param tradePair
	 * @param price
	 * @param amount
	 * @param orderNo
	 */
	private void matchSellLimit(String tradePair, BigDecimal price, BigDecimal amount, String orderNo,String buyGenre) {
		// 根据交易对获取对手盘(买方)队列
		LinkedList<Order> buyList = getBuyList(tradePair);
		//命中对手盘,撮合限价交易
		boolean hitResult = hitSellLimitMatchList(tradePair,buyList,price,amount,orderNo,BigDecimal.ZERO,new ArrayList<Object>(),buyGenre);
		//如果没命中，获取己方盘,进行入盘操作
		if(!hitResult) {
			//入盘操作
			pushSellList(tradePair,price,amount,orderNo);
		}
	}

	private void pushSellList(String tradePair, BigDecimal price, BigDecimal amount, String orderNo) {
		//生成Order
		Order order = createOrder(tradePair, price, amount, orderNo, EnumTradeType.buy);
		//获取买方队列
		LinkedList<Order> sellList = getSellList(tradePair);
		//二分法寻找插入位置
		int index = searchIndex(price,sellList);
		sellList.add(index, order);
		redisUtil.set(SELL_PREFIX+tradePair.toUpperCase(), sellList);

	}

	/**
	 * 限价交易命中买盘
	 * @param buyList
	 * @param price
	 * @param amount
	 * @param orderNo
	 * @return
	 */
	private boolean hitSellLimitMatchList(String tradePair,LinkedList<Order> buyList, BigDecimal price, BigDecimal amount, String orderNo,BigDecimal makeMoney,List<Object> detailList,String buyGenre) {
		if(buyList.size()==0){return false;}
    Order order = buyList.get(0);
		if(order.getPrice().compareTo(price)>=0) {//命中买盘
			if(order.getAmount().compareTo(amount)<=0) {//对手盘数量大于此次需要撮合的数量
				//修改对手盘单中的剩余数量
				order.setAmount(order.getAmount().subtract(amount));
				//撮合订单
				matchOrder(tradePair,buyList,order,orderNo,order.getPrice(),amount,EnumTradeType.sell,buyGenre);
				//发送撮合完成队列  成交均价  已成单为全部money  type currency orderN
				ExchangeVO exchangeVO = new ExchangeVO();
				exchangeVO.setSeq(orderNo);
				exchangeVO.setCurrency(tradePair);
				exchangeVO.setMakeMoney(makeMoney.add(amount));
				exchangeVO.setType("sell");
				BigDecimal sumMoney = BigDecimal.ZERO;
				BigDecimal sumAmount = BigDecimal.ZERO;
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("price", order.getPrice());
				map.put("amount", amount);
				detailList.add(map);
				for (Object object : detailList) {
					Map detailMap = (Map) object;
					BigDecimal detailPrice = (BigDecimal) detailMap.get("price");
					BigDecimal detailAmount = (BigDecimal) detailMap.get("amount");
					sumMoney= sumMoney.add(detailPrice.multiply(detailAmount));
					sumAmount = sumAmount.add(detailAmount);
				}
				exchangeVO.setAverageMoney(sumMoney.divide(sumAmount));
				jmsTemplate.convertAndSend(endMatchQueue, JSONObject.toJSONString(exchangeVO));
			}else {//对手盘数量小于此次需要撮合的数量,直接撮合，移除元素，并递归调用下一次撮合
				//移除元素
				buyList.removeLast();
				//撮合订单
				matchOrder(tradePair,buyList,order,orderNo,order.getPrice(),order.getAmount(),EnumTradeType.sell,buyGenre);
				//递归调用
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("price", order.getPrice());
				map.put("amount", order.getAmount());
				detailList.add(map);
				hitSellLimitMatchList(tradePair,buyList, order.getPrice(), order.getAmount(), orderNo,makeMoney.add(amount),detailList,buyGenre);
			}
			return true;
		}
		return false;
	}

	/**
	 * 撮合卖方市价交易
	 * @param tradePair
	 * @param price
	 * @param amount
	 * @param orderNo
	 */
	private void matchSellMarket(String tradePair, BigDecimal price, BigDecimal amount, String orderNo,String buyGenre) {
		//撮合买方市价交易
		//根据交易对拿取对手方(卖方)队列
		LinkedList<Order> buyList = getBuyList(tradePair);
		//撮合市价交易
		hitSellMarketMatchList(tradePair,buyList,price,amount,orderNo,amount,new ArrayList<Object>(),buyGenre);
	}

	private void hitSellMarketMatchList(String tradePair,LinkedList<Order> buyList, BigDecimal price, BigDecimal amount,
			String orderNo,BigDecimal makeMoney,List<Object> detailList,String buyGenre) {
		//从低往高撮合
		Order order = buyList.getFirst();
		if(order.getAmount().compareTo(amount)>0) {//对手盘数量大于此次需要撮合的数量
			//修改对手盘单中的剩余数量
			order.setAmount(order.getAmount().subtract(amount));
			//撮合订单
			matchOrder(tradePair,buyList,order,orderNo,order.getPrice(),amount,EnumTradeType.buy,buyGenre);
			//发送撮合完成队列  成交均价  已成单为全部money  type currency orderN
			ExchangeVO exchangeVO = new ExchangeVO();
			exchangeVO.setSeq(orderNo);
			exchangeVO.setCurrency(tradePair);
			exchangeVO.setType("sell");
			exchangeVO.setMakeMoney(makeMoney);
			BigDecimal sumMoney = BigDecimal.ZERO;
			BigDecimal sumAmount = BigDecimal.ZERO;
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("price", order.getPrice());
			map.put("amount", amount);
			detailList.add(map);
			for (Object object : detailList) {
				Map detailMap = (Map) object;
				BigDecimal detailPrice = (BigDecimal) detailMap.get("price");
				BigDecimal detailAmount = (BigDecimal) detailMap.get("amount");
				sumMoney= sumMoney.add(detailPrice.multiply(detailAmount));
				sumAmount = sumAmount.add(detailAmount);
			}
			exchangeVO.setAverageMoney(sumMoney.divide(sumAmount));
			jmsTemplate.convertAndSend(endMatchQueue, JSONObject.toJSONString(exchangeVO));
		}else {//对手盘数量小于此次需要撮合的数量,直接撮合，移除元素，并递归调用下一次撮合
			//移除元素
			buyList.removeLast();
			//撮合订单
			matchOrder(tradePair,buyList,order,orderNo,order.getPrice(),order.getAmount(),EnumTradeType.buy,buyGenre);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("price", order.getPrice());
			map.put("amount", order.getAmount());
			detailList.add(map);
			//递归调用
			hitSellMarketMatchList(tradePair,buyList, order.getPrice(), order.getAmount(), orderNo,makeMoney,detailList,buyGenre);
		}
	}

	private void matchOrder(String tradePair,LinkedList<Order> list,Order order, String orderNo, BigDecimal price, BigDecimal amount,EnumTradeType enumTradeType,String buyGenre) {
		//撮合发送通知、更新到缓存，异步数据库
		switch (enumTradeType) {
		case buy:
			redisUtil.set(SELL_PREFIX+tradePair, list);
//      buyConsignationLog.getUserId(),
//        buyConsignationLog.getUsername(), sellConsignationLog.getUserId(), sellConsignationLog.getUsername(),
//        matchInfoVO.getSeq(), moneyDigitalCoin.getId(), commodityDigitalCoin.getId(), matchInfoVO.getMoney(),
//        matchInfoVO.getCount(), matchInfoVO.getSellMoney(), matchInfoVO.getBuyMoney()
			MatchInfoVO matchInfoVO = new MatchInfoVO();
			matchInfoVO.setSeq(getSeq());//全局唯一序列号
			matchInfoVO.setCurrency(tradePair);//交易对
			matchInfoVO.setBuySeq(orderNo);//买单号
			matchInfoVO.setSellSeq(order.getOrderNo());//卖单号
			matchInfoVO.setBuyMoney(order.getPrice().multiply(amount).multiply(fee));//买入手续费，收取对手盘手续费
			matchInfoVO.setSellMoney(price.multiply(amount).multiply(fee));//卖出手续费，收取对手盘手续费
			matchInfoVO.setMoney(price);//成交金额
			matchInfoVO.setCount(amount);//成交数量
      matchInfoVO.setBuyGenre(buyGenre);
      matchInfoVO.setPairDate(new Date(order.getTimestamp()));
			jmsTemplate.convertAndSend(tradeOnceQueue, JSONObject.toJSONString(matchInfoVO));
			break;
		case sell:
			redisUtil.set(BUY_PREFIX+tradePair,list);
			//组装消息队列
			MatchInfoVO matchInfoVO2 = new MatchInfoVO();
			matchInfoVO2.setSeq(getSeq());//全局唯一序列号
			matchInfoVO2.setCurrency(tradePair);//交易对
			matchInfoVO2.setBuySeq(order.getOrderNo());//买单号
			matchInfoVO2.setSellSeq(orderNo);//卖单号
			matchInfoVO2.setBuyMoney(price.multiply(amount).multiply(fee));//买入手续费，收取对手盘手续费
			matchInfoVO2.setSellMoney(order.getPrice().multiply(amount).multiply(fee));//卖出手续费，收取对手盘手续费
			matchInfoVO2.setMoney(price);//成交金额
			matchInfoVO2.setCount(amount);//成交数量
      matchInfoVO2.setBuyGenre(buyGenre);
      matchInfoVO2.setPairDate(new Date(order.getTimestamp()));
			jmsTemplate.convertAndSend(tradeOnceQueue, JSONObject.toJSONString(matchInfoVO2));
			break;
		default:
			break;
		}


	}

	private LinkedList<Order> getSellList(String tradePair) {
		LinkedList<Order> sellList = (LinkedList<Order>) redisUtil.getObject(SELL_PREFIX+tradePair);
		if(null == sellList||sellList.size()==0) {
			return new LinkedList<Order>();
		}
		return sellList;
	}


	private LinkedList<Order> getBuyList(String tradePair) {
		LinkedList<Order>  buyList = (LinkedList<Order>) redisUtil.getObject(BUY_PREFIX+tradePair);
		if(null == buyList||buyList.size()==0) {
			return new LinkedList<Order>();
		}
		return buyList;
	}


	private Order createOrder(String tradePair, BigDecimal price, BigDecimal amount, String orderNo,EnumTradeType enumTradeType) {
		Order order = new Order();
		order.setPrice(price);
		order.setAmount(amount);
		order.setOrderNo(orderNo);
		order.setEnumTradeType(enumTradeType);
		order.setTimestamp(DateUtil.getCurrentTimestamp());
		return order;
	}

	//生成流水号
	private String getSeq() {
		 return UUID.randomUUID().toString().replace("-", "");
	}

}

