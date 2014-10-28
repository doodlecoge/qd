package com.webex.qd.service;

import com.webex.qd.dao.*;
import com.webex.qd.exception.QualityDashboardException;
import com.webex.qd.vo.*;
import com.webex.qd.widget.Layout;
import com.webex.qd.widget.WidgetManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 8:54 AM
 */
@Repository("dashboardService")
@Transactional
public class DashboardServiceImpl extends BaseDao implements DashboardService {

    @Autowired
    private DashboardDao dashboardDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private WidgetDao widgetDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private WorkspaceDao workspaceDao;
    @Autowired
    private WidgetManager widgetManager;

    @Override
    public List<Dashboard> getSystemDashboards(String workspaceName) {
        return getDashboardsInWorkspace(workspaceName);
    }

    @Override
    public List<Dashboard> listNonGroupDashboards() {
        Session session = getCurrentSession();
        return session.createQuery("from Dashboard where isGrp = 0").list();
    }

    @Override
    public List<Dashboard> listGroupDashboards() {
        return getCurrentSession().createQuery("from Dashboard  where isGrp = 1").list();
    }

    @Override
    public List<Dashboard> listGroupDashboards(int userId) {
        return getCurrentSession().createQuery("from Dashboard where isGrp = 1 and uid=?").setParameter(0, userId).list();
    }

    @Override
    public List<Dashboard> listAllDashboards() {
        Session session = getCurrentSession();
        return session.createQuery("from Dashboard where isGrp != 2").list();
    }

    public List<Dashboard> getDashboardsOfUser(String username) {
        User user = userDao.findByUserName(username);
        Set<UserDashboardEntry> entries = user.getDashboards();
        List<Dashboard> result = new LinkedList<Dashboard>();
        for (UserDashboardEntry entry : entries) {
            result.add(entry.getDashboard());
        }
        return result;
    }

    @Override
    public List<Dashboard> getUserOwnedDashboards(String username) {
        User user = userDao.findByUserName(username);
        Set<UserDashboardEntry> entries = user.getDashboards();
        List<Dashboard> result = new LinkedList<Dashboard>();
        for (UserDashboardEntry entry : entries) {
            if (entry.getRole() == 0)
                result.add(entry.getDashboard());
        }
        return result;
    }

    @Override
    public List<String> listDashboardNames() {
        Session session = getCurrentSession();
        return session.createQuery("select distinct name from Dashboard order by name asc").list();
    }

    @Override
    public List<UserDashboardEntry> listUserOfDashboard(int dashboardId, int role) {
        Session session = getCurrentSession();
        return session.createQuery("from UserDashboardEntry where id.dashboardId=? and role=?")
                .setParameter(0, dashboardId)
                .setParameter(1, role).list();
    }

    @Override
    public List<Dashboard> searchDashboardByName(String dashboardName) {
        Session session = getCurrentSession();
        return session.createQuery("from Dashboard where name like ? order by name asc").setParameter(0, "%" + dashboardName + "%").list();
    }

    @Override
    public Dashboard getDashboardById(int id) {
        return dashboardDao.findDashboardById(id);
    }

    @Override
    public Widget getWidgetById(int id) {
        return widgetDao.getWidget(id);
    }

    public Dashboard createDashboard(int userId, String name, int columns, int groupId) {
        JSONArray layout = new JSONArray();
        for (int i = 0; i < columns; i++) {
            layout.add(new JSONArray());
        }

        Dashboard dashboard = new Dashboard();

        dashboard.setName(name);
        dashboard.setLayout(layout.toString());
        dashboard.setUid(userId);
        if (groupId == -1) {
            dashboard.setGrp(1);
        } else {
            dashboard.setGrp(0);
        }

        Session session = getCurrentSession();
        int dashboardId = (Integer) session.save(dashboard);
        dashboard.setId(dashboardId);
        UserDashboardEntry ud = new UserDashboardEntry();
        ud.setId(new UserDashboardPK(userId, dashboardId));
        ud.setRank(nextRankOfUserDashboard(userId));
        ud.setRole(0); // 0: owner, 1: member
        session.save(ud);

        if (groupId > 0) {
            GroupDashboardPK id = new GroupDashboardPK();
            id.setGroupId(groupId);
            id.setDashboardId(dashboardId);
            GroupDashboardEntry grp = new GroupDashboardEntry();
            grp.setId(id);
            session.save(grp);
        }

        return dashboard;
    }

