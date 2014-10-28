package com.webex.qd.widget;

import com.webex.qd.util.JsonUtils;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-10-30
 * Time: 下午3:05
 */
public abstract class DefaultWidgetConfiguration implements IWidgetConfiguration {

    @Override
    public IWidgetConfiguration fromJsonString(String json) {
        try {
            return JsonUtils.fromJson(json, this.getClass());
        } catch (IOException ignore) {
        }
        return this;
    }

    @Override
    public String toJsonString() {
        try {
            return JsonUtils.toJson(this);
        } catch (IOException ignore) {
        }
        return "";
    }

}
