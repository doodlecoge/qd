package com.webex.qd.controller;

import com.webex.qd.tasche.ApiCaller;
import com.webex.qd.tasche.TestStats;
import com.webex.qd.widget.IWidget;
import com.webex.qd.widget.ProjectTotalWidget;
import com.webex.qd.widget.WidgetManager;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenshijie on 7/15/14.
 */
@Controller
@RequestMapping("/widget/project_total")
@Transactional
public class ProjectTotalController {

    @Autowired
    private WidgetManager widgetManager;

    @RequestMapping("/data")
    public @ResponseBody
    String getData(@RequestParam("widgetId") int widgetId,
                   @RequestParam(value = "from", required = false) Date beginDate,
                   @RequestParam(value = "to", required = false) Date endDate) {
        IWidget widget = widgetManager.getWidgetById(widgetId);

        if(!(widget instanceof ProjectTotalWidget)) {
            return "{}";
        }

        ProjectTotalWidget w = (ProjectTotalWidget) widget;

        ProjectTotalWidget.Configuration conf = w.getConfiguration();

        try {
            JSONObject passrates = getPassrates(conf, beginDate, endDate);
            JSONObject coverages = getCoverages(conf, beginDate, endDate);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("config", conf.getConfig());
            jsonObject.put("passrates", passrates);
            jsonObject.put("coverages", coverages);
            return jsonObject.toString();
        } catch (Exception e) {
            return "{}";
        }

    }

    private JSONObject getPassrates(ProjectTotalWidget.Configuration conf, Date beginDate, Date endDate) throws IOException, DocumentException {
        Set<String> projectCodes = conf.getAllProjectCodes();
        Map<String,List<TestStats>> allPassrates = ApiCaller.getProjectPassrate(projectCodes, 0, beginDate, endDate);

        JSONObject passrates = new JSONObject();

        for (Map.Entry<String, List<TestStats>> entry  : allPassrates.entrySet()) {
            JSONArray jarr = new JSONArray();

            List<TestStats> statses = entry.getValue();
            for (TestStats statse : statses) {
                JSONObject singlePassrate = new JSONObject();
                singlePassrate.put("build_number", statse.getBuildNumber());
                singlePassrate.put("last_modify_time", statse.getDateCreated());
                singlePassrate.put("total", statse.getTotalCase());
                singlePassrate.put("fail", statse.getFailedCase());
                jarr.add(singlePassrate);
            }

            if(jarr.size() > 0) {
                passrates.put(entry.getKey(), jarr);
            }
        }

        return passrates;
    }

    private JSONObject getCoverages(ProjectTotalWidget.Configuration conf, Date beginDate, Date endDate) throws IOException, DocumentException {
        Set<String> projectCodes = conf.getAllProjectCodes();
        Map<String,List<TestStats>> allCoverages = ApiCaller.getCoverages(projectCodes, 1, beginDate, endDate);

        JSONObject coverages = new JSONObject();

        for (Map.Entry<String, List<TestStats>> entry  : allCoverages.entrySet()) {
            JSONArray jarr = new JSONArray();

            List<TestStats> statses = entry.getValue();
            for (TestStats statse : statses) {
                JSONObject singlePassrate = new JSONObject();
                singlePassrate.put("build_number", statse.getBuildNumber());
                singlePassrate.put("last_modify_time", statse.getDateCreated());
                singlePassrate.put("total", statse.getTotalCase());
                singlePassrate.put("fail", statse.getFailedCase());
                singlePassrate.put("url", statse.getUrl());
                jarr.add(singlePassrate);
            }

            if(jarr.size() > 0) {
                coverages.put(entry.getKey(), jarr);
            }
        }

        return coverages;
    }
}
