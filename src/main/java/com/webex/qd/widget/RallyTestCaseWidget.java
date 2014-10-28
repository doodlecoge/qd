package com.webex.qd.widget;

import com.webex.qd.widget.rally.RallyConfiguration;
import com.webex.qd.widget.rally.RallyProject;
/**
 * User: huaiwang
 * Date: 13-11-5
 * Time: 上午9:09
 */
public class RallyTestCaseWidget extends AbstractMultiTabWidget<RallyConfiguration, RallyProject> {
    @Override
    public boolean isLazyLoad() {
        return true;
    }

    @Override
    public RallyConfiguration newConfiguration() {
        return new RallyConfiguration();
    }

    @Override
    public Object loadTabData(RallyProject configEntry) {
        return null;
    }
}
