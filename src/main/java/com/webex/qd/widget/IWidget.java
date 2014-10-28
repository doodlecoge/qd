package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.api.ErrorResult;
import com.webex.qd.vo.Widget;
import net.sf.json.JSONException;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 4:39 PM
 */
public abstract class IWidget<T extends IWidgetConfiguration> extends Widget {

    private T configuration;


    public void copyFromWidget(Widget widget) {
        setSetting(widget.getSetting());
        setId(widget.getId());
        setDashboardId(widget.getDashboardId());
        setType(widget.getType());
        setName(widget.getName());

        T configuration = this.newConfiguration();
        if (configuration != null) {
            this.configuration = (T) configuration.fromJsonString(widget.getSetting());
        }
    }

    public ApiResult validateConfig(String jsonString) {
        try {
            IWidgetConfiguration conf = configuration.fromJsonString(jsonString);
            return conf.validate();
        } catch (JSONException e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
    }

    public T getConfiguration() {
        return configuration;
    }

    public void setConfiguration(T configuration) {
        this.configuration = configuration;
    }

    public abstract T newConfiguration();

    public abstract Object loadData();

}

