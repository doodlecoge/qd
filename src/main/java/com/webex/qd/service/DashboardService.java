package com.webex.qd.service;

import com.webex.qd.exception.QualityDashboardException;
import com.webex.qd.vo.Dashboard;
import com.webex.qd.vo.GroupDashboardEntry;
import com.webex.qd.vo.UserDashboardEntry;
import com.webex.qd.vo.Widget;

import java.util.List;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 8:54 AM
 */
public interface DashboardService extends WorkspaceService {
    List<Dashboard> getSystemDashboards(String workspaceName);

    List<Dashboard> listNonGroupDashboards();

    List<Dashboard> listGroupDashboards();

    List<Dashboard> listGroupDashboards(int userId);

    List<Dashboard> listAllDashboards();

    List<Dashboard> getDashboardsOfUser(String username);

    List<Dashboard> getUserOwnedDashboards(String username);

    List<String> listDashboardNames();

    List<UserDashboardEntry> listUserOfDashboard(int dashboardId, int role);

    List<Dashboard> searchDashboardByName(String dashboardName);

    Dashboard getDashboardById(int id);
    Dashboard createDashboard(int userId, String dashboardName, int columns, int groupId);
    Dashboard saveDashboard(Dashboard dashboard);
    Dashboard cloneDashboard(Dashboard sourceDashboard, int ownerUserId, String dashboardName);
    void deleteDashboard(int dashboardId);

    Widget createWidgetForDashboard(int dashboardId, String widgetType, String widgetName);
    Widget copyWidgetForDashboard(int dashboardId, String widgetType, String widgetName, String widgetSetting);
    Widget getWidgetById(int id);
    void saveWidget(Widget widget);
    void deleteWidget(int widgetId);

    void followDashboard(int userId, int dashboardId) throws QualityDashboardException;
    void unfollowDashboard(int userId, int dashboardId) throws QualityDashboardException;

    boolean isUserFollowedDashboard(int userId, int dashboardId);

    void addUser2Dashboard(int userId, int dashboardId, int role) throws QualityDashboardException;
    void removeUserFromDashboard(int userId, int dashboardId) throws QualityDashboardException;

    int nextRankOfUserDashboard(int userId);
    int nextRankOfGroupDashboard(int groupId);
    int nextRankOfWorkspaceDashboard(int workspaceId);
    int nextRankOfWorkspace();

    void saveUserDashboardOrders(int userId, int[] dashboardIds) throws QualityDashboardException;

    void saveGroupDashboardOrders(int groupId, int[] dashboardIds) throws QualityDashboardException;

    void saveWorkspaceDashboardOrders(int workspaceId, int[] dashboardIds) throws QualityDashboardException;

    List<GroupDashboardEntry> listChildrenOfGroup(int groupId);

    void addChild2Group(int groupId, int childId) throws QualityDashboardException;
    void removeChildFromGroup(int groupId, int childId) throws QualityDashboardException;
    void createWorkspace(String workspaceName) throws QualityDashboardException;
    void deleteWorkspace(int workspaceId) throws QualityDashboardException;

    Dashboard changeName(int dbid, String dbname);
}
