package com.webex.qd.vo;

import javax.persistence.*;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 9:36 AM
 */
@Entity
@Table(name="groups_dashboards")
public class GroupDashboardEntry {

    @EmbeddedId
    private GroupDashboardPK id;

    private int rank;

    @OneToOne(targetEntity = Dashboard.class, fetch = FetchType.EAGER)
    @JoinColumns(
            @JoinColumn(name = "db_id",insertable = false, updatable = false)
    )
    private Dashboard dashboard;

    public GroupDashboardPK getId() {
        return id;
    }

    public void setId(GroupDashboardPK id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
}
