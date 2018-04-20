package com.c2b.coin.common;

import java.util.Random;

/**
 * @author MMM
 * @desc 描述
 * @project delicacy-cloud
 * @date 2017-02-24 13:42
 **/
public class RandNumUtil {
  public static String getRandNum(int charCount) {
    String charValue = "";
    for (int i = 0; i < charCount; i++) {
      char c = (char) (randomInt(0, 10) + '0');
      charValue += String.valueOf(c);
    }
    return charValue;
  }

  public static int randomInt(int from, int to) {
    Random r = new Random();
    return from + r.nextInt(to - from);
  }
}
