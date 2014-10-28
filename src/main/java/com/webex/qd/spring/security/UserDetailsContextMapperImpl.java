package com.webex.qd.spring.security;

import com.webex.qd.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 3:42 PM
 */
public class UserDetailsContextMapperImpl implements UserDetailsContextMapper {

    @Autowired
    private UserDao userDao;

    public UserDetails mapUserFromContext(
            DirContextOperations ctx,
            String username,
            Collection<? extends GrantedAuthority> auths) {


        com.webex.qd.vo.User u = userDao.findByUserName(username);
        String fullName = ctx.getStringAttribute("cn");

        // add user to db if not exists
        if(u == null) {
            u = userDao.addUser(username, fullName);
        }

        u = userDao.findByUserName(username);

        if(u == null) {
            return null;
        }

        InetOrgPerson.Essence p = new InetOrgPerson.Essence(ctx);

        p.setUsername(username);
        p.setDisplayName(fullName);

        p.setAuthorities(auths);

        return  p.createUserDetails();
    }

    public void mapUserToContext(UserDetails user,
                                 DirContextAdapter ctx) {
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}