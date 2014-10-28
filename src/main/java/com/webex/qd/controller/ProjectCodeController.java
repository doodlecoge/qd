package com.webex.qd.controller;

import com.webex.qd.dao.WidgetDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: huaiwang
 * Date: 8/20/13
 * Time: 9:12 AM
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class ProjectCodeController {
    @Autowired
    private WidgetDao widgetDao;

    @RequestMapping("/pcs")
    public
    @ResponseBody
    String index() {
        return widgetDao.getProjectMap();
    }
}
