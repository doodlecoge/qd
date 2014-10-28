package com.webex.qd.controller;

import com.webex.qd.exception.QdRtException;
import com.webex.qd.exception.QualityDashboardException;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: huaiwang
 * Date: 8/9/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/err")
public class ErrorController {
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        throw new QdRtException("err" + sdf.format(cal.getTime()));
    }
}
