package com.webex.qd.controller;

import com.webex.qd.widget.CdetsAccumulationWidget;
import com.webex.qd.widget.CdetsWidget;
import com.webex.qd.widget.IWidget;
import com.webex.qd.widget.WidgetManager;
import com.webex.qd.widget.cdets.CdetsCriteria;
import com.webex.qd.widget.cdets.CdetsQuery;
import com.webex.qd.widget.cdets.CdetsServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: huaiwang
 * Date: 13-9-4
 * Time: 上午11:59
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/widget/cdets")
public class CdetsAccumulationController {
    Logger log = LoggerFactory.getLogger(CdetsAccumulationController.class);
    @Autowired
    private WidgetManager widgetManager;

    @RequestMapping("/acc")
    @ResponseBody
    public String acc(@RequestParam("wid") int widgetId, @RequestParam("idx") int idx) throws IOException {
        IWidget w = widgetManager.getWidgetById(widgetId);
        if (!(w instanceof CdetsAccumulationWidget)) {
            return "{error: true}";
        }

        CdetsAccumulationWidget widget = (CdetsAccumulationWidget) w;
        CdetsAccumulationWidget.Configuration configuration = widget.getConfiguration();

        if (configuration.getQueries().size() == 0) {
            return "{error: true}";
        }


        List<CdetsQuery> queries = configuration.getQueries();
        int len = queries.size();

        if (len <= idx) {
            return "{error:true}";
        }

        CdetsQuery query = queries.get(idx);
        CdetsServiceClient client = new CdetsServiceClient();
        return client.queryAccumulativeTrend(query.getQuery());
    }

    @RequestMapping("/qddts_ref")
    @ResponseBody
    public String ref(@RequestParam int wid, @RequestParam int idx) throws IOException {
        IWidget w = widgetManager.getWidgetById(wid);
        if (!(w instanceof CdetsAccumulationWidget)) {
            return "{error: true}";
        }

        CdetsAccumulationWidget widget = (CdetsAccumulationWidget) w;
        CdetsAccumulationWidget.Configuration configuration = widget.getConfiguration();

        List<CdetsQuery> queries = configuration.getQueries();

        if (queries == null || queries.size() == 0 || queries.size() <= idx) {
            return "{error: true}";
        }

        CdetsQuery query = queries.get(idx);

        CdetsServiceClient client = new CdetsServiceClient();
        return client.refreshQddts(query.getQuery());
    }


    @RequestMapping("/cdets_ref")
    @ResponseBody
    public String cdets_ref(@RequestParam int wid) throws IOException {
        IWidget w = widgetManager.getWidgetById(wid);
        if (!(w instanceof CdetsWidget)) {
            return "{error: true}";
        }

        CdetsWidget widget = (CdetsWidget) w;
        CdetsWidget.Configuration configuration = widget.getConfiguration();

        List<CdetsCriteria> criterias = configuration.getCriterias();

        if (criterias == null || criterias.size() == 0) {
            return "{error: true}";
        }

        CdetsServiceClient client = new CdetsServiceClient();
        boolean error = false;
        for (CdetsCriteria criteria : criterias) {
            String resp = client.refreshCdets(criteria.getAdvancedQuery());
            if (resp.indexOf("true") != -1) error = true;
        }

        return "{error:" + (error ? "true" : "false") + "}";
    }

    @RequestMapping("/cdets_updated_time")
    @ResponseBody
    public String cdets_updated_time(@RequestParam int wid) throws IOException {
        IWidget w = widgetManager.getWidgetById(wid);
        if (!(w instanceof CdetsWidget)) {
            return "{error: true}";
        }

        CdetsWidget widget = (CdetsWidget) w;
        CdetsWidget.Configuration configuration = widget.getConfiguration();

        List<CdetsCriteria> criterias = configuration.getCriterias();

        if (criterias == null || criterias.size() == 0) {
            return "{error: true}";
        }

        CdetsServiceClient client = new CdetsServiceClient();
        long max = -1;
        boolean err = false;
        for (CdetsCriteria criteria : criterias) {
            String resp = client.cdetsUpdatedTime(criteria.getAdvancedQuery());
            log.error(resp);
            if (resp.equalsIgnoreCase("err")) err = true;
            else {
                long l = Long.parseLong(resp);
                max = max > l ? max : l;
            }
        }

        if (err) return "err";
        else return "" + max;
    }


}
