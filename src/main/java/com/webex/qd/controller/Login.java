package com.webex.qd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 4:03 PM
 */
@Controller
@RequestMapping("/login")
public class Login extends AppBaseController {

    @RequestMapping(method = {RequestMethod.POST})
    public ModelAndView printWelcome(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("process");
        return mv;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String showLoginPage(HttpServletRequest request) {
        String url = request.getParameter("url");
        request.getSession().setAttribute("url", url);
        return "login";
    }
}
