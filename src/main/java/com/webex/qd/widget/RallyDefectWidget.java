package com.webex.qd.widget;

import com.webex.qd.widget.rally.RallyConfiguration;
import com.webex.qd.widget.rally.RallyProject;

/**
 * User: huaiwang
 * Date: 13-10-18
 * Time: 下午2:07
 */
public class RallyDefectWidget extends AbstractMultiTabWidget<RallyConfiguration, RallyProject> {

    @Override
    public boolean isLazyLoad() {
        return true;
    }

    @Override
    public Object loadTabData(RallyProject configEntry) {
        return null;
    }

    @Override
    public RallyConfiguration newConfiguration() {
        return new RallyConfiguration();
    }
}
