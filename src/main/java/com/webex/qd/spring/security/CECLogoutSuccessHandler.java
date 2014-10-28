package com.webex.qd.spring.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: huaiwang
 * Date: 13-10-14
 * Time: 上午9:16
 */
public class CECLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        String url = request.getParameter("url");
        if(url != null) {
            httpServletResponse.sendRedirect(url);
        } else {
            httpServletResponse.sendRedirect(request.getContextPath());
        }
    }
}
