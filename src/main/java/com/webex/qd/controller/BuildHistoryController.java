package com.webex.qd.controller;

import com.webex.qd.tasche.ApiCaller;
import com.webex.qd.tasche.TestStats;
import com.webex.qd.widget.BuildHistoryWidget;
import com.webex.qd.widget.IWidget;
import com.webex.qd.widget.WidgetManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * User: huaiwang
 * Date: 13-9-19
 * Time: 上午9:32
 */

@Controller
@RequestMapping("/widget")
public class BuildHistoryController {
    @Autowired
    private WidgetManager widgetManager;



    @RequestMapping("/bh")
    @ResponseBody
    public String index(int wid) {
        IWidget w = widgetManager.getWidgetById(wid);
        if (!(w instanceof BuildHistoryWidget)) {
            return "{error: true}";
        }

        BuildHistoryWidget widget = (BuildHistoryWidget) w;

        BuildHistoryWidget.Configuration configuration = widget.getConfiguration();

        try {
        Map<String, List<TestStats>> map = ApiCaller.getBuildPassrate(configuration.getProject_codes(),
                configuration.getBuilds(), configuration.getFilter());

        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, List<TestStats>> entry : map.entrySet()) {
            List<TestStats> testStatses = entry.getValue();
            if (testStatses.size() == 0) {
                continue;
            }

            JSONArray jarr = new JSONArray();

            for (TestStats testStatse : testStatses) {
                JSONObject jobj = new JSONObject();
                jobj.put("pc", testStatse.getProjectCode());
                jobj.put("value", testStatse.getTotalCase());
                jobj.put("ff", testStatse.getFailedCase());
                jobj.put("label", testStatse.getDateCreated());

                jarr.add(jobj);
            }

            jsonObject.put(entry.getKey(), jarr);
        }


        return jsonObject.toString();
        } catch (Exception e) {
            return "{error: true}";
        }
    }
}
