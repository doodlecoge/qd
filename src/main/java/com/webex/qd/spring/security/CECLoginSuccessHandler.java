package com.webex.qd.spring.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: huaiwang
 * Date: 13-10-14
 * Time: 上午9:05
 */
public class CECLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Object url = request.getSession().getAttribute("url");
        request.getSession().removeAttribute("url");
        if (url != null) {
            response.sendRedirect(url.toString());
        } else {
            response.sendRedirect(request.getContextPath());
        }
    }
}
