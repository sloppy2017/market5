package com.c2b;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.TradeApplication;
import com.c2b.coin.trade.entity.ConsignationLog;
import com.c2b.coin.trade.exceptions.TradeException;
import com.c2b.coin.trade.mapper.ConsignationLogMapper;
import com.c2b.coin.trade.service.ConsignationService;
import com.c2b.coin.trade.service.OrderLogService;
import com.c2b.coin.trade.vo.ExchangeVO;
import com.c2b.coin.trade.vo.MatchInfoVO;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TradeApplication.class)
@WebAppConfiguration
public class EcoSendTranTest {

  @Autowired
  ConsignationLogMapper consignationLogMapper;

  @Autowired
  ConsignationService consignationService;

  @Autowired
  OrderLogService orderLogService;

  /*@Test
  public void test(){
    ConsignationLog record = new ConsignationLog();
    String consignationNo = \\\\"2017110794536015778000011\";
    record.setConsignationNo(consignationNo);
    ConsignationLog consignationLog = consignationLogMapper.selectOne(record);
    BigDecimal ufzAmount = consignationService.getUfzAmount(consignationLog);
    System.out.println(\"ufzAmount=\"+ufzAmount);
  }*/
  /*
  @Test
  public void testOrder() throws TradeException{
    ConsignationLog record = new ConsignationLog();
    String consignationNo = \"20171109110152864596000347\";
    record.setConsignationNo(consignationNo);
    ConsignationLog consignationLog = consignationLogMapper.selectOne(record);
    String jsonString = "{\"id\":143,\"seq\":\"20171109120009196798000355\",\"userId\":\"48\",\"type\":\"buy\",\"currency\":\"LTC/BTC\",\"count\":0E-20,\"money\":0.10000000000000000000,\"date\":1510200009000,\"input\":null,\"genre\":\"PriceDeal\",\"status\":1,\"callbackType\":0,\"residueCount\":0E-20,\"makeCount\":0.99000000000000000000,\"makeMoney\":0.09900000000000000000,\"residueMoney\":0,\"averageMoney\":0.10000000000000000000,\"list\":[{\"id\":62,\"buySeq\":\"20171109120009196798000355\",\"sellSeq\":\"20171109120035287389000356\",\"count\":0.99000000000000000000,\"money\":0.09900000000000000000,\"changer\":0.10000000000000000000,\"buyCharge\":0.10000000000000000000,\"sellCharge\":0.10000000000000000000,\"seq\":\"C2017110912003500000000500000000001\",\"pairDate\":1510200036000,\"busDate\":1510200009000,\"sellDate\":1510200036000,\"buyUser\":\"48\",\"sellUser\":\"88\",\"buyGenre\":\"PriceDeal\",\"sellGenre\":\"PriceDeal\",\"matchDealId\":0,\"currency\":\"LTC/BTC\",\"buyMoney\":0.00099000000000000000,\"sellMoney\":0.00009900000000000000,\"tuple\":null}]}";
    ExchangeVO vo = JSONObject.parseObject(jsonString, ExchangeVO.class);
    orderLogService.endMatch(vo);;
  }*/

  /*@Test
  public void testListUnTradeOrder(){
    consignationService.listUnTradeOrder("88");
  }*/

 /* @Test
  public void testMarketEndMatch() throws TradeException{
    String jsonString = "{\"id\":82,\"seq\":\"20171109155224402119000454\",\"userId\":\"103\",\"type\":\"sell\",\"currency\":\"ETH/BTC\",\"count\":1.00000000000000000000,\"money\":0E-20,\"date\":1510213945000,\"input\":null,\"genre\":\"MarketTransactions\",\"status\":1,\"callbackType\":0,\"residueCount\":1.00000000000000000000,\"makeCount\":0,\"makeMoney\":0,\"residueMoney\":0,\"averageMoney\":0,\"list\":[]}";
    ExchangeVO vo = JSONObject.parseObject(jsonString, ExchangeVO.class);
    orderLogService.endMatch(vo);
  }*/

  @Test
  public void testGetBuyOrSellOrder(){
//    consignationService.listConsignationOrderByUserId("83", 4, 0);
  }

  @Test
  public void testOrderLog(){
    orderLogService.listOrder("122", "0", "1", "10");
  }
}
