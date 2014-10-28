package com.webex.qd.dao;

import com.webex.qd.vo.Dashboard;
import com.webex.qd.vo.UserDashboardEntry;

import java.util.List;

/**
 * Author: justin
 * Date: 7/3/13
 * Time: 2:54 PM
 */
public interface DashboardDao {
    Dashboard findDashboardById(int id);

    void saveDashboard(Dashboard dashboard);

    List<Dashboard> findUsersOwnByUserId(int userId);

    List<UserDashboardEntry> findUsersAdministrationsByUserId(int userId);

    List<UserDashboardEntry> findUsersFavoritesByUserId(int userId);

    List<Dashboard> findUsersCreatedDashboardByUserId(int userId);
}
