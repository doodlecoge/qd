package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.widget.sonar.SonarResourceMerger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.HttpClient4Connector;
import org.sonar.wsclient.services.*;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 4:02 PM
 */
public class SonarWidget extends IWidget<SonarWidget.Configuration> {

    @Override
    public Object loadData() {
        return loadData(0);
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    public Data loadData(SonarConfigEntry configEntry) {
        Data data = new Data();
        if (configEntry.hasChild()) {
            fillGroupDataWithSonars(configEntry.getGroups(), data);
        } else {
            fillDataWithSonar(configEntry, data);
        }

        Configuration config = getConfiguration();
        for (SonarConfigEntry entry : config.sonars) {
            data.sonars.add(entry);
        }

        data.selected = configEntry;
        return data;
    }

    public Data loadData(int index) {
        Configuration config = getConfiguration();

        if (config.sonars.size() == 0) {
            return null;
        }

        if (index >= config.sonars.size() || index < 0) {
            index = 0;
        }

        return loadData(config.sonars.get(index));
    }

    public Data loadChildData(int parentIndex, int childIndex) {
        Configuration config = getConfiguration();
        if (config.sonars.size() == 0) {
            return null;
        }

        if (parentIndex >= config.sonars.size() || parentIndex < 0) {
            return null;
        }

        SonarConfigEntry parent = config.sonars.get(parentIndex);
        if (parent.hasChild()) {
            if (childIndex > parent.groups.size() || childIndex < 0) {
                return null;
            }
            SonarConfigEntry child = parent.groups.get(childIndex);
            Data data = loadData(child);
            data.child = child;
            data.selected = parent;
            return data;
        } else {
            return loadData(parent);
        }
    }

    private void fillGroupDataWithSonars(List<SonarConfigEntry> entries, Data data) {
        SonarResourceMerger merger = new SonarResourceMerger();
        for (SonarConfigEntry entry : entries) {
            Resource resource = loadSonarResource(entry);
            if (resource != null) {
                merger.merge(resource);
            }
        }
        data.isGroup = true;
        data.resource = new ResourceAdapter(merger.getMergedResource());
    }

    private void fillDataWithSonar(SonarConfigEntry configEntry, Data data) {
        Sonar sonar = new Sonar(new HttpClient4Connector(new Host(configEntry.url)));
        try {
            TimeMachine timeMachine = sonar.find(TimeMachineQuery.createForMetrics(String.valueOf(configEntry.resourceId), "ncloc", "coverage", "tests"));

            data.formattedTimeMachine = generateFormattedTimeMachineData(timeMachine);
            data.timeMachineSnapshots = generateFromattedTimeMachineSnapshots(timeMachine);
            Resource struts = loadSonarResource(configEntry);
            data.resource = new ResourceAdapter(struts);
        } catch (Exception e) {
            data.resource = new ResourceAdapter(null);
        }
    }

    private Resource loadSonarResource(SonarConfigEntry configEntry) {
        Sonar sonar = new Sonar(new HttpClient4Connector(new Host(configEntry.url)));
        return sonar.find(ResourceQuery.createForMetrics(String.valueOf(configEntry.resourceId),
                "ncloc", "lines", "statements", "files",
                "classes", "packages", "functions", "accessors",
                "function_complexity", "class_complexity", "file_complexity", "complexity",
                "violations", "violations_density", "blocker_violations", "critical_violations", "major_violations", "minor_violations", "info_violations",
                "technical_debt", "technical_debt_days", "technical_debt_ratio", "technical_debt_repart",
                "coverage", "line_coverage", "branch_coverage",
                "test_success_density", "test_failures", "test_errors", "tests", "skipped_tests", "test_execution_time",
                "lines_to_cover", "uncovered_lines", "conditions_to_cover", "uncovered_conditions"));
    }


    private String generateFormattedTimeMachineData(TimeMachine timeMachine) {
        JSONArray result = new JSONArray();
        int columns = timeMachine.getColumns().length;
        for (int i = 0; i < columns; i++) {
            JSONArray subArray = new JSONArray();
            for (TimeMachineCell cell : timeMachine.getCells()) {
                JSONObject o = new JSONObject();
                Date date = cell.getDate();
                o.put("x", transformDate(date));
                o.put("y", cell.values[i]);
                o.put("yl", cell.values[i]);
                subArray.add(o);
            }
            result.add(subArray);
        }
        return result.toString();
    }

    private String generateFromattedTimeMachineSnapshots(TimeMachine timeMachine) {
        JSONArray result = new JSONArray();
        int sid = 1;
        for (TimeMachineCell cell : timeMachine.getCells()) {
            JSONObject o = new JSONObject();
            o.put("sid", sid++);
            o.put("d", transformDate2(cell.getDate()));
            result.add(o);
        }
        return result.toString();
    }

    private long transformDate(Date d) {
        return d.getTime();
    }

    private String transformDate2(Date d) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(d);
    }

