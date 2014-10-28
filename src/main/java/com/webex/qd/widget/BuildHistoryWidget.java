package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.api.ErrorResult;
import com.webex.qd.tasche.ApiCaller;
import com.webex.qd.tasche.TestStats;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 3:59 PM
 */

public class BuildHistoryWidget extends IWidget<BuildHistoryWidget.Configuration> {

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

        public Data(Map<String, List<TestStats>> stats) {
            this.stats = stats;
        }

        public Map<String, List<TestStats>> getStats() {
            return stats;
        }

        public void setStats(Map<String, List<TestStats>> stats) {
            this.stats = stats;
        }
    }



    public static final class Configuration extends DefaultWidgetConfiguration {
        private List<String> project_codes = new LinkedList<String>();
        private int builds;
        private String filter;

        @Override
        public ApiResult validate() {
            try {
                Set<String> set = ApiCaller.getProjectCodes();
                for (String pc : this.project_codes) {
                    if (!set.contains(pc))
                        return new ErrorResult("Invalid project name " + pc);
                }
            } catch (Exception e) {
                return new ErrorResult("Error: " + e.getMessage());
            }

            return ApiResult.SUCCESS;
        }

        @Override
        public boolean doNullCheck() {
            return project_codes.isEmpty();
        }

        public String createDelimitedProjectCodes() {
            return StringUtils.join(project_codes, ",");
        }

        public List<String> getProject_codes() {
            return project_codes;
        }

        public void setProject_codes(List<String> project_codes) {
            this.project_codes = project_codes;
        }

        public int getBuilds() {
            return builds;
        }

        public void setBuilds(int builds) {
            this.builds = builds;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }
    }

}
