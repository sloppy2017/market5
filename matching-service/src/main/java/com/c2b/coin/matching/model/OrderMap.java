package com.c2b.coin.matching.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * 撮合系统存储结构
 * @author DingYi
 * @version 1.0.0
 * @since 2018年4月19日 下午9:13:25
 * @param <T>
 */
@Component
public class OrderMap {

	//map里放双向链表
	private static Map<String,Map> buyOrderMap = new ConcurrentHashMap<String, Map>(); 
	private static Map<String,Map> sellOrderMap = new ConcurrentHashMap<String, Map>(); 
//	/**
//	 * 初始化买卖队列
//	 */
//	public static void init() {
//		initBuyOrderMap();
//		initSellOrderMap();
//	}
//	/**
//	 * 初始化买单列表
//	 */
//	public static void initBuyOrderMap() {
//		//TODO 初始化买单
//	}
//	/**
//	 * 初始化卖单列表
//	 */
//	public static void initSellOrderMap() {
//		//TODO
//	}
	/**
	 * 根据交易对拿到买方树
	 * @param tradePair
	 * @return
	 */
	public LinkedList getBuyList(String tradePair){
		
		return null;
	}
	/**
	 * 根据交易对拿到卖方树
	 * @param tradePair
	 * @return
	 */
	public LinkedList getSellList(String tradePair) {
		return null;
	}
	/**
	 * 排序队列
	 * @return
	 */
	public  Map sort() {
		return null;
	}
	public  Map saveOrderMap() {
		return null;
	}
	
}
