package com.webex.qd.controller;

import com.webex.qd.dao.UserDao;
import com.webex.qd.vo.User;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * author:Tony Wang
 * version:1.0
 * date:12/19/13
 */
@Controller
@RequestMapping("/user")
public class UserListController {
    @Autowired
    private UserDao userDao;

    @RequestMapping("/list/all")
    @ResponseBody
    public String getAllUser() {
        List<User> userList = userDao.getAllUsers();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getId() - o2.getId();
            }
        });
        String json = "{}";
        try {
            mapper.writeValue(stringWriter, userList);
            json = stringWriter.toString();
        } catch (IOException e) {
        }
        return json;
    }
    @RequestMapping("/list/owner")
    @ResponseBody
    public String getAllOwner() {
        List<User> userList = userDao.getDashboardOwners();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getId() - o2.getId();
            }
        });
        String json = "{}";
        try {
            mapper.writeValue(stringWriter, userList);
            json = stringWriter.toString();
        } catch (IOException e) {
        }
        return json;
    }
    @RequestMapping("/list/admin")
    @ResponseBody
    public String getAdmin() {
        List<User> userList = userDao.getAdmins();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getId() - o2.getId();
            }
        });
        String json = "{}";
        try {
            mapper.writeValue(stringWriter, userList);
            json = stringWriter.toString();
        } catch (IOException e) {
        }
        return json;
    }

}
