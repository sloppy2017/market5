package com.c2b.coin.matching.vo;

import lombok.Data;

import java.util.List;

@Data
public class ResultVO extends BaseMatchingVO {

  /**
   * 被吃的挂单记录
   */
  private List<MatchingVO> matchResult;

}
