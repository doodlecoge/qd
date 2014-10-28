package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.api.ErrorResult;
import com.webex.qd.tasche.ApiCaller;
import com.webex.qd.tasche.TestStats;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 3:55 PM
 */
public class ProductDifferWidget extends IWidget<ProductDifferWidget.Configuration> {

    @Override
    public Object loadData() {
        try {
            Map<String, List<TestStats>> map = ApiCaller.getProjectPassrate(getConfiguration().getProject_codes(), 2);
            return new Data(map);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    public static final class Data {
        private Map<String, List<TestStats>> stats;

        public  Data(Map<String, List<TestStats>> stats) {
            this.stats = stats;
        }

        public Map<String, List<TestStats>> getStats() {
            return stats;
        }

        public void setStats(Map<String, List<TestStats>> stats) {
            this.stats = stats;
        }
    }

    @JsonIgnoreProperties({"name"})
    public static final class Configuration extends DefaultWidgetConfiguration {
        List<String> project_codes = new LinkedList<String>();

        public String createCommaDelimitedProjectCodes() {
            return StringUtils.join(project_codes, ",");
        }

        public List<String> getProject_codes() {
            return project_codes;
        }

        public void setProject_codes(List<String> projectCodes) {
            this.project_codes = projectCodes;
        }

        @Override
        public ApiResult validate() {
            try {
                Set<String> set = ApiCaller.getProjectCodes();
                for(String pc : this.project_codes) {
                    if(!set.contains(pc)) {
                        return new ErrorResult("Invalid project name " + pc);
                    }
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
    }
}