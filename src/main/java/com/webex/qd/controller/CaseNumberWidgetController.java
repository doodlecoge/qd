package com.webex.qd.controller;

import com.webex.qd.tasche.ApiCaller;
import com.webex.qd.tasche.TestStats;
import com.webex.qd.widget.CaseNumberWidget;
import com.webex.qd.widget.IWidget;
import com.webex.qd.widget.WidgetManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Author: justin
 * Date: 7/11/13
 * Time: 9:53 AM
 */
@Controller
@RequestMapping("/widget/casenumber")
@Transactional
public class CaseNumberWidgetController {

    @Autowired
    private WidgetManager widgetManager;

    @RequestMapping(value = "/data_provider/{widgetId}", method = {RequestMethod.GET})
    @ResponseBody
    public String cdetsView(@PathVariable("widgetId") int widgetId, HttpServletResponse response) {
        IWidget w = widgetManager.getWidgetById(widgetId);
        JSONObject result = new JSONObject();
        try {
            if (w instanceof CaseNumberWidget) {
                retrieveResult((CaseNumberWidget) w, result);
            }
        } catch (Exception ignore) {}
        return result.toString();
    }


    private void retrieveResult(CaseNumberWidget widget, JSONObject result) throws IOException, DocumentException {
        JSONObject details = new JSONObject();
        JSONArray projectCodes = new JSONArray();

        Map<String, List<TestStats>> map =
                ApiCaller.getProjectPassrate(widget.getConfiguration().getProject_codes(),
                        widget.getConfiguration().getBuilds());

        for (String projectCode : map.keySet()) {
            projectCodes.add(projectCode);
            List<TestStats> stats = map.get(projectCode);
            details.put(projectCode, convert(stats));
        }

        Collection<List<TestStats>> values = map.values();
        int maxLen = 0;
        for (List<TestStats> list : values) {
            if (list != null && list.size() > maxLen) {
                maxLen = list.size();
            }
        }

        JSONArray total = new JSONArray();
        for (int i = 0; i < maxLen; i++) {
            JSONObject element = new JSONObject();
            int totalPassed = 0;
            int totalFailed = 0;
            for (List<TestStats> value : map.values()) {
                if (value.size() > i) {
                    TestStats stat = value.get(i);
                    totalPassed += (stat.getTotalCase() - stat.getFailedCase());
                    totalFailed += (stat.getFailedCase());
                }
            }
            element.put("date", maxLen - i);
            element.put("passed", totalPassed);
            element.put("failed", totalFailed);
            element.put("bn", "N/A");
            total.add(element);
        }

        result.put("TOTAL", total);
        result.put("DETAILS", details);
        result.put("PROJECT_CODES", projectCodes);
    }


    private final SimpleDateFormat OUT_FORMAT = new SimpleDateFormat("MMM/dd");
    private final SimpleDateFormat IN_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private JSONArray convert(List<TestStats> stats) {
        JSONArray array = new JSONArray();
        for (TestStats stat : stats) {
            JSONObject element = new JSONObject();
            String date = null;
            try {
                date = OUT_FORMAT.format(IN_FORMAT.parse(stat.getDateCreated()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            element.put("date", date);
            element.put("passed", stat.getTotalCase() - stat.getFailedCase());
            element.put("failed", stat.getFailedCase());
            element.put("bn", stat.getBuildNumber());
            array.add(element);
        }
        return array;
    }
}
