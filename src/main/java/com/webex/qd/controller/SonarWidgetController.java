package com.webex.qd.controller;

import com.webex.qd.api.ApiResult;
import com.webex.qd.api.ErrorResult;
import com.webex.qd.service.DashboardService;
import com.webex.qd.vo.Widget;
import com.webex.qd.widget.IWidget;
import com.webex.qd.widget.SonarWidget;
import com.webex.qd.widget.WidgetManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: justin
 * Date: 7/11/13
 * Time: 8:35 AM
 */
@Controller
@RequestMapping("/widget/sonar")
@Transactional
public class SonarWidgetController {

    @Autowired
    private WidgetManager widgetManager;

    @Autowired
    private DashboardService dashboardService;



    @RequestMapping(value = "/view/{widgetId}", method = {RequestMethod.GET})
    public String cdetsView(@PathVariable("widgetId") int widgetId, String projectIndex, @RequestParam(required = false)String cIndex, ModelMap model) {
        IWidget widget = widgetManager.getWidgetById(widgetId);

        int index = 0;
        try {
            index = Integer.valueOf(projectIndex);
        } catch (NumberFormatException ignore) {
        }

        WidgetController.addDefaultAttributes(model, widget);

        if (widget instanceof SonarWidget) {
            SonarWidget sonarWidget = (SonarWidget) widget;
            SonarWidget.Data data;
            if (StringUtils.isBlank(cIndex)) {
                data = sonarWidget.loadData(index);
            } else {
                int cdx = 0;
                try {
                    cdx = Integer.valueOf(cIndex);
                } catch (NumberFormatException ignore) {
                }
                data = sonarWidget.loadChildData(index, cdx);
            }
            model.addAttribute("data", data);

            if (data != null && data.isGroup()) {
                return "/widget/sonar/group";
            }
        }
        return "/widget/sonar/view";
    }

    @RequestMapping(value = "/setting/save", method = RequestMethod.POST)
    public @ResponseBody ApiResult saveSetting(@ModelAttribute FormBean form) {

        Widget widget = dashboardService.getWidgetById(form.getWidgetId());

        if (widget == null) {
            return new ErrorResult("Widget " + form.getWidgetId() + " not found");
        }

        if (StringUtils.isBlank(form.getName())) {
            return new ErrorResult("Please provide a name for the widget");
        }


        SonarWidget.Configuration configuration = new SonarWidget.Configuration();
        List<SonarWidget.SonarConfigEntry> notNullValues = filterNullValues(form.getEntries());
        configuration.setSonars(notNullValues);
        widget.setSetting(configuration.toJsonString());
        widget.setName(form.getName());

        dashboardService.saveWidget(widget);

        return ApiResult.SUCCESS;
    }


    private List<SonarWidget.SonarConfigEntry> filterNullValues(List<SonarWidget.SonarConfigEntry> candidates) {
        List<SonarWidget.SonarConfigEntry> notNullValues = new LinkedList<SonarWidget.SonarConfigEntry>();
        if (candidates == null || candidates.size() == 0) {
            return notNullValues;
        }

        for (SonarWidget.SonarConfigEntry candidate : candidates) {
            if (!candidate.isEmpty()) {
                notNullValues.add(candidate);
            }
        }
        return notNullValues;
    }


    public static final class FormBean extends WidgetSettingForm {
        private LinkedList<SonarWidget.SonarConfigEntry> entries = new LinkedList<SonarWidget.SonarConfigEntry>();

        public LinkedList<SonarWidget.SonarConfigEntry> getEntries() {
            return entries;
        }

        public void setEntries(LinkedList<SonarWidget.SonarConfigEntry> entries) {
            this.entries = entries;
        }
    }
}
