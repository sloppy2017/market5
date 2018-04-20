package com.c2b.coin.matching.vo;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import com.c2b.coin.matching.constant.EnumConsignedType;
import com.c2b.coin.matching.constant.EnumTradeType;
import lombok.Data;

@Data
public class ConsignedVO {
  /**
   * 委托单号
   *
   */
  private long consignedId;

  /**
   * 用户ID
   */
  private long userId;

  /**
   * 创建时间
   */
  private long createTime;

  /**
   * 交易类型 ：买入buy,or卖出sell,or撤单callback
   */
//  @NotNull(message = "tradeType不能为空")
  private EnumTradeType tradeType;

  //commodityCoin/marketCoin
  /**
   * 市场币
   */
  @NotNull(message = "marketCoin不能为空")
  private String marketCoin;

  /**
   * 货物币
   */
  @NotNull(message = "commodityCoin不能为空")
  private String commodityCoin;

  /**
   * 委托数量，市价卖单表示数量，市价买单表示冻结的钱数
   */
  private BigDecimal count = BigDecimal.ZERO;

  /**
   * 委托价格
   */
  private BigDecimal price = BigDecimal.ZERO;

  /**
   * 委托类型：限价交易LIMIT：市价交易MARKET
   */
//  @NotNull(message = "consignedType不能为空")
  private EnumConsignedType consignedType;

}
