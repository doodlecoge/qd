package com.webex.qd.vo;

import javax.persistence.*;
import java.util.Date;


/**
 * Created by chenshijie on 3/21/14.
 */
@Entity
@Table(name = "daily_changes")
public class DailyChanges {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "time")
    private Date time;

    @Column(name = "repo")
    private String repo;

    @Column(name = "branch")
    private String branch;

    @Column(name = "user")
    private String user;

    @Column(name = "added")
    private Integer add;

    @Column(name = "modified")
    private Integer modified;

    @Column(name = "deleted")
    private Integer delete;

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setAdd(Integer add) {
        this.add = add;
    }

    public void setModified(Integer modified) {
        this.modified = modified;
    }

    public void setDelete(Integer delete) {
        this.delete = delete;
    }

    public int getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public String getRepo() {
        return repo;
    }

    public String getBranch() {
        return branch;
    }

    public String getUser() {
        return user;
    }

    public Integer getAdd() {
        return add;
    }

    public Integer getModified() {
        return modified;
    }

    public Integer getDelete() {
        return delete;
    }
}
