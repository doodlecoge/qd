package com.webex.qd.vo;

import javax.persistence.*;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 3:41 PM
 */
@Entity
@Table(name="widgets")
public class Widget {
    @Id
    @GeneratedValue
    private int id;
    private String setting;

    @Column(name = "dashboard_id")
    private int dashboardId;
    private String type;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public int getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
