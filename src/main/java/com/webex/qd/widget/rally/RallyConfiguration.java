package com.webex.qd.widget.rally;

import com.webex.qd.api.ApiResult;
import com.webex.qd.widget.DefaultMultiTabWidgetConfiguration;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-10-31
 * Time: 下午3:49
 */
@JsonIgnoreProperties({"name", "login_key", "configEntries"})
public class RallyConfiguration extends DefaultMultiTabWidgetConfiguration<RallyProject> {
    private List<RallyProject> projects = new LinkedList<RallyProject>();
    private String login_key;
    private String chartType;

    public List<RallyProject> getProjects() {
        return projects;
    }

    public void setProjects(List<RallyProject> projects) {
        this.projects = projects;
    }

    public String getLogin_key() {
        return login_key;
    }

    public void setLogin_key(String login_key) {
        this.login_key = login_key;
    }

    public boolean useSpecifiedLoginKey() {
        return StringUtils.isNotBlank(login_key);
    }

    @Override
    public ApiResult validate() {
        return ApiResult.SUCCESS;
    }

    @Override
    public List<RallyProject> getConfigEntries() {
        return getProjects();
    }

    @Override
    public void setConfigEntries(List<RallyProject> entries) {
        setProjects(entries);
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
}
