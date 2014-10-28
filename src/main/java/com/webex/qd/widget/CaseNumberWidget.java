package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.api.ErrorResult;
import com.webex.qd.tasche.ApiCaller;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 3:58 PM
 */
public class CaseNumberWidget extends IWidget<CaseNumberWidget.Configuration> {
    @Override
    public Object loadData() {
        return null;
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }


    public static final class Configuration extends DefaultWidgetConfiguration {
        private List<String> project_codes = new LinkedList<String>();
        private int builds;

        @Override
        public ApiResult validate() {
            try {
                Set<String> set = ApiCaller.getProjectCodes();
                for (String pc : this.project_codes) {
                    if (!set.contains(pc)) {
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

        public String createDelimitedProjectCodes() {
            return StringUtils.join(project_codes, ",");
        }

        public List<String> getProject_codes() {
            return project_codes;
        }

        public void setProject_codes(List<String> projectCodes) {
            this.project_codes = projectCodes;
        }

        public int getBuilds() {
            return builds;
        }

        public void setBuilds(int builds) {
            this.builds = builds;
        }
    }
}
