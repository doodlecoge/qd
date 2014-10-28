package com.webex.qd.widget;

import com.webex.qd.widget.rally.RallyConfiguration;
import com.webex.qd.widget.rally.RallyProject;

/**
 * Created with IntelliJ IDEA.
 * User: huaiwang
 * Date: 7/23/13
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class RallyWidget extends AbstractMultiTabWidget<RallyConfiguration, RallyProject> {

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
