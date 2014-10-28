package com.webex.qd.controller;

import com.webex.qd.api.ApiResult;
import com.webex.qd.api.ErrorResult;
import com.webex.qd.dao.UserDao;
import com.webex.qd.exception.QualityDashboardException;
import com.webex.qd.service.DashboardService;
import com.webex.qd.spring.security.AuthUtil;
import com.webex.qd.vo.Dashboard;
import com.webex.qd.vo.User;
import com.webex.qd.vo.Widget;
import com.webex.qd.widget.Layout;
import com.webex.qd.widget.WidgetManager;
import net.sf.json.JSONException;
import org.apache.velocity.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Author: justin
 * Date: 7/11/13
 * Time: 4:23 PM
 */
@Controller
@RequestMapping("/dashboard")
@Transactional
public class DashboardCrudController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private WidgetManager widgetManager;

    @RequestMapping(value = "add_db", method = RequestMethod.POST)
    public
    @ResponseBody
    ApiResult addDashboard(@RequestParam("name") String name,
                           @RequestParam("columns") int columns,
                           @RequestParam("gid") int gid) {
        ApiResult result = ApiResult.FAILURE;

        if (columns != 2 && columns != 3) {
            result.putExt("errMsg", "Dashboard allows only 2 ~ 3 columns!");
            return result;
        }

        try {
            User user = userDao.findByUserName(AuthUtil.getUsername());
            if (user == null) {
                result.putExt("errMsg", "Create dashboard for nobody");
                return result;
            }
            int uid = user.getId();
            Dashboard db = dashboardService.createDashboard(uid, name, columns, gid);
            result = ApiResult.SUCCESS;
            result.putExt("dbid", db.getId());
        } catch (Exception e) {
            e.printStackTrace();
            result.putExt("errMsg", e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "copy_db", method = RequestMethod.POST)
    public
    @ResponseBody
    ApiResult copyDashboard(@RequestParam("name") String name,
                           @RequestParam("columns") int columns,
                           @RequestParam("dbid") int dbid) {
        ApiResult result = ApiResult.FAILURE;

        if (columns != 2 && columns != 3) {
            result.putExt("errMsg", "Dashboard allows only 2 ~ 3 columns!");
            return result;
        }

        try {
            User user = userDao.findByUserName(AuthUtil.getUsername());
            if (user == null) {
                result.putExt("errMsg", "Create dashboard for nobody");
                return result;
            }

            Dashboard dbDst = dashboardService.cloneDashboard(dashboardService.getDashboardById(dbid), user.getId(), name);
            result = ApiResult.SUCCESS;
            result.putExt("dbid", dbDst.getId());
        } catch (Exception e) {
            e.printStackTrace();
            result.putExt("errMsg", e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "del_db")
    public
    @ResponseBody
    ApiResult deleteDashboard(@RequestParam("dbid") int dashboardId) {
        ApiResult result = ApiResult.FAILURE;

        User user = userDao.findByUserName(AuthUtil.getUsername());
        Dashboard dashboard = dashboardService.getDashboardById(dashboardId);

        if (dashboard == null) {
            result.setStatusMessage("Dashboard Not Found");
            return result;
        }

        if (user == null || user.getId() != dashboard.getUid()) {
            result.setStatusMessage("Permission Denied!");
            return result;
        }

        try {
            dashboardService.deleteDashboard(dashboardId);
            return ApiResult.SUCCESS;
        } catch (Exception e) {
            result.setStatusMessage(e.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "follow_dashboard")
    public
    @ResponseBody
    ApiResult followDashboard(@RequestParam("dbid") int dashboardId) {
        User user = userDao.findByUserName(AuthUtil.getUsername());
        if (user == null) {
            return new ErrorResult("Authentication failed");
        }

        Dashboard dashboard = dashboardService.getDashboardById(dashboardId);
        if (dashboard == null) {
            return new ErrorResult("Invalid dashboard");
        }

        try {
            dashboardService.followDashboard(user.getId(), dashboardId);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }


    @RequestMapping(value = "unfollow_dashboard")
    public
    @ResponseBody
    ApiResult unfollowDashboard(@RequestParam("dbid") int dashboardId) {
        User user = userDao.findByUserName(AuthUtil.getUsername());
        if (user == null) {
            return new ErrorResult("Authentication failed");
        }

        Dashboard dashboard = dashboardService.getDashboardById(dashboardId);
        if (dashboard == null) {
            return new ErrorResult("Invalid dashboard");
        }

        try {
            dashboardService.unfollowDashboard(user.getId(), dashboardId);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }

    @RequestMapping(value = "toggle_fav")
    public
    @ResponseBody
    ApiResult toggleFavorite(@RequestParam("dbid") int dashboardId) {
        User user = userDao.findByUserName(AuthUtil.getUsername());
        if (user == null) {
            return new ErrorResult("Authentication failed");
        }

        Dashboard dashboard = dashboardService.getDashboardById(dashboardId);
        if (dashboard == null) {
            return new ErrorResult("Invalid dashboard");
        }

        if (dashboardService.isUserFollowedDashboard(user.getId(), dashboardId)) {
            return unfollowDashboard(dashboardId);
        } else {
            return followDashboard(dashboardId);
        }
    }


    @RequestMapping(value = "save_userdashboard_orders")
    public
    @ResponseBody
    ApiResult saveUserDashboardOrders(@RequestParam("ids") String ids) {
        User user = userDao.findByUserName(AuthUtil.getUsername());
        if (user == null) {
            return new ErrorResult("Authentication failed");
        }
        int userId = user.getId();
        String[] idArray = StringUtils.split(ids, ",");
        int[] dashboardIds = new int[idArray.length];
        for (int i = 0; i < idArray.length; i++) {
            try {
                dashboardIds[i] = Integer.valueOf(idArray[i]);
            } catch (NumberFormatException e) {
                return new ErrorResult("Invalid dashboard id provided");
            }
        }

        try {
            dashboardService.saveUserDashboardOrders(userId, dashboardIds);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }


    @RequestMapping(value = "add_child_to_group")
    public
    @ResponseBody
    ApiResult addChild2Group(@RequestParam("gid") int groupId, @RequestParam("cid") int childId) {
        try {
            dashboardService.addChild2Group(groupId, childId);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }

    @RequestMapping(value = "remove_child_from_group")
    public
    @ResponseBody
    ApiResult removeChildFromGroup(@RequestParam("gid") int groupId,
                                   @RequestParam("cid") int childId) {
        try {
            dashboardService.removeChildFromGroup(groupId, childId);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }

    @RequestMapping(value = "save_groupdashboard_orders")
    public
    @ResponseBody
    ApiResult saveGroupDashboardOrders(@RequestParam("gid") int groupId,
                                       @RequestParam("ids") String ids) {
        String[] idArray = StringUtils.split(ids, ",");
        int[] dashboardIds = new int[idArray.length];
        for (int i = 0; i < idArray.length; i++) {
            try {
                dashboardIds[i] = Integer.valueOf(idArray[i]);
            } catch (NumberFormatException e) {
                return new ErrorResult("Invalid dashboard id provided");
            }
        }
        try {
            dashboardService.saveGroupDashboardOrders(groupId, dashboardIds);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }

    @RequestMapping(value = "save_workspacedashboard_orders")
    public
    @ResponseBody
    ApiResult saveWorkspaceDashboardOrders(@RequestParam("wsId") int workspaceId,
                                       @RequestParam("ids") String ids) {
        String[] idArray = StringUtils.split(ids, ",");
        int[] dashboardIds = new int[idArray.length];
        for (int i = 0; i < idArray.length; i++) {
            try {
                dashboardIds[i] = Integer.valueOf(idArray[i]);
            } catch (NumberFormatException e) {
                return new ErrorResult("Invalid dashboard id provided");
            }
        }
        try {
            dashboardService.saveWorkspaceDashboardOrders(workspaceId, dashboardIds);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }

    @RequestMapping(value = "add_widget")
    public
    @ResponseBody
    ApiResult addWidget(@RequestParam("dashboardId") int dashboardId,
                        @RequestParam("type") String type,
                        @RequestParam("name") String name) {
        Dashboard dashboard = dashboardService.getDashboardById(dashboardId);
        if (dashboard == null) {
            return new ErrorResult("Invalid dashboard");
        }

        if (!widgetManager.isValidType(type)) {
            return new ErrorResult("Invalid widget type");
        }

        Widget widget = dashboardService.createWidgetForDashboard(dashboardId, type, name);
        ApiResult success = new ApiResult(ApiResult.SUCCESS_CODE);
        success.putExt("widgetId", widget.getId());
        return success;
    }


    @RequestMapping(value = "delete_widget")
    public
    @ResponseBody
    ApiResult deleteWidget(@RequestParam("widgetId") int widgetId) {
        dashboardService.deleteWidget(widgetId);
        return ApiResult.SUCCESS;
    }


    @RequestMapping(value = "save_layout")
    public
    @ResponseBody
    ApiResult saveDashboardLayout(@RequestParam("dashboardId") int dashboardId,
                                  @RequestParam("layout") String layout) {
        Layout layoutObj;
        try {
            layoutObj = Layout.fromJson(layout);
        } catch (JSONException e) {
            return new ErrorResult("Invalid layout");
        }

        if (layoutObj == null) {
            return new ErrorResult("Invalid layout");
        }

        Dashboard dashboard = dashboardService.getDashboardById(dashboardId);
        if (dashboard == null) {
            return new ErrorResult("Invalid dashboard");
        }

        dashboard.setLayout(layout);
        dashboardService.saveDashboard(dashboard);
        return ApiResult.SUCCESS;
    }


    @RequestMapping(value = "add_db_user")
    public
    @ResponseBody
    ApiResult addUser2Dashboard(@RequestParam("dbid") int dashboardId,
                                @RequestParam("role") int role,
                                @RequestParam("uid") int userId) {
        try {
            dashboardService.addUser2Dashboard(userId, dashboardId, role);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }

    @RequestMapping(value = "del_db_user")
    public
    @ResponseBody
    ApiResult removeUserFromDashboard(@RequestParam("dbid") int dashboardId,
                                      @RequestParam("uid") int userId) {
        try {
            dashboardService.removeUserFromDashboard(userId, dashboardId);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }


    @RequestMapping(value = "chg_db_name")
    public
    @ResponseBody
    String chageDashboardName(@RequestParam("id") int id, @RequestParam("name") String name) {

        Dashboard db = dashboardService.changeName(id, name);

        if (db == null) {
            return "{}";
        } else {
            return "{id:" + db.getId() + ",name:\"" + db.getName().replace("\"", "\\\"") + "\"}";
        }
    }

    @RequestMapping(value = "add_db_2_ws", method = RequestMethod.POST)
    public
    @ResponseBody
    ApiResult addDashboard2workspace(@RequestParam("workspaceId") int workspaceId,
                                     @RequestParam("dashboardId") int dashboardId) {
        try {
            dashboardService.addDashboard2Workspace(workspaceId, dashboardId);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }

    @RequestMapping(value = "delete_db_from_ws", method = RequestMethod.POST)
    public
    @ResponseBody
    ApiResult deleteDashboardFromWorkspace(@RequestParam("workspaceId") int workspaceId,
                                     @RequestParam("dashboardId") int dashboardId) {
        try {
            dashboardService.deleteDashboardFromWorkspace(workspaceId, dashboardId);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }

    @RequestMapping(value = "create_workspace", method = RequestMethod.POST)
    public
    @ResponseBody
    ApiResult createWorkspace(@RequestParam("workspaceName") String workspaceName) {
        try {
            dashboardService.createWorkspace(workspaceName);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }

    @RequestMapping(value = "delete_workspace", method = RequestMethod.POST)
    public
    @ResponseBody
    ApiResult createWorkspace(@RequestParam("workspaceId") int workspaceId) {
        try {
            dashboardService.deleteWorkspace(workspaceId);
        } catch (QualityDashboardException e) {
            return new ErrorResult(e.getMessage());
        }
        return ApiResult.SUCCESS;
    }
}
