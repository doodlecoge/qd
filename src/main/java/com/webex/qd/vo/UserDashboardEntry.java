package com.webex.qd.vo;

import javax.persistence.*;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 3:26 PM
 */
@Entity
@Table(name="users_dashboards")
public class UserDashboardEntry {
    @EmbeddedId
    private UserDashboardPK id;

    private int role;
    private int rank;

    @OneToOne(targetEntity = Dashboard.class)
    @JoinColumns(
            @JoinColumn(name = "dashboard_id",insertable = false, updatable = false)
    )
    private Dashboard dashboard;

    @OneToOne(targetEntity = User.class)
    @JoinColumns(
            @JoinColumn(name = "user_id", insertable = false, updatable = false)
    )
    private User user;

    public UserDashboardPK getId() {
        return id;
    }

    public void setId(UserDashboardPK id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
