package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.tasche.ApiCaller;
import com.webex.qd.tasche.TestStats;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 4:49 PM
 */
public class ProductWidget extends IWidget<ProductWidget.Configuration> {

    @Override
    public Object loadData() {
        try {
            Data data = new Data();
            Map<String, List<TestStats>> map = ApiCaller.getProjectPassrate(getConfiguration().projectCodes, 1);
            Map<String, List<TestStats>> mapCoverage = ApiCaller.getCoverages(getConfiguration().projectCodes, 1);

            for (Map.Entry<String, List<TestStats>> entry : map.entrySet()) {
                List<TestStats> builds = entry.getValue();
                if (builds.size() > 0) {
                    data.addStat(builds.get(0));
                }
            }

            Map<String, TestStats> coverages = new HashMap<String, TestStats>();
            for (Map.Entry<String, List<TestStats>> entry : mapCoverage.entrySet()) {
                List<TestStats> builds = entry.getValue();
                if (builds.size() > 0) {
                    coverages.put(entry.getKey(), builds.get(0));
                }
            }
            data.addCoverages(coverages);

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    public static final class Data {
        private TestStats totalTaStat = new TestStats();

        private List<TestStats> stats = new LinkedList<TestStats>();
        private Map<String, TestStats> coverages = null;


        public void addStat(TestStats stats) {
            this.stats.add(stats);
            totalTaStat.merge(stats);
        }

        public List<TestStats> getStats() {
            return stats;
        }

        public void addCoverages(Map<String, TestStats> coverages) {
            this.coverages = coverages;
        }

        public Map<String, TestStats> getCoverages() {
            return this.coverages;
        }


        public TestStats getTotal() {
            return totalTaStat;
        }

        public float getTotalCoverage() {
            TestStats totalCoverageStat = new TestStats();
            for (TestStats coverageStat : this.coverages.values()) {
                totalCoverageStat.merge(coverageStat);
            }
            return totalCoverageStat.getPassrate();
        }
    }

    public static final class Configuration extends WidgetConfiguration {
        private JSONObject json;
        private List<String> projectCodes = new LinkedList<String>();

        @Override
        public IWidgetConfiguration fromJson(JSONObject json) {
            this.json = json;
            projectCodes.clear();
            if (json.containsKey("project_codes")) {
                JSONArray jarr = json.getJSONArray("project_codes");
                for (int i = 0; i < jarr.size(); i++) {
                    String pc = jarr.getString(i);
                    if (StringUtils.isNotBlank(pc)) {
                        this.projectCodes.add(pc);
                    }
                }
            }
            return this;
        }

        @Override
        public boolean doNullCheck() {
            return projectCodes.isEmpty();
        }

        @Override
        public JSONObject toJson() {
            return null;
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        public Object get(String key) {
            Object obj = this.json.get(key);
            if (obj == null) {
                return "";
            } else {
                return obj;
            }
        }

        public String getProjectCodes() {
            if (!json.has("project_codes")) {
                return "{}";
            }

            return json.get("project_codes").toString();
        }
    }
}
