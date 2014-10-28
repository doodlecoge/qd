package com.webex.qd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 3:33 PM
 */
@Controller
@SessionAttributes({"username", "base", "admin", "principal"})
public class AppBaseController {
}
