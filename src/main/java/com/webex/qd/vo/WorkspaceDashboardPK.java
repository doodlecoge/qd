package com.webex.qd.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-4
 * Time: 上午11:36
 */
@Embeddable
public class WorkspaceDashboardPK implements Serializable {
    @Column(name="workspace_id")
    private int workspaceId;

    @Column(name = "dashboard_id")
    private int dashboardId;

    public WorkspaceDashboardPK() {
    }

    public WorkspaceDashboardPK(int workspaceId, int dashboardId) {
        this.workspaceId = workspaceId;
        this.dashboardId = dashboardId;
    }

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public int getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }
}
