package com.webex.qd.widget;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-12
 * Time: 下午2:09
 */
public abstract class AbstractMultiTabWidget<T extends IMultiTabWidgetConfiguration<CE>, CE extends IMultiTabWidgetConfigEntry> extends IWidget<T> {
    @Override
    public Object loadData() {
        return loadTabData(0);
    }

    public abstract boolean isLazyLoad();

    public Object loadTabData(int tabIndex) {
        T config = this.getConfiguration();

        if (config == null) {
            return null;
        }

        List<CE> configEntries = config.getConfigEntries();
        if (configEntries == null || configEntries.size() == 0) {
            return null;
        }

        if (tabIndex >= configEntries.size() || tabIndex < 0) {
            return null;
        }

        CE entry = configEntries.get(tabIndex);
        if (entry == null) {
            return null;
        }

        return loadTabData(entry);
    }

    public abstract Object loadTabData(CE configEntry);
}
