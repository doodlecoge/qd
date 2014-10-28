package com.webex.qd.widget;

import net.sf.json.JSONObject;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 4:50 PM
 */
public abstract class WidgetConfiguration implements IWidgetConfiguration {
    public IWidgetConfiguration fromJsonString(String config) {
        JSONObject json = JSONObject.fromObject(config);
        if (json != null && !json.isNullObject()) {
            return fromJson(json);
        }
        return null;
    }

    @Override
    public String toJsonString() {
        JSONObject json = toJson();
        if (json != null && !json.isNullObject()) {
            return json.toString();
        } else {
            return "{}";
        }
    }

    public abstract IWidgetConfiguration fromJson(JSONObject json);

    public abstract JSONObject toJson();

}
