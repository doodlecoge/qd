package com.webex.qd.spring.interceptor;

import com.webex.qd.service.DashboardService;
import com.webex.qd.spring.security.AuthUtil;
import com.webex.qd.vo.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 5:01 PM
 */
public class AuthenticationInfoInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private DashboardService dashboardService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.getSession().setAttribute("base", request.getContextPath());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String username = AuthUtil.getUsername();
        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("principal", AuthUtil.getPrincipal());
        List<Workspace> workspaces = dashboardService.listWorkspaces();
        request.getSession().setAttribute("GLOBAL_WORKSPACES", workspaces);
        request.getSession().setAttribute("GLOBAL_CURRENT_WORKSPACE", getWorkspaceFromCookies(request));
    }




    private String getWorkspaceFromCookies(HttpServletRequest request) {
         Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("workspace".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
