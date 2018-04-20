package com.c2b.coin.matching.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: Ning.Dai
 * @Date: 2018/3/13 上午10:01
 * @Description:
 */
@Data
public class BaseMatchingVO {
  /**
   * 挂单Id
   */
  private long consignedId;

  /**
   * 挂单是否已完成成交
   */
  private boolean complete;

  /**
   * 完成的量
   */
  private BigDecimal doneCount = BigDecimal.ZERO;

  /**
   * 完成的金额
   */
  private BigDecimal doneValue = BigDecimal.ZERO;
}
