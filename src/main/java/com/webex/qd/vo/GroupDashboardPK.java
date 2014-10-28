package com.webex.qd.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 9:46 AM
 */
@Embeddable
public class GroupDashboardPK implements Serializable {

    @Column(name="grp_id")
    private int groupId;

    @Column(name = "db_id")
    private int dashboardId;

    public GroupDashboardPK() {

    }

    public GroupDashboardPK(int groupId, int dashboardId) {
        this.groupId = groupId;
        this.dashboardId = dashboardId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }
}
