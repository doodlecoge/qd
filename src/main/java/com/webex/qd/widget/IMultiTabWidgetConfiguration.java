package com.webex.qd.widget;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-12
 * Time: 下午2:17
 */
public interface IMultiTabWidgetConfiguration<E extends IMultiTabWidgetConfigEntry> extends IWidgetConfiguration {
    public List<E> getConfigEntries();

    public void setConfigEntries(List<E> entries);
}
