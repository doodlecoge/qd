package com.webex.qd.dao;

import com.webex.qd.vo.Widget;

import java.util.List;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 3:43 PM
 */
public interface WidgetDao {

    Widget getWidget(int id);

    void delete(Widget widget);

    String getProjectMap();

    List<Object[]> getWidgetUsage();

}
