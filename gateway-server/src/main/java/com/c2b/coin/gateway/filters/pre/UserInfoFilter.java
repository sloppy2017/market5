package com.c2b.coin.gateway.filters.pre;

import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.Constants;
import com.c2b.coin.gateway.util.UserAgent;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import cz.mallat.uasparser.UserAgentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class UserInfoFilter extends ZuulFilter {
  private Logger logger = LoggerFactory.getLogger(UserInfoFilter.class);
  @Resource(name = "redisTemplate")
  private RedisTemplate<Object, Object> redisTemplate;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 1;
  }
  @Autowired
  Environment environment;

  @Override
  public boolean shouldFilter() {
    HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
    UserAgentInfo uai = UserAgent.userAgent(request);
    logger.error("request:{}", request.getHeader("user-agent"));
    logger.info("操作系统家族：" + uai.getOsFamily());
    logger.info("操作系统详细名称：" + uai.getOsName());
    logger.info("浏览器名称和版本:" + uai.getUaName());
    logger.info("类型：" + uai.getType());
    logger.info("浏览器名称：" + uai.getUaFamily());
    logger.info("浏览器版本：" + uai.getBrowserVersionInfo());
    logger.info("设备类型：" + uai.getDeviceType());
    return true;
  }

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
    HttpSession httpSession = request.getSession(false);
    String servletPath = request.getRequestURI();
    if (servletPath.contains("/api/account/client")) {
      return failure(ctx, 401);
    }
    if (servletPath.contains("/nologin")) {
      return null;
    }
    UserAgentInfo uai = UserAgent.userAgent(request);
    logger.info("操作系统家族：" + uai.getOsFamily());
    logger.info("操作系统详细名称：" + uai.getOsName());
    logger.info("浏览器名称和版本:" + uai.getUaName());
    logger.info("类型：" + uai.getType());
    logger.info("浏览器名称：" + uai.getUaFamily());
    logger.info("浏览器版本：" + uai.getBrowserVersionInfo());
    logger.info("设备类型：" + uai.getDeviceType());
    if (uai.getType().toLowerCase().contains("browser")) {
      return web(ctx, request, httpSession);
    } else {
      return app(ctx, request);
    }
  }

  public boolean verfiyUrl(String path) {
    return path.contains("/google/bind") || path.contains("/google/unbind") || path.contains("/mobile/bind") || path.contains("/mobile/unbind") || path.contains("sms-verify-code") ||
      path.contains("sms-verify-code") || path.contains("send-second-valid-sms") || path.contains("logout") || path.contains("/google-verify-code") || path.contains("/account/activity/");
  }

  public Object failure(RequestContext ctx, int code) {
    ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由
    ctx.setResponseStatusCode(code);// 返回错误码
    ctx.setResponseBody("{\"data\": {},\"error\": {\"code\": \"" + code + "\",\"detail\": \"userInfo  not exist!\"},\"success\": false}");
    ctx.set("isSuccess", true);
    return null;
  }

  public Object success(RequestContext ctx, boolean isRouter) {
    ctx.setSendZuulResponse(isRouter);// 过滤该请求，不对其进行路由
    ctx.setResponseStatusCode(200);// 返回错误码
    ctx.set("isSuccess", true);
    return null;
  }


  private Object web(RequestContext ctx, HttpServletRequest request, HttpSession httpSession) {
    logger.info("===================================GOTO WEB FILTER !!!!!!==========================");
    if (null == httpSession) {
      return failure(ctx, 401);
    }
    String userinfo = String.valueOf(httpSession.getAttribute("user"));
    logger.info("=====================ddd========" + userinfo);
    if (!environment.acceptsProfiles("dev")) {
      String referer = request.getHeader("Referer");
      if ((referer == null) || !(referer.trim().startsWith("https://www.PARK.ONE.com"))) {
        return failure(ctx, 401);
      }
    }
    if (StringUtils.isEmpty(userinfo)) {
      return failure(ctx, 401);
    }
    if (StringUtils.isEmpty(userinfo) || "null".equals(userinfo)) {
      return failure(ctx, 401);
    }
    String secondValid = stringRedisTemplate.opsForValue().get(Constants.REDIS_USER_LOGIN_SECOND_VERFIY_KEY + httpSession.getId());
    String sessionToken = String.valueOf(httpSession.getAttribute("token"));

    String token = request.getHeader("token");
    if (!sessionToken.equals(token)){
      return failure(ctx, 401);
    }
    if (StringUtils.isEmpty(secondValid)) {
      secondValid = stringRedisTemplate.opsForValue().get(Constants.REDIS_USER_LOGIN_SECOND_VERFIY_KEY + token);
    }
    String servletPath = request.getRequestURI();
    JSONObject user = JSONObject.parseObject(userinfo);
    String twoValid = user.getString("twoValid");
    if (validSecond(twoValid, secondValid, servletPath)) {
      return failure(ctx, 450);
    }
    return success(ctx, user, token);
  }

  private Object app(RequestContext ctx, HttpServletRequest request) {
    String token = request.getHeader(Constants.HTTP_HEADER_TOKEN);
    logger.info("===================================GOTO WEB FILTER TOKEN {}!!!!!!==========================", token);
    if (StringUtils.isEmpty(token)) {
      return failure(ctx, 401);
    }
    String userInfoJson = stringRedisTemplate.opsForValue().get(Constants.REDIS_USER_TKOEN_KEY + token);
    String secondValid = stringRedisTemplate.opsForValue().get(Constants.REDIS_USER_LOGIN_SECOND_VERFIY_KEY + token);
    if (StringUtils.isEmpty(userInfoJson) || "null".equals(userInfoJson)) {
      return failure(ctx, 401);
    }
    String servletPath = request.getRequestURI();
    JSONObject user = JSONObject.parseObject(userInfoJson);
    String twoValid = user.getString("twoValid");
    if (validSecond(twoValid, secondValid, servletPath)) {
      return failure(ctx, 450);
    }
    return success(ctx, user, token);
  }

  private boolean validSecond(String twoValid, String secondValid, String servletPath) {
    return (!StringUtils.isEmpty(twoValid) && "1".equals(twoValid)) && (StringUtils.isEmpty(secondValid) || !"1".equals(secondValid)) && !verfiyUrl(servletPath);
  }

  private Object success(RequestContext ctx, JSONObject user, String token) {
    Integer userId = user.getInteger("id");
    String username = user.getString("username");
    RequestContext.getCurrentContext().getZuulRequestHeaders().put(Constants.HTTP_HEADER_USER_ID, String.valueOf(userId));
    RequestContext.getCurrentContext().getZuulRequestHeaders().put(Constants.HTTP_HEADER_USER_NAME, username);
    RequestContext.getCurrentContext().getZuulRequestHeaders().put(Constants.HTTP_HEADER_TOKEN, token);
    logger.info("===================================GET USER {} !!!!!!==========================", userId);
    return success(ctx, true);
  }
}
