package com.c2b.util;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
public class InternetUtil {

    private static Logger log = LoggerFactory.getLogger(InternetUtil.class);
    /**
     * 构造函数.
     */
    private InternetUtil() {
    }

    /**
     * 获取客户端IP地址.<br>
     * 支持多级反向代理
     * 
     * @param request
     *            HttpServletRequest
     * @return 客户端真实IP地址
     */
    public static String getRemoteAddr(final HttpServletRequest request) {
        try{
            String remoteAddr = request.getHeader("X-Forwarded-For");
            // 如果通过多级反向代理，X-Forwarded-For的值不止一个，而是一串用逗号分隔的IP值，此时取X-Forwarded-For中第一个非unknown的有效IP字符串
            if (isEffective(remoteAddr) && (remoteAddr.indexOf(",") > -1)) {
                String[] array = remoteAddr.split(",");
                for (String element : array) {
                    if (isEffective(element)) {
                        remoteAddr = element;
                        break;
                    }
                }
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getHeader("X-Real-IP");
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
            return remoteAddr;
        }catch(Exception e){
            log.error("get romote ip error,error message:"+e.getMessage());
            return "";
        }
    }
    
    /**
     * 获取客户端源端口
     * @param request
     * @return
     */
    public static Long getRemotePort(final HttpServletRequest request){
        try{
            String port = request.getHeader("remote-port");
            if(StringUtils.isNotEmpty(port)) {
                try{
                    return Long.parseLong(port);
                }catch(NumberFormatException ex){
                    log.error("convert port to long error , port:   "+port);
                    return 0l;
                }
            }else{
                return 0l;
            }       
        }catch(Exception e){
            log.error("get romote port error,error message:"+e.getMessage());
            return 0l;
        }
    }

    /**
     * 远程地址是否有效.
     * 
     * @param remoteAddr
     *            远程地址
     * @return true代表远程地址有效，false代表远程地址无效
     */
    private static boolean isEffective(final String remoteAddr) {
        boolean isEffective = false;
        if ((null != remoteAddr) && (!"".equals(remoteAddr.trim()))
                && (!"unknown".equalsIgnoreCase(remoteAddr.trim()))) {
            isEffective = true;
        }
        return isEffective;
    }
    
    /** 
     * 获取请求来的ips  
     * @param request 
     * @return 
     */  
     public String getIpAddr(HttpServletRequest request) {  
         String ipAddress = null;  
         ipAddress = request.getHeader("x-forwarded-for");  
         if (ipAddress == null || ipAddress.length() == 0  
                 || "unknown".equalsIgnoreCase(ipAddress)) {  
             ipAddress = request.getHeader("Proxy-Client-IP");  
         }  
         if (ipAddress == null || ipAddress.length() == 0  
                 || "unknown".equalsIgnoreCase(ipAddress)) {  
             ipAddress = request.getHeader("WL-Proxy-Client-IP");  
         }  
         if (ipAddress == null || ipAddress.length() == 0  
                 || "unknown".equalsIgnoreCase(ipAddress)) {  
             ipAddress = request.getRemoteAddr();  
                   
             //这里主要是获取本机的ip,可有可无  
             if (ipAddress.equals("127.0.0.1")  
                     || ipAddress.endsWith("0:0:0:0:0:0:1")) {  
                 // 根据网卡取本机配置的IP  
                 InetAddress inet = null;  
                 try {  
                     inet = InetAddress.getLocalHost();  
                 } catch (UnknownHostException e) {  
                     e.printStackTrace();  
                 }  
                 ipAddress = inet.getHostAddress();  
             }  
           
         }  
           
         // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
         if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()  
                                                             // = 15  
             if (ipAddress.indexOf(",") > 0) {  
                 ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));  
             }  
         }  
          //或者这样也行,对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割    
         //return ipAddress!=null&&!"".equals(ipAddress)?ipAddress.split(",")[0]:null;         
         return ipAddress;  
     } 
}
