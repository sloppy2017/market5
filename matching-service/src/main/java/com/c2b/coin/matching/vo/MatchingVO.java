package com.c2b.coin.matching.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MatchingVO extends BaseMatchingVO {
  /**
   * 交易唯一序列号 ，全局唯一
   *
   */
  private String seq;

	/**
	 * 成交数量
	 */
	private BigDecimal count;
	/**
	 * 成交的单价
	 */
	private BigDecimal price;

	/**
	 * 成交时间
	 */
	private Long matchTime;
}
