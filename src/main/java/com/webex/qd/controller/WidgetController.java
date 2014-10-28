package com.webex.qd.controller;

import com.webex.qd.api.ApiResult;
import com.webex.qd.api.ErrorResult;
import com.webex.qd.service.DashboardService;
import com.webex.qd.tasche.ApiCaller;
import com.webex.qd.util.JsonUtils;
import com.webex.qd.vo.Widget;
import com.webex.qd.widget.*;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.generic.MathTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 10:43 AM
 */
@Controller
@RequestMapping("/widget")
@Transactional
public class WidgetController {

    @Autowired
    private WidgetManager widgetManager;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ConversionService conversionService;

    @RequestMapping(value = "/view/{widgetId}", method = {RequestMethod.GET})
    public String defaultWidgetView(@PathVariable("widgetId") int widgetId,
                                    @RequestParam(value = "tabIndex", required = false) Integer index,
                                    @RequestParam(value ="isOpenNewTab",required =false) boolean isOpenNewTab,
                                    ModelMap model) {
        IWidget widget = widgetManager.getWidgetById(widgetId);
        int tabIndex = index == null ? 0 : index;
        addDefaultAttributes(model, widget);
        if (widget.getConfiguration().doNullCheck()) {
            return "/widget/empty";
        }

        if (widget instanceof AbstractMultiTabWidget) {
            model.addAttribute("index", tabIndex);
            AbstractMultiTabWidget multiTabWidget = (AbstractMultiTabWidget) widget;
            if (!multiTabWidget.isLazyLoad()) {
                Object data = ((AbstractMultiTabWidget) widget).loadTabData(tabIndex);
                model.addAttribute("data", data);
            }
        } else {
            if (widget instanceof SonarWidget) {
                return "redirect:/widget/sonar/view/" + widgetId;
            }
            Object data = widget.loadData();
            boolean isFromTab =false;

            if(isOpenNewTab){
                isFromTab = true;
            }

            model.addAttribute("data", data);
            model.addAttribute("isFromTab", isFromTab);
        }
        return "/widget/" + widget.getType() + "/view";
    }

    @RequestMapping(value = "/tab/{widgetId}", method = {RequestMethod.GET})
    public String defaultWidgetTabView(@PathVariable("widgetId") int widgetId,
                                       @RequestParam(value = "tabIndex", required = false) Integer index,
                                       ModelMap model) {
        IWidget widget = widgetManager.getWidgetById(widgetId);
        int tabIndex = index == null ? 0 : index;
        addDefaultAttributes(model, widget);
        if (widget instanceof AbstractMultiTabWidget) {
            model.addAttribute("index", tabIndex);
            AbstractMultiTabWidget multiTabWidget = (AbstractMultiTabWidget) widget;
            Object data = multiTabWidget.loadTabData(tabIndex);
            model.addAttribute("data", data);
        }
        return "/widget/" + widget.getType() + "/tab";
    }

    @RequestMapping(value = "/index")
    public String backCompatibility(@RequestParam("id") int widgetId) {
        return "redirect:/dashboard/" + widgetId;
    }


    public static void addDefaultAttributes(ModelMap model, IWidget widget) {
        model.addAttribute("widget", widget);
        model.addAttribute("name", widget.getName());
        model.addAttribute("setting", widget.getConfiguration());
        model.addAttribute("base", ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getContextPath());
        model.addAttribute("math", new MathTool());
        model.addAttribute("rptBaseUrl", "http://ta.webex.com.cn");
    }


    @RequestMapping(value = "/setting/{widgetId}", method = RequestMethod.GET)
    public String defaultWidgetSetting(@PathVariable("widgetId") int widgetId, ModelMap model) {
        IWidget widget = widgetManager.getWidgetById(widgetId);

        addDefaultAttributes(model, widget);
        model.addAttribute("config", widget.getConfiguration());
        try {
            Set<String> projectCodes = ApiCaller.getProjectCodes();
            model.addAttribute("project_codes", JsonUtils.toJson(projectCodes));
        } catch (Exception exception) {
            model.addAttribute("project_codes", "[]");
        }
        return "/widget/" + widget.getType() + "/setting";
    }


