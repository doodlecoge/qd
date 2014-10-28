package com.webex.qd.widget;


/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-12
 * Time: 下午2:52
 */
public abstract class DefaultMultiTabWidgetConfiguration<CE extends IMultiTabWidgetConfigEntry>
        extends DefaultWidgetConfiguration
        implements IMultiTabWidgetConfiguration<CE> {

    @Override
    public boolean doNullCheck() {
        return getConfigEntries().isEmpty();
    }
}
