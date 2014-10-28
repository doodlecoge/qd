package com.webex.qd.widget.qddts;

import com.webex.qd.widget.IMultiTabWidgetConfigEntry;
import org.apache.commons.lang.StringUtils;

/**
 * author:Tony Wang
 * version:1.0
 * date:1/9/14
 */
public class QddtsCriteria implements IMultiTabWidgetConfigEntry {
    private String displayName;
    private String advancedQuery;
    private String detailsUrl;

    public QddtsCriteria() {

    }

    public QddtsCriteria(String displayName, String advancedQuery) {
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
