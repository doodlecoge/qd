package com.webex.qd.widget.cdets;

import com.webex.qd.widget.IMultiTabWidgetConfigEntry;
import org.apache.commons.lang.StringUtils;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 4:00 PM
 */
public class CdetsCriteria implements IMultiTabWidgetConfigEntry {
    private String displayName;
    private String advancedQuery;
    private String detailsUrl;

    public CdetsCriteria() {

    }

    public CdetsCriteria(String displayName, String advancedQuery) {
        this.displayName = displayName;
        this.advancedQuery = advancedQuery;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAdvancedQuery() {
        return advancedQuery;
    }

    public void setAdvancedQuery(String advancedQuery) {
        this.advancedQuery = advancedQuery;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    @Override
    public boolean isNull() {
        return StringUtils.isBlank(displayName) &&
                StringUtils.isBlank(advancedQuery) &&
                StringUtils.isBlank(detailsUrl);
    }
}

