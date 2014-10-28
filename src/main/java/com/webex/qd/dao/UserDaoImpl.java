package com.webex.qd.dao;

import com.webex.qd.vo.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 3:01 PM
 */
@Repository("userDao")
@Transactional
public class UserDaoImpl extends BaseDao implements UserDao {
    public User findByUserName(String username) {
        List<User> lst = getCurrentSession().createQuery("FROM User where username= :username").setParameter("username", username).list();
        if (lst == null || lst.size() == 0) {
            return null;
        }
        return lst.get(0);
    }

    @Override
    public User findById(int id) {
        return (User) getCurrentSession().get(User.class, id);
    }

    public User addUser(String username, String fullName) {
        User user = new User();
        user.setUsername(username);
        user.setFullname(fullName);
        user.setEnabled(true);

        try {
            getCurrentSession().save(user);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return getCurrentSession().createQuery("from User order by fullname asc").list();
    }

    @Override
    public List<User> getDashboardOwners() {
        return getCurrentSession().createQuery("from User u where exists (select uid from Dashboard where uid=u.id)").list();
    }

    @Override
    public List<User> getAdmins() {
        return getCurrentSession().createQuery("from User u where exists (select id.userId from UserDashboardEntry where id.userId = u.id)").list();
    }

}