    public static final class ResourceAdapter extends Resource {
        private Resource resource;

        public ResourceAdapter(Resource resource) {
            this.resource = resource;
        }

        public Measure getMeasure(String metricKey) {
            if (resource == null) {
                return new Measure() {
                    @Override
                    public String getFormattedValue() {
                        return "-";
                    }
                };
            }

            Measure m = resource.getMeasure(metricKey);
            if (m == null) {
                return new Measure() {
                    @Override
                    public String getFormattedValue() {
                        return "-";
                    }
                };
            } else {
                return m;
            }
        }
    }

    public static final class Data {
        private Resource resource;
        private String formattedTimeMachine;
        private String timeMachineSnapshots;
        private List<SonarConfigEntry> sonars = new LinkedList<SonarConfigEntry>();
        private SonarConfigEntry selected;
        private SonarConfigEntry child = null;
        private boolean isGroup = false;

        public List<SonarConfigEntry> getSonars() {
            return sonars;
        }

        public Resource getResource() {
            return resource;
        }

        public String getFormattedTimeMachine() {
            return formattedTimeMachine;
        }

        public String getTimeMachineSnapshots() {
            return timeMachineSnapshots;
        }

        public SonarConfigEntry getSelected() {
            return selected;
        }

        public SonarConfigEntry getChild() {
            return child;
        }

        public boolean isGroup() {
            return isGroup;
        }

        public String getTechnicalDebtRepartData() {
            Measure measure = resource.getMeasure("technical_debt_repart");
            Map<String, String> map = measure.getDataAsMap(";");
            if (map != null) {
                return StringUtils.join(map.values(), ",");
            } else {
                return "";
            }
        }

        public String getTechnicalDebtRepartColumn() {
            Measure measure = resource.getMeasure("technical_debt_repart");
            Map<String, String> map = measure.getDataAsMap(";");
            if (map != null) {
                return StringUtils.join(map.keySet(), "|");
            } else {
                return "";
            }
        }

        /**
         * Used by velocity template
         *
         * @param blocker        the amount of blocker violations
         * @param critical       the amount of critical violations
         * @param major          the amount of major violations
         * @param minor          the amount of minor violations
         * @param info           the amount of info violations
         * @param evaluatedValue the value to be evaluated
         * @return the percentage of the evaluatedValue / max(blocker, critical, major, minor, info)
         */
        public int violationPercentage(double blocker, double critical, double major, double minor, double info, double evaluatedValue) {
            double max = blocker;

            if (critical > max) {
                max = critical;
            }
            if (major > max)  {
                max = major;
            }
            if (minor > max) {
                max = minor;
            }
            if (info > max) {
                max = info;
            }
            return (int) (evaluatedValue * 100 / max);
        }
    }

    @JsonIgnoreProperties({"empty", "resourceKey", "null"})
    public static final class SonarConfigEntry implements IMultiTabWidgetConfigEntry {
        private String name;
        private String url;
        private String resourceId;
        private List<SonarConfigEntry> groups = new LinkedList<SonarConfigEntry>();

        public SonarConfigEntry() {
        }

        public boolean hasChild() {
            return groups != null && groups.size() > 0;
        }

        public SonarConfigEntry(String name, String sonarUrl, String resourceId) {
            this.name = name;
            this.url = sonarUrl;
            this.resourceId = resourceId;
        }

        @Override
        public boolean isNull() {
            return isEmpty();
        }

        public boolean isEmpty() {
            if (StringUtils.isNotBlank(name)) {
                return false;
            } else if (StringUtils.isNotBlank(url)) {
                return false;
            } else if (StringUtils.isNotBlank(resourceId)) {
                return false;
            }

            if (groups != null && groups.size() > 0) {
                for (SonarConfigEntry groupEntry : groups) {
                    if (!groupEntry.isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return StringUtils.removeEnd(url, "/");
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSonarUrl() {
            return StringUtils.removeEnd(url, "/");
        }

        public void setSonarUrl(String sonarUrl) {
            this.url = sonarUrl;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public List<SonarConfigEntry> getGroups() {
            return groups;
        }

        public void setGroups(List<SonarConfigEntry> groups) {
            this.groups = groups;
        }
    }

    @JsonIgnoreProperties({"name", "configEntries"})
    public static final class Configuration extends DefaultMultiTabWidgetConfiguration<SonarConfigEntry> {
        private List<SonarConfigEntry> sonars = new LinkedList<SonarConfigEntry>();

        public Configuration() {
        }

        public List<SonarConfigEntry> getSonars() {
            return sonars;
        }

        public void setSonars(List<SonarConfigEntry> sonars) {
            this.sonars = sonars;
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        @Override
        public List<SonarConfigEntry> getConfigEntries() {
            return getSonars();
        }

        @Override
        public void setConfigEntries(List<SonarConfigEntry> entries) {
            setSonars(entries);
        }
    }
}

