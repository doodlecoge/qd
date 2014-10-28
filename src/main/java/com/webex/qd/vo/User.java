package com.webex.qd.vo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 2:58 PM
 */
@Entity
@Table(name="users")
@JsonIgnoreProperties(value = {"dashboards", "enabled"})
public class User implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    private String username;
    private String fullname;
    private boolean enabled;

    @OneToMany(mappedBy = "id.userId", targetEntity = UserDashboardEntry.class, fetch = FetchType.EAGER)
    @OrderBy("rank asc")
    private Set<UserDashboardEntry> dashboards;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<UserDashboardEntry> getDashboards() {
        return dashboards;
    }

    public void setDashboards(Set<UserDashboardEntry> dashboards) {
        this.dashboards = dashboards;
    }

    public boolean isAdminOfDashboard(int dashboardId) {
        for (UserDashboardEntry entry : dashboards) {
            if (entry.getId().getDashboardId() == dashboardId) {
                if (entry.getRole() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFollowerOfDashboard(int dashboardId) {
        for (UserDashboardEntry entry : dashboards) {
            if (entry.getId().getDashboardId() == dashboardId) {
                return true;
            }
        }
        return false;
    }
}
