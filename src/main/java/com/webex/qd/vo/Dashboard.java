package com.webex.qd.vo;

import com.webex.qd.widget.Layout;

import javax.persistence.*;
import java.util.Set;

/**
 * Author: justin
 * Date: 7/3/13
 * Time: 2:56 PM
 */
@Entity
@Table(name="dashboards")
public class Dashboard {

    @Id
    @GeneratedValue
    private int id;

    private String name;
    private String layout;
    private int uid;


    @Column(name="is_grp")
    private int isGrp;


    @OneToMany(mappedBy = "id.groupId", targetEntity = GroupDashboardEntry.class, fetch = FetchType.EAGER)
    @OrderBy("rank asc")
    private Set<GroupDashboardEntry> groupDashboardEntries;


    @OneToMany(mappedBy = "dashboardId", targetEntity = Widget.class)
    private Set<Widget> widgets;


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

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getGrp() {
        return isGrp;
    }

    public void setGrp(int grp) {
        isGrp = grp;
    }

    public boolean isGroup() {
        return isGrp == 1;
    }


    public Set<GroupDashboardEntry> getGroupDashboardEntries() {
        return groupDashboardEntries;
    }

    public void setGroupDashboardEntries(Set<GroupDashboardEntry> groupDashboardEntries) {
        this.groupDashboardEntries = groupDashboardEntries;
    }

    public Set<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(Set<Widget> widgets) {
        this.widgets = widgets;
    }

    @SuppressWarnings("unused")
    public Layout getLayoutObject() {
        return Layout.fromJson(getLayout());
    }


    @SuppressWarnings("unused")
    public Widget getWidgetById(int id) {
        for (Widget widget : widgets) {
            if (widget.getId() == id) {
                return widget;
            }
        }
        return null;
    }

    public boolean contains(int dashboardId) {
        for (GroupDashboardEntry entry : this.groupDashboardEntries) {
            if (entry.getId().getDashboardId() == dashboardId) {
                return true;
            }
        }
        return false;
    }
}