    /**
     * Dispatch the request to the specified save handler,
     * according to the widget type
     *
     * @param widgetId id of the widget
     * @param request  http servlet request
     * @return the handler url of the save request
     */
    @RequestMapping(value = "/setting/save", method = RequestMethod.POST)
    public String saveDispatcher(@RequestParam("widgetId") int widgetId, HttpServletRequest request) {
        IWidget widget = widgetManager.getWidgetById(widgetId);
        if (widget instanceof AbstractMultiTabWidget) {
            return "forward:/widget/setting/mtsave";
        } else if (widget instanceof SonarWidget) {
            return "forward:/widget/sonar/setting/save";
        }
        return "forward:/widget/setting/defaultsave";
    }

    @RequestMapping(value = "/setting/mtsave", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody ApiResult defaultMultiTabWidgetSaveSetting(@RequestParam("widgetId") int widgetId, @RequestParam("name") String name, HttpServletRequest request) {
        IWidget widget = widgetManager.getWidgetById(widgetId);
        if (widget == null || !(widget instanceof AbstractMultiTabWidget)) {
            return new ErrorResult("Widget: id=" + widgetId + " not found");
        }
        if (StringUtils.isBlank(name)) {
            return new ErrorResult("Please provide a name for the widget");
        }

        AbstractMultiTabWidget<? extends IMultiTabWidgetConfiguration, ? extends IMultiTabWidgetConfigEntry> multiTabWidget =
                (AbstractMultiTabWidget<? extends IMultiTabWidgetConfiguration, ? extends IMultiTabWidgetConfigEntry>) widget;
        Class<? extends IMultiTabWidgetConfiguration> configurationClass = multiTabWidget.getConfiguration().getClass();
        try {
            ServletRequestDataBinder binder = new ServletRequestDataBinder(configurationClass.newInstance());
            binder.setConversionService(conversionService);
            binder.bind(request);
            BindingResult bindingResult = binder.getBindingResult();
            IMultiTabWidgetConfiguration<IMultiTabWidgetConfigEntry> newConfiguration = (IMultiTabWidgetConfiguration<IMultiTabWidgetConfigEntry>)bindingResult.getTarget();
            List<IMultiTabWidgetConfigEntry> notNullValues = filterNullEntries(newConfiguration.getConfigEntries());
            newConfiguration.setConfigEntries(notNullValues);

            Widget w = dashboardService.getWidgetById(widgetId);
            w.setSetting(newConfiguration.toJsonString());
            w.setName(name);
            dashboardService.saveWidget(w);
        } catch (Exception e) {
            return ApiResult.FAILURE;
        }
        return ApiResult.SUCCESS;
    }

    private List<IMultiTabWidgetConfigEntry> filterNullEntries(List<IMultiTabWidgetConfigEntry> candidates) {
        List<IMultiTabWidgetConfigEntry> notNullValues = new LinkedList<IMultiTabWidgetConfigEntry>();
        if (candidates == null || candidates.size() == 0) {
            return notNullValues;
        }

        for (IMultiTabWidgetConfigEntry candidate : candidates) {
            if (candidate != null && !candidate.isNull()) {
                notNullValues.add(candidate);
            }
        }
        return notNullValues;
    }


    /**
     * The default save handler, if the widget doesn't have a setting save handler.
     * save dispatcher will forward the request to this default save handler
     *
     * @param widgetId id of the widget
     * @param name     name of the widget
     * @param setting  setting (in json) of the widget
     * @return the handle result
     */
    @RequestMapping(value = "/setting/defaultsave", method = RequestMethod.POST)
    public
    @ResponseBody
    ApiResult defaultWidgetSaveSetting(@RequestParam("widgetId") int widgetId,
                                       @RequestParam("name") String name,
                                       @RequestParam("setting") String setting) {
        Widget widget = dashboardService.getWidgetById(widgetId);

        if (widget == null) {
            return new ErrorResult("Widget " + widgetId + " not found");
        }

        IWidget iWidget = widgetManager.getWidgetById(widgetId);

        if (StringUtils.isBlank(name)) {
            return new ErrorResult("Please provide a name for the widget");
        }

        if (StringUtils.isBlank(setting)) {
            setting = "{}";
        }

        ApiResult validateResult = iWidget.validateConfig(setting);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }

        widget.setSetting(setting);
        widget.setName(name);

        dashboardService.saveWidget(widget);

        return ApiResult.SUCCESS;
    }
}
