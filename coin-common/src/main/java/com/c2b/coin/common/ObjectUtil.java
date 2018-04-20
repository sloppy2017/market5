package com.c2b.coin.common;

import com.alibaba.fastjson.JSONObject;

/**
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 * @version 1.0 2016/11/22 11:52
 * @projectname new-pay
 * @packname com.yingu.service.common.util
 */
public class ObjectUtil {
  public static <T> T converyToBean(Object obj, Class<T> c){
    T t = null;
    if ("String".equals(c.getSimpleName())) {
      t = (T)obj;
    } else {
      if (TypeUtil.isJson(obj.toString())) {
        t = (T) JSONObject.parseObject(obj.toString(), c);
      } else if (TypeUtil.isXML(obj.toString())) {
        t = (T) JAXBUtil.converyToBean(obj.toString(), c);
      }
    }
    return t;
  }
}
