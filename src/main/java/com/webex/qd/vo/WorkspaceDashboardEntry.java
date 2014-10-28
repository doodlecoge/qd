package com.webex.qd.vo;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-4
 * Time: 上午11:38
 */
@Entity
@Table(name="workspaces_dashboards")
public class WorkspaceDashboardEntry {
    @EmbeddedId
    private WorkspaceDashboardPK id;

    @Column(name="[order]")
    private int order;


    public WorkspaceDashboardPK getId() {
        return id;
    }

    public void setId(WorkspaceDashboardPK id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
