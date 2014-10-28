package com.webex.qd.spring;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: huaiwang
 * Date: 8/9/13
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */

public class QdExceptionHandler implements HandlerExceptionResolver, Ordered {
    @Override
    public ModelAndView resolveException(
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Object o,
            Exception e) {


        Map<String, String> model = new HashMap<String, String>();
        model.put("e", getStackTrace(e));
        model.put("error", e.getMessage());
        model.put("url", request.getRequestURL().toString());
        model.put("ctx", request.getContextPath());

        return new ModelAndView("err", model);
    }

    private String getStackTrace(Exception e) {
//        StringBuilder builder = new StringBuilder();
//        StackTraceElement[] trace = e.getStackTrace();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(os);
        e.printStackTrace(stream);
//        for (StackTraceElement aTrace : trace) {
//            builder.append("\tat ").append(aTrace).append("\n");
//        }
        return os.toString();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
