package com.webex.qd.dao;

import com.webex.qd.vo.Workspace;
import com.webex.qd.vo.WorkspaceDashboardEntry;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-4
 * Time: 下午2:10
 */
public interface WorkspaceDao {
    WorkspaceDashboardEntry getWorkspaceById(int workspaceId, int dashboardId);

    Workspace findWorkspaceById(int id);

    Workspace findWorkspaceByName(String name);

    List<Workspace> listAllWorkspaces();

    List<Workspace> listAllWorkspacesOrderById();
}
