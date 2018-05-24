package com.c2b.coin.api.thread;

/**
 * ThreadContextMap
 *
 * @Auther: tangwei
 * @Date: 2018/5/24
 */
public class ThreadContextMap extends com.c2b.coin.web.common.thread.ThreadContextMap {

  public enum Constants {
    USER_ID,
    USER_NAME
  }

  public void putUserId(String userId) {
    this.put(Constants.USER_ID.name(), userId);
  }

  public String getUserId() {
    return this.get(Constants.USER_ID.name());
  }

  public void putUserName(String userName) {
    this.put(Constants.USER_NAME.name(), userName);
  }

  public String getUserName() {
    return this.get(Constants.USER_NAME.name());
  }

}
