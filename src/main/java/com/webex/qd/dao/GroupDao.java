package com.webex.qd.dao;

import com.webex.qd.vo.GroupDashboardEntry;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 9:38 AM
 */
public interface GroupDao {
    GroupDashboardEntry getGroupById(int groupId, int dashboardId);
}
