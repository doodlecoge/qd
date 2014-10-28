package com.webex.qd.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 3:18 PM
 */
@Embeddable
public class UserDashboardPK implements Serializable {

    @Column(name = "user_id")
    private int userId;

    @Column(name = "dashboard_id")
    private int dashboardId;

    public UserDashboardPK() {}

    public UserDashboardPK(int userId, int dashboardId) {
        this.userId = userId;
        this.dashboardId = dashboardId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }
}
