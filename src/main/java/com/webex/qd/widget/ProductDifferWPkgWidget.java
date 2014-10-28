package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.tasche.ApiCaller;
import com.webex.qd.tasche.TestStats;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 3:57 PM
 */
public class ProductDifferWPkgWidget extends IWidget<ProductDifferWPkgWidget.Configuration> {

    @Override
    public Object loadData() {
        try {
            Map<String, List<TestStats>> map = ApiCaller.getProjectPassrate(getConfiguration().getAllProjectCodes(), 2);
            return new Data(map);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    public static final class DiffedLine {
        private TestStats previousStat;
        private TestStats currentStat;
        private float diff = Float.MIN_VALUE;
        private String name;
        private boolean referable = false;

        public float getDiff() {
            return diff;
        }

        public String getDiffAsString() {
            if (diff == Float.MIN_VALUE) {
                return "N/A";
            } else {
                DecimalFormat df = new DecimalFormat("0.0");
                return df.format(diff) + "%";
            }
        }

        public void setDiff(float diff) {
            this.diff = diff;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isReferable() {
            return referable;
        }

        public void setReferable(boolean referable) {
            this.referable = referable;
        }

        public TestStats getPreviousStat() {
            return previousStat;
        }

        public void setPreviousStat(TestStats previousStat) {
            this.previousStat = previousStat;
        }

        public TestStats getCurrentStat() {
            return currentStat;
        }

        public void setCurrentStat(TestStats currentStat) {
            this.currentStat = currentStat;
        }

        public String getPassRateAsString(TestStats stat) {
            if (stat == null) {
                return "N/A";
            } else {
                DecimalFormat df = new DecimalFormat("0.0");
                return df.format(stat.getPassrate()) + "%";
            }
        }
    }


    public static final class Data {
        private Map<String, List<TestStats>> stats;

        public Data(Map<String, List<TestStats>> stats) {
            this.stats = stats;
        }

        public Map<String, List<TestStats>> getStats() {
            return stats;
        }

        public void setStats(Map<String, List<TestStats>> stats) {
            this.stats = stats;
        }

        public List<TestStats> getStat(String name) {
            return this.stats.get(name);
        }

        public DiffedLine diff(String groupName, Set<String> projectCodes) {
            return diff(groupName, Arrays.asList(projectCodes.toArray(new String[projectCodes.size()])));
        }

        public DiffedLine diff(String groupName, List<String> projectCodes) {
            TestStats mergedStats1 = new TestStats();
            TestStats mergedStats2 = new TestStats();
            for (String projectCode : projectCodes) {
                List<TestStats> stats = this.stats.get(projectCode);
                if (stats != null && stats.size() > 0) {
                    mergedStats1.merge(stats.get(0));
                    if (stats.size() > 1) {
                        mergedStats2.merge(stats.get(1));
                    }
                }
            }
            DiffedLine diffedLine = _diff(Arrays.asList(mergedStats1, mergedStats2));
            diffedLine.setName(groupName);
            diffedLine.setReferable(false);
            return diffedLine;
        }

        public DiffedLine diff(String projectCode) {
            return _diff(this.stats.get(projectCode));
        }

        private DiffedLine _diff(List<TestStats> stats) {
            DiffedLine diffedLine = new DiffedLine();
            if (stats != null) {
                if (stats.size() == 1) {
                    TestStats stat = stats.get(0);
                    diffedLine.setCurrentStat(stat);
                    diffedLine.setName(stat.getProjectCode());
                } else if (stats.size() > 1) {
                    TestStats stat1 = stats.get(1);
                    diffedLine.setPreviousStat(stat1);
                    TestStats stat2 = stats.get(0);
                    diffedLine.setCurrentStat(stat2);
                    diffedLine.setDiff(stat2.getPassrate() - stat1.getPassrate());
                    diffedLine.setName(stat1.getProjectCode());
                    diffedLine.setReferable(true);
                }
            }
            return diffedLine;
        }
    }


    public static final class Configuration extends WidgetConfiguration {
        private JSONObject json;
        private Map<String, List<String>> groups = new HashMap<String, List<String>>();


        @Override
        public IWidgetConfiguration fromJson(JSONObject json) {
            this.json = json;
            JSONObject jobj = json.getJSONObject("project_codes");
            groups = extractGroups(jobj);
            return this;
        }

        @Override
        public boolean doNullCheck() {
            return groups.isEmpty();
        }

        public List<String> getNonGroupedProjectCodes() {
            return groups.get("--");
        }

        public Set<String> getGroupNames() {
            return groups.keySet();
        }

        public List<String> getGroupProjectCodes(String groupName) {
            if (groups.containsKey(groupName)) {
                return groups.get(groupName);
            } else {
                return Collections.emptyList();
            }
        }


        private List<String> extractProjectCodes(JSONArray array) {
            List<String> result = new LinkedList<String>();
            if (array != null) {
                for (int i = 0 ; i < array.size(); i++) {
                    String projectCode = array.getString(i);
                    result.add(projectCode);
                }
            }
            return result;
        }

        private Map<String, List<String>> extractGroups(JSONObject projectCodesNode) {
            Map<String, List<String>> groups = new HashMap<String, List<String>>();
            if (projectCodesNode != null && !projectCodesNode.isNullObject()) {
                Iterator iterator = projectCodesNode.keys();
                while (iterator.hasNext()) {
                    String groupName = (String) iterator.next();
                    JSONArray array = projectCodesNode.getJSONArray(groupName);
                    groups.put(groupName, extractProjectCodes(array));
                }
            }
            return groups;
        }

        public Set<String> getAllProjectCodes() {
            Set<String> allProjectCodes = new HashSet<String>();
            for(List<String> projectCodes : groups.values()) {
                for (String projectCode : projectCodes) {
                    allProjectCodes.add(projectCode);
                }
            }
            return allProjectCodes;
        }

        @Override
        public JSONObject toJson() {
            return null;
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        public String getProjectCodes() {
            if (!json.has("project_codes")) {
                return "{}";
            }

            return json.get("project_codes").toString();
        }
    }
}

