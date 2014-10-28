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
 * Date: 7/10/13
 * Time: 3:56 PM
 */
public class ProductWPkgWidget extends IWidget<ProductWPkgWidget.Configuration> {

    @Override
    public Object loadData() {
        try {
            Data data = new Data();
            Map<String, List<TestStats>> map = ApiCaller.getProjectPassrate(getConfiguration().projectCodes, 1);
            Map<String, List<TestStats>> mapCoverage = ApiCaller.getCoverages(getConfiguration().projectCodes, 1);

            for(Map.Entry<String, List<TestStats>> entry : map.entrySet()) {
                List<TestStats> builds = entry.getValue();
                if(builds.size() > 0) {
                    data.addStat(builds.get(0));
                }
            }

            if(mapCoverage.size() > 0) {
                Map<String, TestStats> coverages = new HashMap<String, TestStats>();

                for(String pc : mapCoverage.keySet()) {
                    List<TestStats> builds = mapCoverage.get(pc);
                    if(builds.size() > 0) {
                        coverages.put(pc, builds.get(0));
                    }
                }

                data.addCoverages(coverages);
            }

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
        private int tt;
        private int ff;

        private List<TestStats> stats = new LinkedList<TestStats>();
        private Map<String, TestStats> coverages = null;


        public void addStat(TestStats stats) {
            this.stats.add(stats);
            tt += stats.getTotalCase();
            ff += stats.getFailedCase();
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

        public float getTotalCoverage() {
            int t = 0;
            int f = 0;
            for(String pc : this.coverages.keySet()) {
                TestStats ts = this.coverages.get(pc);
                t += ts.getTotalCase();
                f += ts.getFailedCase();
            }

            if(t != 0) {
                return (t - f) * 100.f / t;
            } else {
                return 0.f;
            }
        }

        public TestStats getStat(String key) {
            for(TestStats s : stats) {
                if(s.getProjectCode().equals(key)) {
                    return s;
                }
            }

            return null;
        }


        public TestStats getTotal() {
            return new TestStats("Total", "", tt, ff, null);
        }

        public TestStats getTotal(JSONArray pcs) {
            int tt = 0;
            int ff = 0;


            Set<String> pcSet = new HashSet<String>();
            int len = pcs.size();
            for(int i = 0; i < len; i++) {
                pcSet.add(pcs.getString(i));
            }

            for(TestStats ts : stats) {
                String pc = ts.getProjectCode();

                if(pcSet.contains(pc)) {
                    tt += ts.getTotalCase();
                    ff += ts.getFailedCase();
                }
            }

            return new TestStats("", "", tt, ff, "");
        }


        public float getTotalCoverage(JSONArray pcs) {
            int t = -1;
            int f = 0;

            Set<String> pcSet = new HashSet<String>();
            int len = pcs.size();
            for(int i = 0; i < len; i++) {
                pcSet.add(pcs.getString(i));
            }

            for(String pc : this.coverages.keySet()) {
                if(!pcSet.contains(pc)) {
                    continue;
                }

                TestStats ts = this.coverages.get(pc);
                if(t == -1) {
                    t = 0;
                }
                t += ts.getTotalCase();
                f += ts.getFailedCase();
            }

            if(t == -1) {
                return -1;
            } else if(t != 0) {
                return (t - f) * 100.f / t;
            } else {
                return 0.f;
            }
        }
    }


    public static final class Configuration extends WidgetConfiguration {
        private JSONObject json;
        private List<String> projectCodes = new LinkedList<String>();

        @Override
        public IWidgetConfiguration fromJson(JSONObject json) {
            this.json = json;

            JSONObject jobj = json.getJSONObject("project_codes");
            Iterator it = jobj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                JSONArray jarr = jobj.getJSONArray(key);

                for (int i = 0; i < jarr.size(); i++) {
                    String pc = jarr.getString(i);
                    if (!StringUtils.isBlank(pc)) {
                        projectCodes.add(pc);
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
            if (!this.json.has("project_codes")) {
                return "{}";
            }
            return this.json.get("project_codes").toString();
        }
    }
}

