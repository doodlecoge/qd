package com.webex.qd.dao;

import com.webex.qd.vo.GroupDashboardEntry;
import com.webex.qd.vo.GroupDashboardPK;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 9:38 AM
 */
@Repository("groupDao")
@Transactional
public class GroupDaoImpl extends BaseDao implements GroupDao {
    @Override
    public GroupDashboardEntry getGroupById(int groupId, int dashboardId) {
        return (GroupDashboardEntry) getCurrentSession().get(GroupDashboardEntry.class, new GroupDashboardPK(groupId, dashboardId));
    }
}
