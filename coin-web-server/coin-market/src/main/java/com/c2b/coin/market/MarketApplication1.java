package com.c2b.coin.market;


import com.c2b.coin.market.service.TaskServiceBase;

import java.util.Calendar;

public class MarketApplication1 {
  public static void main(String[] args) {
    Calendar cal = Calendar.getInstance();
    System.out.println(cal.getTimeInMillis());

    cal.add(Calendar.DAY_OF_MONTH , -1);

    System.out.println(cal.getTimeInMillis());

    Calendar cal1 = Calendar.getInstance();
    cal1.add(Calendar.WEEK_OF_YEAR , -1);
     cal1.getTimeInMillis();
    System.out.println(cal1.getTimeInMillis());
    System.out.println("cal1 = " + cal1);

  }
}
