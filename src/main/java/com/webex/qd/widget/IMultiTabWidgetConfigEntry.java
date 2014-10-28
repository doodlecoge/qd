package com.webex.qd.widget;

import com.webex.qd.widget.rally.RallyProject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-12
 * Time: 下午2:45
 */
@JsonIgnoreProperties({"null"})
public interface IMultiTabWidgetConfigEntry {
    boolean isNull();
}
