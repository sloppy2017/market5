package com.c2b.coin.common;

import java.math.BigDecimal;
import java.util.Random;

public class MarketingRandomCoinNumber {

//  private static final Random random = new Random();

  private static final BigDecimal maxBTCNum = new BigDecimal(0.00015);
  private static final BigDecimal minBTCNum = new BigDecimal(0.0001);

  private static final BigDecimal maxETHNum = new BigDecimal(0.004);
  private static final BigDecimal minETHNum = new BigDecimal(0.003);

  private static final BigDecimal toIntegerNum = new BigDecimal(1000000);

  private static final int generateRandom(int max ,int min){
    int num = new Random().nextInt(max)%(max-min+1) + min;
    return num;
  }

  public static final BigDecimal generateBTCNumber(){
    int randomNum =  MarketingRandomCoinNumber.generateRandom(maxBTCNum.multiply(toIntegerNum).intValue(),minBTCNum.multiply(toIntegerNum).intValue());
    return new BigDecimal(randomNum).divide(toIntegerNum);
  }

  public static final BigDecimal generateETHNumber(){
    int randomNum =  MarketingRandomCoinNumber.generateRandom(maxETHNum.multiply(toIntegerNum).intValue(),minETHNum.multiply(toIntegerNum).intValue());
    return new BigDecimal(randomNum).divide(toIntegerNum);
  }

  public static void main(String[] args) {
    for (int i = 0 ; i < 3000 ; i++){
      BigDecimal num = MarketingRandomCoinNumber.generateBTCNumber();
//      System.out.println("ETH 0.003-0.004ETH（小数点后6位）:"+MarketingRandomCoinNumber.generateETHNumber());
      if(num.compareTo(new BigDecimal(0.00015))>0){
        System.out.println(num );
      }else{
        System.out.println(i );
      }
    }

  }
}