    @Override
    public void deleteDashboard(int dashboardId) {
        Session session = getCurrentSession();
        Dashboard dashboard = dashboardDao.findDashboardById(dashboardId);
        Set<Widget> widgets = dashboard.getWidgets();
        List<UserDashboardEntry> userDashboardEntries = session.createQuery("FROM UserDashboardEntry where id.dashboardId = ?").setParameter(0, dashboardId).list();
        List<GroupDashboardEntry> groupDashboardEntries1 = session.createQuery("FROM GroupDashboardEntry where id.dashboardId = ?").setParameter(0, dashboardId).list();
        List<GroupDashboardEntry> groupDashboardEntries2 = session.createQuery("FROM GroupDashboardEntry where id.groupId = ?").setParameter(0, dashboardId).list();

        for (Widget widget : widgets) {
            session.delete(widget);
        }

        for (UserDashboardEntry entry : userDashboardEntries) {
            session.delete(entry);
        }

        for (GroupDashboardEntry entry : groupDashboardEntries1) {
            session.delete(entry);
        }

        for (GroupDashboardEntry entry : groupDashboardEntries2) {
            session.delete(entry);
        }

        session.delete(dashboard);
    }

    @Override
    public void saveWidget(Widget widget) {
        getCurrentSession().saveOrUpdate(widget);
    }

    @Override
    public void deleteWidget(int widgetId) {
        Widget widget = (Widget) getCurrentSession().get(Widget.class, widgetId);
        if (widget != null) {
            getCurrentSession().delete(widget);
        }
    }

    @Override
    public int nextRankOfUserDashboard(int userId) {
        Query query = getCurrentSession().createQuery("select max(rank) from UserDashboardEntry where id.userId=?")
                .setParameter(0, userId);
        List resultSet = query.list();
        if (resultSet == null || resultSet.size() == 0) {
            return 0;
        } else {
            if (resultSet.get(0) == null) {
                return 0;
            } else {
                return Integer.parseInt(resultSet.get(0).toString()) + 1;
            }
        }
    }

    @Override
    public int nextRankOfGroupDashboard(int groupId) {
        Query query = getCurrentSession().createQuery("select max(rank) from GroupDashboardEntry where id.groupId=?")
                .setParameter(0, groupId);
        List resultSet = query.list();
        if (resultSet == null || resultSet.size() == 0) {
            return 0;
        } else {
            if (resultSet.get(0) == null) {
                return 0;
            } else {
                return Integer.parseInt(resultSet.get(0).toString()) + 1;
            }
        }
    }

    @Override
    public int nextRankOfWorkspace() {
        String sqlStr = "select max(id) from workspaces";
        Query query = getCurrentSession().createSQLQuery(sqlStr);
        List resultSet = query.list();

        if (resultSet == null || resultSet.size() == 0) {
            return 0;
        } else {
            if (resultSet.get(0) == null) {
                return 0;
            } else {
                return Integer.parseInt(resultSet.get(0).toString()) + 1;
            }
        }
    }

    @Override
    public int nextRankOfWorkspaceDashboard(int workspaceId) {
        String sqlStr = "select max(`order`) from workspaces_dashboards where workspace_id=" + workspaceId;
        Query query = getCurrentSession().createSQLQuery(sqlStr);
        List resultSet = query.list();

        if (resultSet == null || resultSet.size() == 0) {
            return 0;
        } else {
            if (resultSet.get(0) == null) {
                return 0;
            } else {
                return Integer.parseInt(resultSet.get(0).toString()) + 1;
            }
        }
    }

    @Override
    public void followDashboard(int userId, int dashboardId) throws QualityDashboardException {
        Session session = getCurrentSession();

        List resultList = session.createQuery("from UserDashboardEntry where id.userId=? and id.dashboardId=?")
                .setParameter(0, userId)
                .setParameter(1, dashboardId).list();
        if (resultList != null && resultList.size() > 0) {
            throw new QualityDashboardException("User has already followed the dashboard");
        }

        UserDashboardEntry userDashboard = new UserDashboardEntry();
        userDashboard.setId(new UserDashboardPK(userId, dashboardId));
        userDashboard.setRole(1);
        userDashboard.setRank(nextRankOfUserDashboard(userId));
        session.save(userDashboard);
    }

