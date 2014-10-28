package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.apiclient.HttpEngine;
import com.webex.qd.controller.TimsWidgetController;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.dom4j.Document;
import org.dom4j.XPath;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-8
 * Time: 上午9:09
 */
public class TimsAutomationCaseStatsWidget extends AbstractMultiTabWidget<TimsAutomationCaseStatsWidget.Configuration,
        TimsAutomationCaseStatsWidget.ConfigEntry> {

    @Override
    public boolean isLazyLoad() {
        return true;
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    @Override
    public Object loadTabData(ConfigEntry configEntry) {
        return retrieveReport(configEntry);
    }

    private Data retrieveReport(ConfigEntry entry) {
        Data data = new Data();
        data.setConfig(entry);
        data.setAutomated(countSearchHits(entry.getAutomatedSearchId()));
        data.setNotAutomated(countSearchHits(entry.getNotAutomatedSearchId()));
        data.setTobeAutomated(countSearchHits(entry.getTobeAutomatedSearchId()));
        data.calculateAutomationRate();
//        data.setMap(TimsWidgetController.createMap(this.getConfiguration()));
        return data;
    }

    private int countSearchHits(String searchId) {
        HttpEngine engine = new HttpEngine();
        if (StringUtils.isBlank(searchId)) {
            return -1;
        }

        try {
            Document xml = engine.get("http://tims.cisex.com/xml/" + searchId + "/search.svc").getXmlDocument();
            Map<String, String> uris = new HashMap<String, String>();
            uris.put("tims", "http://tims.cisex.com/namespace");
            XPath xpath = xml.createXPath("//tims:SearchHit");
            xpath.setNamespaceURIs(uris);
            List searchHits = xpath.selectNodes(xml);
            return searchHits.size();
        } catch (Exception e) {
            return -1;
        }
    }

    public static final class Data {
        private ConfigEntry config;
        private int automated;
        private int notAutomated;
        private int tobeAutomated;
        private int total;
        private String automationRate = "-";
        private String map;

        public ConfigEntry getConfig() {
            return config;
        }

        public void setConfig(ConfigEntry config) {
            this.config = config;
        }

        public int getAutomated() {
            return automated;
        }

        public void setAutomated(int automated) {
            this.automated = automated;
        }

        public int getNotAutomated() {
            return notAutomated;
        }

        public void setNotAutomated(int notAutomated) {
            this.notAutomated = notAutomated;
        }

        public int getTobeAutomated() {
            return tobeAutomated;
        }

        public void setTobeAutomated(int tobeAutomated) {
            this.tobeAutomated = tobeAutomated;
        }

        public String getAutomationRate() {
            return automationRate;
        }

        public void setAutomationRate(String automationRate) {
            this.automationRate = automationRate;
        }

        public int getTotal() {
            return total;
        }

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }

        protected void calculateAutomationRate() {
            total = 0;
            total += automated <= 0 ? 0 : automated;
            total += tobeAutomated <= 0 ? 0 : tobeAutomated;
            total += notAutomated <= 0 ? 0 : notAutomated;
            if (total > 0 && automated >= 0) {
                float rate = ((float)automated / (float)total) * 100;
                DecimalFormat df = new DecimalFormat("#.0");
                automationRate = df.format(rate) + "%";
            } else {
                automationRate = "-";
            }
        }
    }

    @JsonIgnoreProperties({"name", "configEntries"})
    public static final class Configuration extends DefaultWidgetConfiguration implements IMultiTabWidgetConfiguration<ConfigEntry> {
        private List<ConfigEntry> entries = new LinkedList<ConfigEntry>();

        public List<ConfigEntry> getEntries() {
            return entries;
        }

        public void setEntries(List<ConfigEntry> entries) {
            this.entries = entries;
        }

        @Override
        public List<ConfigEntry> getConfigEntries() {
            return getEntries();
        }

        @Override
        public void setConfigEntries(List<ConfigEntry> entries) {
            setEntries(entries);
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        @Override
        public boolean doNullCheck() {
            return entries.isEmpty();
        }
    }

    public static final class ConfigEntry implements IMultiTabWidgetConfigEntry {
        private String displayName;
        private String automatedSearchId;
        private String notAutomatedSearchId;
        private String tobeAutomatedSearchId;

        public ConfigEntry() {

        }

        public ConfigEntry(String displayName, String automatedSearchId, String notAutomatedSearchId, String tobeAutomatedSearchId) {
            this.displayName = displayName;
            this.automatedSearchId = automatedSearchId;
            this.notAutomatedSearchId = notAutomatedSearchId;
            this.tobeAutomatedSearchId = tobeAutomatedSearchId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getAutomatedSearchId() {
            return automatedSearchId;
        }

        public void setAutomatedSearchId(String automatedSearchId) {
            this.automatedSearchId = automatedSearchId;
        }

        public String getNotAutomatedSearchId() {
            return notAutomatedSearchId;
        }

        public void setNotAutomatedSearchId(String notAutomatedSearchId) {
            this.notAutomatedSearchId = notAutomatedSearchId;
        }

        public String getTobeAutomatedSearchId() {
            return tobeAutomatedSearchId;
        }

        public void setTobeAutomatedSearchId(String tobeAutomatedSearchId) {
            this.tobeAutomatedSearchId = tobeAutomatedSearchId;
        }

        @Override
        public boolean isNull() {
            return StringUtils.isBlank(displayName) &&
                    StringUtils.isBlank(automatedSearchId) &&
                    StringUtils.isBlank(notAutomatedSearchId) &&
                    StringUtils.isBlank(tobeAutomatedSearchId);
        }
    }
}
