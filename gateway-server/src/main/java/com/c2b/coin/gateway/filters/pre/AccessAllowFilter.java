package com.c2b.coin.gateway.filters.pre;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
/**
 * 增加跨域问题解决
 * @author jianghongyan
 * @version v6.1.1
 * @since 2016.12.11
 */
public class AccessAllowFilter extends ZuulFilter{
	private  Logger logger = Logger.getLogger(getClass());

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.addZuulRequestHeader("Host", toHostHeader(ctx.getRequest()));
		 
		
		//进行跨域支持
		HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
		String origin = RequestContext.getCurrentContext().getRequest().getHeader("origin");
		response.addHeader("Access-Control-Allow-Origin",origin);
        response.addHeader("Access-Control-Allow-Methods","POST, GET, PUT, DELETE, OPTIONS,HEAD");
        response.addHeader("Access-Control-Max-Age","3600");
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Allow-Credentials","true");
     

		return null;
	}
	
	 private String toHostHeader(HttpServletRequest request) {
	        int port = request.getServerPort();
	        if ((port == 80 && "http".equals(request.getScheme()))
	                || (port == 443 && "https".equals(request.getScheme()))) {
	            return request.getServerName();
	        }
	        else {
	            return request.getServerName() + ":" + port;
	        }
	 }

	@Override
	public boolean shouldFilter() {
		// 判断过滤器是否执行
		return true;
	}

	@Override
	public int filterOrder() {
		//返回值代表filter执行顺序
		return 0;
	}

	@Override
	public String filterType() {
		//		pre：可以在请求被路由之前调用
		//		routing：在路由请求时候被调用
		//		post：在routing和error过滤器之后被调用
		//		error：处理请求时发生错误时被调用
		return "pre";
	}

}
