package com.webex.qd.dao;

import com.webex.qd.vo.User;

import java.util.List;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 2:56 PM
 */
public interface UserDao {
    User findByUserName(String username);

    User findById(int id);

    User addUser(String username, String fullName);

    List<User> getAllUsers();

    List<User> getDashboardOwners();
    List<User> getAdmins();

}
