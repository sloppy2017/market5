package com.c2b.coin.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.trade.entity.MessageTransferError;
import com.c2b.coin.trade.exceptions.TradeException;
import com.c2b.coin.trade.mapper.MessageTransferErrorMapper;
import com.c2b.coin.trade.vo.ExchangeVO;
import com.c2b.coin.trade.vo.MatchInfoVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 类说明 ：撮合成功后把交易成功部分数据插入交易记录表，修改委托单表数据，做用户资产变更以及插入用户资产变更表数据
 *
 * @author Anne
 * @date 2017年10月22日
 */
@Component
public class TradeMarriedDeal {

  @Autowired
  OrderLogService orderLogService;

  @Autowired
  MessageTransferErrorMapper messageTransferErrorMapper;

  private static Logger log = LoggerFactory.getLogger(TradeMarriedDeal.class);

  /**
   * 一条成交的交易明细
   * 如果一个委托单有n条成交明细则会读取n次消息
   * @param text 交易明细json串
   */
  @JmsListener(destination = Constants.TRADE_SUCCESS_DEAL_QUEUE_DESTINATION)
  public void receiveQueue(final String text) {
    try {
      log.info("Consumer3 消费者=" + text);
      if (StringUtils.isEmpty(text)) {
        log.error("queue:" + Constants.TRADE_SUCCESS_DEAL_QUEUE_DESTINATION + "返回json串为空！");
        return;
      }
      orderLogService.dealTradeSuccessData(JSONObject.parseObject(text, MatchInfoVO.class));
      Thread.sleep(1000);
    } catch (TradeException e) {
      e.printStackTrace();
      log.error("交易明细消息处理失败：queue【{}】, message【{}】", Constants.TRADE_SUCCESS_DEAL_QUEUE_DESTINATION, text);
      MessageTransferError messageTransferError = new MessageTransferError();
      messageTransferError.setCreateTime(DateUtil.getCurrentTimestamp());
      messageTransferError.setMessageText(text);
      messageTransferError.setType(1);
      messageTransferErrorMapper.insert(messageTransferError);
    } catch (Exception e){
      e.printStackTrace();
      log.error("交易明细消息处理失败：queue【{}】, message【{}】", Constants.TRADE_SUCCESS_DEAL_QUEUE_DESTINATION, text);
      MessageTransferError messageTransferError = new MessageTransferError();
      messageTransferError.setCreateTime(DateUtil.getCurrentTimestamp());
      messageTransferError.setMessageText(text);
      messageTransferError.setType(1);
      messageTransferErrorMapper.insert(messageTransferError);
    }
  }

  /**
   * 订单达到最终状态之后，就不会再有成交信息推送过来，所以这个是1对多的消息，1个委托单号对应0条或n条交易明细
   * @param text 委托单的所有交易记录
   */
  @JmsListener(destination = Constants.TRADE_END_QUEUE_DESTINATION)
  public void endMatch(final String text) {
    try{
      log.info("最终订单结果消费者=" + text);
      if (StringUtils.isEmpty(text)) {
        log.error("queue:" + Constants.TRADE_END_QUEUE_DESTINATION + "返回json串为空！");
        return;
      }
      orderLogService.endMatch(JSONObject.parseObject(text, ExchangeVO.class));
    }catch (TradeException e) {
      e.printStackTrace();
      log.error("终态结果消息处理失败：queue【{}】, message【{}】", Constants.TRADE_END_QUEUE_DESTINATION, text);
      MessageTransferError messageTransferError = new MessageTransferError();
      messageTransferError.setCreateTime(DateUtil.getCurrentTimestamp());
      messageTransferError.setMessageText(text);
      messageTransferError.setType(2);
      messageTransferErrorMapper.insert(messageTransferError);
    } catch (Exception e){
      e.printStackTrace();
      log.error("终态结果消息处理失败：queue【{}】, message【{}】", Constants.TRADE_END_QUEUE_DESTINATION, text);
      MessageTransferError messageTransferError = new MessageTransferError();
      messageTransferError.setCreateTime(DateUtil.getCurrentTimestamp());
      messageTransferError.setMessageText(text);
      messageTransferError.setType(2);
      messageTransferErrorMapper.insert(messageTransferError);
    }

  }

}
