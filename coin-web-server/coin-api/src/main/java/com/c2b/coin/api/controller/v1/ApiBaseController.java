package com.c2b.coin.api.controller.v1;

import com.c2b.coin.api.thread.ThreadContextMap;
import com.c2b.coin.web.common.rest.BaseController;

/**
 * ApiBaseController
 *
 * @Auther: tangwei
 * @Date: 2018/5/24
 */
public class ApiBaseController extends BaseController {

  private static ThreadContextMap threadContextMap;

  static {
    init();
  }

  static void init() {
    threadContextMap = null;
    if (threadContextMap == null) {
      threadContextMap = new ThreadContextMap();
    }
  }

  public static ThreadContextMap getThreadContextMap() {
    return threadContextMap;
  }

}
