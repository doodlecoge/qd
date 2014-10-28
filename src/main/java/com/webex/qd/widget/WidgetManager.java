package com.webex.qd.widget;

import com.webex.qd.dao.WidgetDao;
import com.webex.qd.vo.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 4:47 PM
 */
@Component
@Scope(value = "singleton")
public class WidgetManager {
    private Map<String, Class> widgetTypes = new HashMap<String, Class>();

    @Autowired
    private WidgetDao widgetDao;

    public WidgetManager() {
        widgetTypes.put(WidgetType.PRODUCT.getKey(), ProductWidget.class);
        widgetTypes.put(WidgetType.NOT_FOUND.getKey(), NotFoundWidget.class);
        widgetTypes.put(WidgetType.PRODUCT_TRENDS.getKey(), ProductTrendsWidget.class);
        widgetTypes.put(WidgetType.PRODUCT_DIFFER.getKey(), ProductDifferWidget.class);
        widgetTypes.put(WidgetType.PRODUCT_W_PKG.getKey(), ProductWPkgWidget.class);
        widgetTypes.put(WidgetType.PRODUCT_DIFFER_W_PKG.getKey(), ProductDifferWPkgWidget.class);
        widgetTypes.put(WidgetType.CASE_NUMBER.getKey(), CaseNumberWidget.class);
        widgetTypes.put(WidgetType.BUILD_HISTORY.getKey(), BuildHistoryWidget.class);
        widgetTypes.put(WidgetType.CDETS.getKey(), CdetsWidget.class);
        widgetTypes.put(WidgetType.LATEST_BUILD.getKey(), LatestBuildWidget.class);
        widgetTypes.put(WidgetType.SONAR.getKey(), SonarWidget.class);
        widgetTypes.put(WidgetType.CDETS_TREND.getKey(), CdetsTrendWidget.class);
        widgetTypes.put(WidgetType.TIMS_REPORT.getKey(), TimsReportWidget.class);
        widgetTypes.put(WidgetType.TIMS_REPORT_TABLE.getKey(), TimsReportTableWidget.class);
        widgetTypes.put(WidgetType.RALLY.getKey(), RallyWidget.class);
        widgetTypes.put(WidgetType.RALLY_DEFECT.getKey(), RallyDefectWidget.class);
        widgetTypes.put(WidgetType.RALLY_DEFECT_CUMULATIVE.getKey(), RallyDefectCumulativeWidget.class);
        widgetTypes.put(WidgetType.RALLY_TEST_CASE.getKey(), RallyTestCaseWidget.class);
        widgetTypes.put(WidgetType.CDETS_ACCUMULATION.getKey(), CdetsAccumulationWidget.class);
        widgetTypes.put(WidgetType.JENKINS_VIEW.getKey(), JenkinsViewWidget.class);
        widgetTypes.put(WidgetType.TIMS_AUTOMATION_CASE_STATS.getKey(), TimsAutomationCaseStatsWidget.class);
        widgetTypes.put(WidgetType.EMBEDDED.getKey(),EmbeddedWidget.class);
        widgetTypes.put(WidgetType.QDDTS.getKey(),QddtsWidget.class);
        widgetTypes.put(WidgetType.DAILY_CHANGES.getKey(),DailyChangesWidget.class);
        widgetTypes.put(WidgetType.PROJECT_TOTAL.getKey(),ProjectTotalWidget.class);
    }



    public IWidget delegate(Widget widget) {
        Class clazz = widgetTypes.get(widget.getType());
        if (clazz == null) {
            return new NotFoundWidget();
        }

        try {
            IWidget w = (IWidget) clazz.newInstance();
            w.copyFromWidget(widget);
            return w;
        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        }
        return new NotFoundWidget();
    }

    public boolean isValidType(String type) {
        return widgetTypes.keySet().contains(type);
    }

    public List<WidgetType> getActiveWidgetTypes() {
        List<WidgetType> typeList = new ArrayList<WidgetType>();
        WidgetType[] types = WidgetType.values();
        for (WidgetType type : types) {
            if (!type.isDeprecate()) {
                typeList.add(type);
            }
        }
        return typeList;
    }

    public IWidget getWidgetById(int widgetId) {
        Widget w = widgetDao.getWidget(widgetId);
        if (w != null) {
            return delegate(w);
        } else {
            return new NotFoundWidget();
        }
    }
}

