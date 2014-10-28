package com.webex.qd.service;

import com.webex.qd.exception.QualityDashboardException;
import com.webex.qd.vo.Dashboard;
import com.webex.qd.vo.Workspace;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-4
 * Time: 下午2:15
 */
public interface WorkspaceService {
    List<Workspace> listWorkspaces();

    List<Dashboard> getDashboardsInWorkspace(int workspaceId);

    List<Dashboard> getDashboardsInWorkspace(String workspaceName);

    String getDefaultWorkspaceName();

    Workspace getWorkspaceById(int workspaceId);

    void addDashboard2Workspace(int workspaceId, int dashboardId) throws QualityDashboardException;

    void deleteDashboardFromWorkspace(int workspaceId, int dashboardId) throws QualityDashboardException;
}
