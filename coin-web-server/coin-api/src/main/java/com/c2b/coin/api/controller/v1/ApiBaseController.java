package com.c2b.coin.api.controller.v1;

import com.c2b.coin.web.common.rest.BaseController;
import com.c2b.coin.web.common.thread.ThreadContextMap;

/**
 * ApiBaseController
 *
 * @Auther: tangwei
 * @Date: 2018/5/24
 */
public class ApiBaseController extends BaseController {

  private static ThreadContextMap contextMap;

  public enum ThreadContextMapKey {
    USER_ID,
    USER_NAME;
  }

  static {
    init();
  }

  static void init() {
    contextMap = null;
    if (contextMap == null) {
      contextMap = new ThreadContextMap();
    }
  }

  public static void put(ThreadContextMapKey key, String value) {
    contextMap.put(key.name(), value);
  }

  public static String get(ThreadContextMapKey key) {
    return contextMap.get(key.name());
  }

  public static void clearThreadContextMap() {
    contextMap.clear();
  }

}