    @Override
    public void unfollowDashboard(int userId, int dashboardId) throws QualityDashboardException {
        Session session = getCurrentSession();

        List resultList = session.createQuery("from UserDashboardEntry where id.userId=? and id.dashboardId=?")
                .setParameter(0, userId)
                .setParameter(1, dashboardId).list();
        if (resultList != null && resultList.size() != 0) {
            UserDashboardEntry userDashboard = (UserDashboardEntry) resultList.get(0);
            session.delete(userDashboard);
        }
    }

    @Override
    public boolean isUserFollowedDashboard(int userId, int dashboardId) {
        UserDashboardPK key = new UserDashboardPK(userId, dashboardId);
        UserDashboardEntry entry = (UserDashboardEntry) getCurrentSession().get(UserDashboardEntry.class, key);
        return entry != null;
    }

    @Override
    public void addUser2Dashboard(int userId, int dashboardId, int role) throws QualityDashboardException {
        Session session = getCurrentSession();
        if (!isValidDashboard(dashboardId)) {
            throw new QualityDashboardException("Invalid dashboard");
        }

        if (!isValidUser(userId)) {
            throw new QualityDashboardException("Invalid user");
        }

        UserDashboardEntry entry = (UserDashboardEntry) session.get(UserDashboardEntry.class, new UserDashboardPK(userId, dashboardId));
        if (entry == null) {
            // create a new record
            entry = new UserDashboardEntry();
            entry.setId(new UserDashboardPK(userId, dashboardId));
            entry.setRank(nextRankOfUserDashboard(userId));
        }
        entry.setRole(role);
        session.save(entry);
    }

    @Override
    public void removeUserFromDashboard(int userId, int dashboardId) throws QualityDashboardException {
        Session session = getCurrentSession();
        if (!isValidDashboard(dashboardId)) {
            throw new QualityDashboardException("Invalid dashboard");
        }

        if (!isValidUser(userId)) {
            throw new QualityDashboardException("Invalid user");
        }

        UserDashboardEntry entry = (UserDashboardEntry) session.get(UserDashboardEntry.class, new UserDashboardPK(userId, dashboardId));
        if (entry != null) {
            session.delete(entry);
        }
    }

    @Override
    public void saveUserDashboardOrders(int userId, int[] dashboardIds) throws QualityDashboardException {
        Session session = getCurrentSession();

        List<UserDashboardEntry> results = session.createQuery("from UserDashboardEntry where id.userId=?")
                .setParameter(0, userId).list();
        for (int i = 0; i < dashboardIds.length; i++) {
            for (UserDashboardEntry entry : results) {
                if (entry.getId().getDashboardId() == dashboardIds[i]) {
                    entry.setRank(i);
                    session.save(entry);
                }
            }
        }
    }

    @Override
    public void saveGroupDashboardOrders(int groupId, int[] dashboardIds) throws QualityDashboardException {
        Session session = getCurrentSession();

        List<GroupDashboardEntry> results = session.createQuery("from GroupDashboardEntry where id.groupId=?")
                .setParameter(0, groupId).list();
        for (int i = 0; i < dashboardIds.length; i++) {
            for (GroupDashboardEntry entry : results) {
                if (entry.getId().getDashboardId() == dashboardIds[i]) {
                    entry.setRank(i);
                    session.save(entry);
                }
            }
        }
    }

    @Override
    public void saveWorkspaceDashboardOrders(int workspaceId, int[] dashboardIds) throws QualityDashboardException {
        Session session = getCurrentSession();

        List<WorkspaceDashboardEntry> results = session.createQuery("from WorkspaceDashboardEntry where id.workspaceId=?")
                .setParameter(0, workspaceId).list();
        for (int i = 0; i < dashboardIds.length; i++) {
            for (WorkspaceDashboardEntry entry : results) {
                if (entry.getId().getDashboardId() == dashboardIds[i]) {
                    entry.setOrder(i);
                    session.save(entry);
                }
            }
        }
    }

    @Override
    public List<GroupDashboardEntry> listChildrenOfGroup(int groupId) {
        Session session = getCurrentSession();
        List<GroupDashboardEntry> results = session.createQuery("from GroupDashboardEntry where id.groupId=? order by rank asc").setParameter(0, groupId).list();

        return results;
    }

