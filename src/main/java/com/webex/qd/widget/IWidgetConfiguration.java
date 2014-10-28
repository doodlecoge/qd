package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 4:40 PM
 */
@JsonIgnoreProperties({"name"})
public interface IWidgetConfiguration {
    IWidgetConfiguration fromJsonString(String json);

    String toJsonString();

    ApiResult validate();

    boolean doNullCheck();
}