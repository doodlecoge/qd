package com.webex.qd.widget;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 4:38 PM
 */
public class NotFoundWidget extends IWidget {

    @Override
    public String getType() {
        return WidgetType.NOT_FOUND.getKey();
    }

    @Override
    public IWidgetConfiguration newConfiguration() {
        return null;
    }

    @Override
    public Object loadData() {
        return null;
    }

    @Override
    public String getName() {
        return "NOT FOUND";
    }
}
