package com.c2b;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RandomId {
  public static void main(String[] args) {
    String str = "C".concat(String.format("%1$ty%2$s%3$05d", new Date(), 123, 123));
    System.out.println(str);
  }
}
