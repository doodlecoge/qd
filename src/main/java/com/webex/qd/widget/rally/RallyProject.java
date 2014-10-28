package com.webex.qd.widget.rally;

import com.webex.qd.widget.IMultiTabWidgetConfigEntry;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: huaiwang
 * Date: 7/31/13
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties({"empty", "null"})
public class RallyProject implements IMultiTabWidgetConfigEntry {
    private String display_name;
    private String workspace_id;
    private String project_id;

    public RallyProject() {
    }

    @Override
    public boolean isNull() {
        return isEmpty();
    }

    public boolean isEmpty() {
        if (StringUtils.isNotBlank(display_name)) {
            return false;
        } else if (StringUtils.isNotBlank(workspace_id)) {
            return false;
        } else if (StringUtils.isNotBlank(project_id)) {
            return false;
        }
        return true;
    }

    public RallyProject(String displayName, String workspaceId, String projectId) {
        this.display_name = displayName;
        this.workspace_id = workspaceId;
        this.project_id = projectId;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String displayName) {
        this.display_name = displayName;
    }

    public String getWorkspace_id() {
        return workspace_id;
    }

    public void setWorkspace_id(String workspaceId) {
        if (workspaceId != null) {
            Pattern ptn = Pattern.compile("^(\\d+)");
            Matcher matcher = ptn.matcher(workspaceId);

            if (matcher.find()) {
                this.workspace_id = matcher.group(1);
            }
        }
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String projectId) {
        if (projectId != null) {
            Pattern ptn = Pattern.compile("^(\\d+)");
            Matcher matcher = ptn.matcher(projectId);

            if (matcher.find()) {
                this.project_id = matcher.group(1);
            }
        }
    }
}
