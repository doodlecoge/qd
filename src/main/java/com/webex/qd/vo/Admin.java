package com.webex.qd.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Author: justin
 * Date: 7/17/13
 * Time: 1:17 PM
 */
@Entity
@Table(name="admin_users")
public class Admin {

    @Column(name="username")
    @Id
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
