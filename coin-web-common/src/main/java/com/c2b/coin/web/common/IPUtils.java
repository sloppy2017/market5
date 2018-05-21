package com.c2b.coin.web.common;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {
  
  private static Logger logger = LoggerFactory.getLogger(IPUtils.class);

  /**
   * 获取IP地址
   * <p>
   * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
   * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
   */
  public static String getIpAddr(HttpServletRequest request) {
    String ip = null;
    try {
      ip = request.getHeader("x-forwarded-for");
      if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("Proxy-Client-IP");
      }
      if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("WL-Proxy-Client-IP");
      }
      if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_CLIENT_IP");
      }
      if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
      }
      if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getRemoteAddr();
      }
    } catch (Exception e) {
      logger.error("IPUtils ERROR ", e);
    }
    return ip;
  }

  /**
   * 获取用户的真实IP，取X-Forwarded-For中第一个非unknown的有效IP字符串
   *
   * @param request
   * @return
   */
  public static String getRealIpAddress(HttpServletRequest request) {
    String ip = getIpAddr(request);
    if (ip != null && ip.length() > 0 && ip.contains(",")) {
      return getNotUnknown(ip.split(","), 0);
    }
    return ip;
  }

  private static String getNotUnknown(String[] ipAddress, int start) {
    if (ipAddress != null && start < ipAddress.length) {
      String ip = ipAddress[start];
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        return getNotUnknown(ipAddress, ++start);
      }
      return ip;
    }
    return "";
  }

}
