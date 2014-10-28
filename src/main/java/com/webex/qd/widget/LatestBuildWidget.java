package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.tasche.TestStats;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 4:02 PM
 */
public class LatestBuildWidget extends IWidget<LatestBuildWidget.Configuration> {
    @Override
    public Object loadData() {
        return null;
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    public static final class Data {
        private Map<String, List<TestStats>> stats;

        private Map<String, TestStats> coverages = null;

        public Data(Map<String, List<TestStats>> stats) {
            this.stats = stats;
        }

        public Map<String, List<TestStats>> getStats() {
            return stats;
        }

        public void addCoverages(Map<String, TestStats> coverages) {
            this.coverages = coverages;
        }

        public Map<String, TestStats> getCoverages() {
            return this.coverages;
        }

        public float getTotalCoverage() {
            int tt = 0;
            int ff = 0;
            for (String pc : this.coverages.keySet()) {
                TestStats ts = this.coverages.get(pc);
                tt += ts.getTotalCase();
                ff += ts.getFailedCase();
            }

            if (tt != 0) {
                return (tt - ff) * 100.f / tt;
            } else {
                return 0.f;
            }
        }

        public void setStats(Map<String, List<TestStats>> stats) {
            this.stats = stats;
        }

        public List<TestStats> getStat(String name) {
            return this.stats.get(name);
        }


        public List<TestStats> getTotal(JSONArray pcs) {
            List<Integer> tt = new ArrayList<Integer>();
            List<Integer> ff = new ArrayList<Integer>();

            Set<String> pcSet = new HashSet<String>();
            int len = pcs.size();
            for (int i = 0; i < len; i++) {
                pcSet.add(pcs.getString(i));
            }

            int maxLen = 2;

            for (String pc : stats.keySet()) {
                if (!pcSet.contains(pc)) continue;

                List<TestStats> lst = stats.get(pc);

                int lstSz = lst.size();

                for (int i = 0; i < maxLen; i++) {

                    if (lstSz <= i) continue;

                    TestStats ts = lst.get(i);

                    if (tt.size() <= i) {
                        tt.add(ts.getTotalCase());
                        ff.add(ts.getFailedCase());
                    } else {
                        tt.set(i, tt.get(i) + ts.getTotalCase());
                        ff.set(i, ff.get(i) + ts.getFailedCase());
                    }
                }
            }


            List<TestStats> rst = new ArrayList<TestStats>();

            len = tt.size();

            for (int i = 0; i < len; i++) {
                int t = tt.get(i);
                int f = ff.get(i);

                rst.add(new TestStats("", "", t, f, ""));
            }

            return rst;
        }


        public TestStats getTotalCoverage(JSONArray pcs) {
            int tt = 0;
            int ff = 0;

            Set<String> pcSet = new HashSet<String>();
            int len = pcs.size();
            for (int i = 0; i < len; i++) {
                pcSet.add(pcs.getString(i));
            }

            for (String pc : coverages.keySet()) {
                if (!pcSet.contains(pc)) continue;

                TestStats testStats = coverages.get(pc);

                tt += testStats.getTotalCase();
                ff += testStats.getFailedCase();
            }

            if (tt == 0) {
                return null;
            } else {
                return new TestStats("", "", tt, ff, "");
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

            if (!jobj.isNullObject()) {
                Iterator it = jobj.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    JSONArray jarr = jobj.getJSONArray(key);

                    int len = jarr.size();
                    for (int i = 0; i < len; i++) {
                        String pc = jarr.getString(i);
                        if (!StringUtils.isBlank(pc)) {
                            projectCodes.add(pc);
                        }
                    }
                }
            }
            return this;
        }

        @Override
        public JSONObject toJson() {
            return null;
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        @Override
        public boolean doNullCheck() {
            return projectCodes.isEmpty();
        }

        public Object get(String key) {
            if (json == null) {
                return null;
            }
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

        public Object getConfig() {
            return this.json.get("project_codes");
        }

        public Set<String> getAllProjectCodes() {
            Set<String> set = new HashSet<String>();

            JSONObject jobj = this.json.getJSONObject("project_codes");

            Iterator pkgs = jobj.keys();

            while (pkgs.hasNext()) {
                String pkg = pkgs.next().toString();
                JSONArray pcs = jobj.getJSONArray(pkg);

                int len = pcs.size();
                for (int i = 0; i < len; i++) {
                    set.add(pcs.getString(i));
                }
            }

            return set;
        }


    }
}
