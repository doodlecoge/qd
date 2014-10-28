package com.webex.qd.controller;

/**
 * Author: justin
 * Date: 7/15/13
 * Time: 2:45 PM
 */
public abstract class WidgetSettingForm {
    private int widgetId;
    private String name;

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}