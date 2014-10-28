package com.webex.qd.dao;

import com.webex.qd.vo.Workspace;
import com.webex.qd.vo.WorkspaceDashboardEntry;
import com.webex.qd.vo.WorkspaceDashboardPK;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-4
 * Time: 下午2:11
 */
@Repository("workspaceDao")
@Transactional
public class WorkspaceDaoImpl extends BaseDao implements WorkspaceDao {
    @Override
    public WorkspaceDashboardEntry getWorkspaceById(int workspaceId, int dashboardId) {
        return (WorkspaceDashboardEntry) getCurrentSession().get(WorkspaceDashboardEntry.class, new WorkspaceDashboardPK(workspaceId, dashboardId));
    }

    @Override
    public Workspace findWorkspaceById(int id) {
        return (Workspace) getCurrentSession().get(Workspace.class, id);
    }


    @Override
    public Workspace findWorkspaceByName(String name) {
        List<Workspace> lst = getCurrentSession().createQuery("FROM Workspace where name= :name").setParameter("name", name).list();
        if (lst == null || lst.size() == 0) {
            return null;
        }
        return lst.get(0);
    }

    @Override
    public List<Workspace> listAllWorkspaces() {
        return getCurrentSession().createQuery("FROM Workspace ORDER BY name ASC").list();
    }

    @Override
    public List<Workspace> listAllWorkspacesOrderById() {
        return getCurrentSession().createQuery("FROM Workspace ORDER BY id ASC").list();
    }
}
