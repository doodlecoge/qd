package com.webex.qd.controller;

import com.webex.qd.apiclient.HttpEngine;
import com.webex.qd.util.QdProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: huaiwang
 * Date: 7/31/13
 * Time: 10:52 AM
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/widget/rally")
public class RallyController {

    @RequestMapping("/bd/{pc}")
    @ResponseBody
    public String getBurnDownChart(@PathVariable("pc") String projectCode) {
        String url = QdProperties.getProperty("qd-rally-rs-url");
        HttpEngine engine = new HttpEngine();
        String html = "{\"error\": true}";
        try {
            html = engine.get(url + projectCode).getHtml();
        } catch (IOException e) {
        }

        return html;
    }

}
