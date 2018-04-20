package com.c2b.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.c2b.util.InternetUtil;

@Component
public class GlobalInterceptor implements HandlerInterceptor {

    private static Logger log = Logger.getLogger(GlobalInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("utf-8");
        String RequestIP = InternetUtil.getRemoteAddr(request);
        log.info("RequestIP="+RequestIP);
        log.info("RequestURL=" + request.getRequestURI());
        /*String path = request.getServletPath();

        if (path.matches(Const.NO_INTERCEPTOR_PATH)) {
            return true;
        }else{}*/
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

}