    @Override
    public void addChild2Group(int groupId, int childId) throws QualityDashboardException {
        Session session = getCurrentSession();
        Dashboard group = dashboardDao.findDashboardById(groupId);
        if (group == null || !group.isGroup()) {
            throw new QualityDashboardException("Invalid group");
        }

        Dashboard child = dashboardDao.findDashboardById(childId);
        if (child == null || child.isGroup()) {
            throw new QualityDashboardException("Invalid child");
        }

        GroupDashboardEntry entry = groupDao.getGroupById(groupId, childId);
        if (entry != null) {
            throw new QualityDashboardException("The child is already in the group");
        } else {
            entry = new GroupDashboardEntry();
            entry.setRank(nextRankOfGroupDashboard(groupId));
            entry.setId(new GroupDashboardPK(groupId, childId));
            session.save(entry);
        }
    }

    @Override
    public void removeChildFromGroup(int groupId, int childId) throws QualityDashboardException {
        Session session = getCurrentSession();
        Dashboard group = dashboardDao.findDashboardById(groupId);
        if (group == null || !group.isGroup()) {
            throw new QualityDashboardException("Invalid group");
        }

        Dashboard child = dashboardDao.findDashboardById(childId);
        if (child == null || child.isGroup()) {
            throw new QualityDashboardException("Invalid child");
        }

        GroupDashboardEntry entry = groupDao.getGroupById(groupId, childId);
        if (entry != null) {
            session.delete(entry);
        }
    }

    @Override
    public Widget createWidgetForDashboard(int dashboardId, String widgetType, String widgetName) {
        final Widget widget = new Widget();
        widget.setDashboardId(dashboardId);
        widget.setType(widgetType);
        JSONObject setting = new JSONObject();
        setting.put("name", widgetName);
        widget.setSetting(setting.toString());
        widget.setName(widgetName);
        getCurrentSession().save(widget);
        return widget;
    }

    @Override
    public Widget copyWidgetForDashboard(int dashboardId, String widgetType, String widgetName, String widgetSetting) {
        final Widget widget = new Widget();
        widget.setDashboardId(dashboardId);
        widget.setType(widgetType);
        widget.setSetting(widgetSetting);
        widget.setName(widgetName);
        getCurrentSession().save(widget);
        return widget;
    }

    @Override
    public Dashboard saveDashboard(Dashboard dashboard) {
        getCurrentSession().saveOrUpdate(dashboard);
        return dashboard;
    }

    @Override
    public Dashboard cloneDashboard(Dashboard sourceDashboard, int ownerUserId, String dashboardName) {
        Layout sourceLayout = sourceDashboard.getLayoutObject();
        List<Layout.Column> columns = sourceLayout.getColumns();
        List<List<String>> dstLayout = new LinkedList<List<String>>();

        Dashboard dstDashboard = createDashboard(ownerUserId, dashboardName, 2, 0);

        for (Layout.Column column : columns) {
            List<Integer> widgetIds = column.getWidgetIds();
            List<String> col = new LinkedList<String>();
            for (Integer id : widgetIds) {
                Widget sourceWidget = widgetManager.getWidgetById(id);
                Widget dstWidget = copyWidgetForDashboard(dstDashboard.getId(), sourceWidget.getType(), sourceWidget.getName(), sourceWidget.getSetting());
                col.add(String.valueOf(dstWidget.getId()));
            }
            dstLayout.add(col);
        }
        JSONArray array = new JSONArray();
        array.add(dstLayout.get(0));
        array.add(dstLayout.get(1));
        dstDashboard.setLayout(array.toString());
        saveDashboard(dstDashboard);
        return dstDashboard;
    }

    private boolean isValidDashboard(int dashboardId) {
        Dashboard dashboard = dashboardDao.findDashboardById(dashboardId);
        return dashboard != null;
    }

    private boolean isValidUser(int userId) {
        User user = userDao.findById(userId);
        return user != null;
    }

    public Dashboard changeName(int dbid, String name) {
        Dashboard dashboard = getDashboardById(dbid);
        dashboard.setName(name);
        getCurrentSession().update(dashboard);
        return dashboard;
    }

    @Override
    public List<Workspace> listWorkspaces() {
        return workspaceDao.listAllWorkspaces();
    }

