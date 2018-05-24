package com.c2b.coin.web.common.thread;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ThreadContextMap
 *
 * @Auther: tangwei
 * @Date: 2018/5/24
 */
public class ThreadContextMap {

  protected final ThreadLocal<Map<String, String>> localMap;

  public ThreadContextMap() {
    this.localMap = new ThreadLocal<>();
  }

  public void put(final String key, final String value) {
    Map<String, String> map = localMap.get();
    map = map == null ? new HashMap<>() : new HashMap<>(map);
    map.put(key, value);
    localMap.set(Collections.unmodifiableMap(map));
  }

  public String get(final String key) {
    final Map<String, String> map = localMap.get();
    return map == null ? null : map.get(key);
  }

  public void remove(final String key) {
    final Map<String, String> map = localMap.get();
    if (map != null) {
      final Map<String, String> copy = new HashMap<>(map);
      copy.remove(key);
      localMap.set(Collections.unmodifiableMap(copy));
    }
  }

  public void clear() {
    localMap.remove();
  }

}
