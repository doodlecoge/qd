package com.webex.qd.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 3:01 PM
 */
public class BaseDao {

    @Autowired
    @Qualifier(value = "sf_qd")
    private SessionFactory sessionFactory;

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