    @Override
    public List<Dashboard> getDashboardsInWorkspace(int workspaceId) {
        Workspace workspace = workspaceDao.findWorkspaceById(workspaceId);
        if (workspace == null) {
            return null;
        } else {
            List<Dashboard> dashboards = new LinkedList<Dashboard>();
            Set<WorkspaceDashboardEntry> entries = workspace.getDashboards();
            if (entries != null && entries.size() > 0) {
                for (WorkspaceDashboardEntry entry : entries) {
                    int dashboardId = entry.getId().getDashboardId();
                    Dashboard dashboard = dashboardDao.findDashboardById(dashboardId);
                    if (dashboard != null) {
                        dashboards.add(dashboard);
                    }
                }
            }
            return dashboards;
        }
    }

    @Override
    public List<Dashboard> getDashboardsInWorkspace(String workspaceName) {
        Workspace workspace = workspaceDao.findWorkspaceByName(workspaceName);
        if (workspace != null) {
            return getDashboardsInWorkspace(workspace.getId());
        } else {
            return (List<Dashboard>) Collections.EMPTY_LIST;
        }
    }

    @Override
    public String getDefaultWorkspaceName() {
        List<Workspace> workspaces = workspaceDao.listAllWorkspacesOrderById();
        if (workspaces != null && workspaces.size() > 0) {
            return workspaces.get(0).getName();
        }
        return null;
    }

    @Override
    public Workspace getWorkspaceById(int workspaceId) {
        return workspaceDao.findWorkspaceById(workspaceId);
    }

    @Override
    public void addDashboard2Workspace(int workspaceId, int dashboardId) throws QualityDashboardException {
        Session session = getCurrentSession();

        Dashboard dashboard = dashboardDao.findDashboardById(dashboardId);
        if (dashboard == null) {
            throw new QualityDashboardException("Invalid dashboard");
        }

        Workspace workspace = workspaceDao.findWorkspaceById(workspaceId);
        if (workspace == null) {
            throw new QualityDashboardException("Invalid workspace");
        }
        List<Dashboard> workspaces = getDashboardsInWorkspace(workspaceId);
        WorkspaceDashboardEntry entry = workspaceDao.getWorkspaceById(workspaceId, dashboardId);
        if (entry != null) {
            throw new QualityDashboardException("Dasnboard already in this workspace");
        } else {
            entry = new WorkspaceDashboardEntry();
            entry.setId(new WorkspaceDashboardPK(workspaceId, dashboardId));
            entry.setOrder(nextRankOfWorkspaceDashboard(workspaceId));
            session.save(entry);
        }
    }

    @Override
    public void deleteDashboardFromWorkspace(int workspaceId, int dashboardId) throws QualityDashboardException {
        Session session = getCurrentSession();

        Dashboard dashboard = dashboardDao.findDashboardById(dashboardId);
        if (dashboard == null) {
            throw new QualityDashboardException("Invalid dashboard");
        }

        Workspace workspace = workspaceDao.findWorkspaceById(workspaceId);
        if (workspace == null) {
            throw new QualityDashboardException("Invalid workspace");
        }

        WorkspaceDashboardEntry entry = workspaceDao.getWorkspaceById(workspaceId, dashboardId);
        if (entry == null) {
            throw new QualityDashboardException("Dasnboard is not in this workspace");
        } else {
            session.delete(entry);

        }
    }

    @Override
    public void createWorkspace(String workspaceName) throws QualityDashboardException {
        Session session = getCurrentSession();

        Workspace workspace = workspaceDao.findWorkspaceByName(workspaceName);
        if (workspace != null) {
            throw new QualityDashboardException("Workspace is exist!");
        } else {
            workspace = new Workspace();
            workspace.setId(nextRankOfWorkspace());
            workspace.setName(workspaceName);
            session.save(workspace);
        }
    }

    @Override
    public void deleteWorkspace(int workspaceId) throws QualityDashboardException {
        Session session = getCurrentSession();

        Workspace workspace = workspaceDao.findWorkspaceById(workspaceId);
        List<WorkspaceDashboardEntry> entrys = session.createQuery("FROM WorkspaceDashboardEntry where id.workspaceId = ?").setParameter(0, workspaceId).list();
        for (WorkspaceDashboardEntry entry : entrys) {
            session.delete(entry);
        }

        if (workspace == null) {
            throw new QualityDashboardException("Workspace is not exist!");
        } else {
            session.delete(workspace);
        }
    }
}
