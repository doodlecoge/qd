package com.webex.qd.dao;

import com.webex.qd.vo.Admin;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: justin
 * Date: 7/17/13
 * Time: 1:20 PM
 */
@Repository("adminDao")
@Transactional
public class AdminDaoImpl extends BaseDao implements AdminDao {
    @Override
    public boolean isAdmin(String userName) {
        if (StringUtils.isBlank(userName)) {
            return false;
        }
        Admin admin = (Admin) getCurrentSession().get(Admin.class, userName);
        return admin != null;
    }
}
