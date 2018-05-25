package com.c2b.coin.api.controller;

import com.c2b.coin.api.thread.ThreadContextMap;

/**
 * BaseController
 *
 * @Auther: tangwei
 * @Date: 2018/5/24
 */
public class BaseController extends com.c2b.coin.web.common.rest.BaseController {

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
