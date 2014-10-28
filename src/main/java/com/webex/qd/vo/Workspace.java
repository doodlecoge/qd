package com.webex.qd.vo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-4
 * Time: 上午10:57
 */
@Entity
@Table(name="workspaces")
public class Workspace implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @OneToMany(mappedBy = "id.workspaceId", targetEntity = WorkspaceDashboardEntry.class, fetch = FetchType.EAGER)
    @OrderBy("order asc")
    private Set<WorkspaceDashboardEntry> dashboards;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<WorkspaceDashboardEntry> getDashboards() {
        return dashboards;
    }

    public void setDashboards(Set<WorkspaceDashboardEntry> dashboards) {
        this.dashboards = dashboards;
    }


}
