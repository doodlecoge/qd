package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.widget.jenkins.GetJenkinsView;
import com.webex.qd.widget.jenkins.View;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: justin
 * Date: 9/4/13
 * Time: 10:56 AM
 */
public class JenkinsViewWidget extends AbstractMultiTabWidget<JenkinsViewWidget.Configuration, JenkinsViewWidget.JenkinsViewWidgetConfigEntry> {

    @Override
    public boolean isLazyLoad() {
        return false;
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    @Override
    public Object loadTabData(JenkinsViewWidgetConfigEntry configEntry) {
        View view = GetJenkinsView.getJenkinsView(configEntry.getViewUrl());
        Data data = new Data();
        data.setConfigEntry(configEntry);
        data.setView(view);
        return data;
    }



    public static final class JenkinsViewWidgetConfigEntry implements IMultiTabWidgetConfigEntry {
        private String displayName;
        private String viewUrl;

        public JenkinsViewWidgetConfigEntry() {
        }

        public JenkinsViewWidgetConfigEntry(String displayName, String viewUrl) {
            this.displayName = displayName;
            this.viewUrl = viewUrl;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getViewUrl() {
            return viewUrl;
        }

        public void setViewUrl(String viewUrl) {
            this.viewUrl = viewUrl;
        }

        @Override
        public boolean isNull() {
            return StringUtils.isBlank(displayName) &&
                    StringUtils.isBlank(viewUrl);
        }
    }

    @JsonIgnoreProperties({"name", "configEntries"})
    public static final class Configuration extends DefaultMultiTabWidgetConfiguration<JenkinsViewWidgetConfigEntry> {

        private List<JenkinsViewWidgetConfigEntry> jenkinsViews = new LinkedList<JenkinsViewWidgetConfigEntry>();

        public Configuration() {
        }

        public List<JenkinsViewWidgetConfigEntry> getJenkinsViews() {
            return jenkinsViews;
        }

        public void setJenkinsViews(List<JenkinsViewWidgetConfigEntry> jenkinsViews) {
            this.jenkinsViews = jenkinsViews;
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        @Override
        public List<JenkinsViewWidgetConfigEntry> getConfigEntries() {
            return getJenkinsViews();
        }

        @Override
        public void setConfigEntries(List<JenkinsViewWidgetConfigEntry> entries) {
            setJenkinsViews(entries);
        }
    }

    public static final class Data {
        private int index;
        private JenkinsViewWidgetConfigEntry configEntry;
        private View view;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public JenkinsViewWidgetConfigEntry getConfigEntry() {
            return configEntry;
        }

        public void setConfigEntry(JenkinsViewWidgetConfigEntry configEntry) {
            this.configEntry = configEntry;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }
    }
}
