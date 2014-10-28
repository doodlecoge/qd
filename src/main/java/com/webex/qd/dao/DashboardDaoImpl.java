package com.webex.qd.dao;

import com.webex.qd.vo.Dashboard;
import com.webex.qd.vo.UserDashboardEntry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Author: justin
 * Date: 7/3/13
 * Time: 2:56 PM
 */
@Repository("dashboardDao")
@Transactional
public class DashboardDaoImpl extends BaseDao implements DashboardDao {

    @Override
    public Dashboard findDashboardById(int id) {
        return (Dashboard) getCurrentSession().get(Dashboard.class, id);
    }

    @Override
    public void saveDashboard(Dashboard dashboard) {
        getCurrentSession().saveOrUpdate(dashboard);
    }

    @Override
    public List<Dashboard> findUsersOwnByUserId(int userId) {
        return getCurrentSession().createQuery("from Dashboard a where a.uid = ? order by a.name asc").setParameter(0, userId).list();
    }

    @Override
    public List<UserDashboardEntry> findUsersAdministrationsByUserId(int userId) {
        return getCurrentSession().createQuery("from UserDashboardEntry a where a.user.id = ? and a.role = 0 order by a.dashboard.name asc").setParameter(0, userId).list();
    }

    @Override
    public List<UserDashboardEntry> findUsersFavoritesByUserId(int userId) {
        return getCurrentSession().createQuery("from UserDashboardEntry a where a.user.id = ? and a.role = 1 order by a.dashboard.name asc").setParameter(0, userId).list();
    }

    @Override
    public List<Dashboard> findUsersCreatedDashboardByUserId(int userId) {
        return getCurrentSession().createQuery("from Dashboard a where a.uid = ?").setParameter(0, userId).list();
    }
}

