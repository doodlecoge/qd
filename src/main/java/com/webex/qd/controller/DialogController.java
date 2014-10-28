package com.webex.qd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author: justin
 * Date: 7/11/13
 * Time: 4:18 PM
 */
@Controller
@RequestMapping("/dashboard")
@Transactional
public class DialogController {

    @RequestMapping("add_db_panel")
    public String addDashboardDialog() {
        return "/partial/add_db_panel";
    }

    @RequestMapping("copy_db_panel")
    public String copyDashboardDialog() {
        return "/partial/copy_db_panel";
    }
}
