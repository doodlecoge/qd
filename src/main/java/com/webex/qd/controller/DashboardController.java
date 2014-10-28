package com.webex.qd.controller;

import com.webex.qd.dao.AdminDao;
import com.webex.qd.dao.CECInfoDao;
import com.webex.qd.dao.DashboardDao;
import com.webex.qd.dao.UserDao;
import com.webex.qd.service.DashboardService;

import com.webex.qd.spring.security.AuthUtil;
import com.webex.qd.util.QdProperties;
import com.webex.qd.vo.*;
import com.webex.qd.widget.WidgetManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Author: justin
 * Date: 7/5/13
 * Time: 4:35 PM
 */
@Controller
@RequestMapping("/dashboard")
@Transactional
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private WidgetManager widgetManager;

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private CECInfoDao cecDao;

    @RequestMapping(method = {RequestMethod.GET})
    public String indexPage(@CookieValue(value = "workspace", required = false) String workspace, ModelMap model, HttpServletResponse response) {
        if (workspace == null) {
            workspace = dashboardService.getDefaultWorkspaceName();
            if (workspace != null) {
                Cookie cookie = new Cookie("workspace", workspace);
                cookie.setPath("/");
                cookie.setMaxAge(Integer.MAX_VALUE);
                response.addCookie(cookie);
            }
        }
        List<Dashboard> dashboards = dashboardService.getSystemDashboards(workspace);
        List<Dashboard> nonGroups = removeChildDashboards(dashboards);

        appendMyFavoritesInfo(model, userDao.findByUserName(AuthUtil.getUsername()));

        if (nonGroups.size() == 0) {
            return "/dashboard/empty_workspace";
        }
        return "redirect:/dashboard/" + nonGroups.get(0).getId();
    }

    @RequestMapping(value = "save_ws", method = {RequestMethod.GET})
    public String chooseWorkspace(@RequestParam("workspace") String workspace, HttpServletResponse response) {
        Cookie cookie = new Cookie("workspace", workspace);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
        return "redirect:/dashboard";
    }


    @RequestMapping(value = "/{id}", method = {RequestMethod.GET})
    public String indexPage(@CookieValue(value = "workspace", required = false) String workspace, @PathVariable("id") int id, ModelMap model) {
        Dashboard dashboard = dashboardService.getDashboardById(id);
        List<Dashboard> dashboards = dashboardService.getSystemDashboards(workspace);
        List<Dashboard> nonChildren = removeChildDashboards(dashboards);

        if (dashboard == null) {
            // accessing an invalid dashboard, redirect to the first valid dashboard
            if (nonChildren.size() > 0) {
                return "redirect:/dashboard/" + nonChildren.get(0).getId();
            } else {
                return "redirect:/dashboard";
            }
        }

        if (dashboard.isGroup()) {
            // if we are accessing a group, redirect to its first child if possible
            Set<GroupDashboardEntry> children = dashboard.getGroupDashboardEntries();
            if (children.size() > 0) {
                int idOfFirstChild = children.iterator().next().getId().getDashboardId();
                return "redirect:/dashboard/" + idOfFirstChild;
            }
        }

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("allDashboards", nonChildren);
        model.addAttribute("bn", QdProperties.getProperty("qd-build-number"));

        User user = userDao.findByUserName(AuthUtil.getUsername());
        appendMyFavoritesInfo(model, user);
        appendUserInformation(model, dashboard);

        // initialize lazy load objects
        for (Widget widget : dashboard.getWidgets()) {
            continue;
        }
        return "/dashboard/index";
    }

    /**
     * Generate the navigation bar (dashboards list) according to the user, workspace and the current viewing dashboard
     * @param id            id of the current viewing dashboard
     * @param workspace    name of the workspace
     * @param model
     * @return              view name
     */
    @RequestMapping(value = "get_nav_bar", method = {RequestMethod.GET})
    public String navBar(@RequestParam(value = "id", required = false) Integer id, @CookieValue(value = "workspace", required = false) String workspace, ModelMap model) {
        User user = userDao.findByUserName(AuthUtil.getUsername());
        Dashboard dashboard = null;
        if (id != null) {
            dashboard  = dashboardService.getDashboardById(id);
        }
        List<Dashboard> dashboards = dashboardService.getSystemDashboards(workspace);
        List<Dashboard> nonChildren = removeChildDashboards(dashboards);

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("allDashboards", nonChildren);
        model.addAttribute("user", user);
        appendMyFavoritesInfo(model, user);

        // initialize lazy load objects
        if (dashboard != null) {
            for (Widget widget : dashboard.getWidgets()) {
                continue;
            }
        }
        return "/partial/nav_items";
    }


    private void appendUserInformation(ModelMap model, Dashboard dashboard) {
        model.addAttribute("user", userDao.findByUserName(AuthUtil.getUsername()));
        model.addAttribute("admin", adminDao.isAdmin(AuthUtil.getUsername()));
        model.addAttribute("owner", userDao.findById(dashboard.getUid()));
        model.addAttribute("administrators", dashboardService.listUserOfDashboard(dashboard.getId(), 0));
    }

    private void appendMyFavoritesInfo(ModelMap model, User user) {
        List<Dashboard> myOwnedDashboards = new LinkedList<Dashboard>();
        List<Dashboard> myAdministrations = new LinkedList<Dashboard>();
        List<Dashboard> myFavorites = new LinkedList<Dashboard>();
        if (user != null) {
            myOwnedDashboards = dashboardDao.findUsersOwnByUserId(user.getId());
            myOwnedDashboards = removeChildDashboards(myOwnedDashboards);

            for (UserDashboardEntry entry : dashboardDao.findUsersAdministrationsByUserId(user.getId())) {
                myAdministrations.add(entry.getDashboard());
            }
            myAdministrations = removeChildDashboards(myAdministrations);
            filter(myOwnedDashboards, myAdministrations);

            for (UserDashboardEntry entry : dashboardDao.findUsersFavoritesByUserId(user.getId())) {
                myFavorites.add(entry.getDashboard());
            }
            myFavorites = removeChildDashboards(myFavorites);
            filter(myAdministrations, myFavorites);
        }
        model.put("myOwnedDashboards", removeChildDashboards(myOwnedDashboards));
        model.put("myAdministrations", removeChildDashboards(myAdministrations));
        model.put("myFavorites", removeChildDashboards(myFavorites));
    }


    private void filter(final List<Dashboard> filters, List<Dashboard> toBeFiltered) {
        CollectionUtils.filter(toBeFiltered, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return !isContainedIn((Dashboard) object, filters);
            }
        });
    }

    private boolean isContainedIn(Dashboard dashboard, List<Dashboard> dashboards) {
        if (dashboard == null || dashboards == null || dashboards.size() == 0)
            return false;

        for (Dashboard d : dashboards) {
            if (d.getId() == dashboard.getId()) {
                return true;
            }
        }
        return false;
    }


    @RequestMapping(value = "widget_panel", method = RequestMethod.GET)
    public String getWidgetPanel(ModelMap model) {
        model.addAttribute("types", widgetManager.getActiveWidgetTypes());
        return "/partial/widget_panel";
    }

    @RequestMapping(value = "/get_sub_dbs/{id}", method = RequestMethod.GET)
    public @ResponseBody String getChildDashboards(@PathVariable("id") int id) {
        Set<GroupDashboardEntry> children = dashboardService.getDashboardById(id).getGroupDashboardEntries();
        JSONArray result = new JSONArray();
        for (GroupDashboardEntry child : children) {
            JSONObject o = new JSONObject();
            o.put("id", child.getDashboard().getId());
            o.put("name", child.getDashboard().getName());
            result.add(o);
        }
        return result.toString();
    }


    @RequestMapping(value = "edit_db_names", method = RequestMethod.GET)
    public String getEditPanel(ModelMap model) {
        User user = userDao.findByUserName(AuthUtil.getUsername());
        Set<UserDashboardEntry> entries = user.getDashboards();

//        // Remove the child dashboards in the collection
//        List<Dashboard> nonChildDashboards = getNonChildDashboards();
//        Iterator<UserDashboardEntry> iterator = entries.iterator();
//        while(iterator.hasNext()) {
//            UserDashboardEntry entry = iterator.next();
//            boolean notChild = false;
//            for (Dashboard nonChild : nonChildDashboards) {
//                if (nonChild.getId() == entry.getId().getDashboardId()) {
//                    notChild = true;
//                    break;
//                }
//            }
//            if (!notChild) {
//                iterator.remove();
//            }
//        }
        model.addAttribute("dbs", user.getDashboards());
        model.addAttribute("uid", user.getId());
        //TODO: hard coded true, should change to the super admin
        model.addAttribute("admin", true);
        return "/partial/edit_db_names";
    }

    @RequestMapping(value = "workspace_manager", method = RequestMethod.GET)
    public String getWorkspacePanel(ModelMap model) {
        List<Workspace> ws = dashboardService.listWorkspaces();
        model.addAttribute("workspaces", ws);
        return "/partial/workspace_manager";
    }

    @RequestMapping(value = "public_db_manager", method = RequestMethod.GET)
    public String getPublicPanel(@RequestParam("wsId") int workspaceId, ModelMap model) {
        User user = userDao.findByUserName(AuthUtil.getUsername());

        model.addAttribute("uid", user.getId());
        model.addAttribute("dbs", dashboardService.getDashboardsInWorkspace(workspaceId));
        model.addAttribute("workspace", dashboardService.getWorkspaceById(workspaceId));
        model.addAttribute("admin", true);
        return "/partial/public_db_manager";
    }

    @RequestMapping(value = "group_manage_panel", method = RequestMethod.GET)
    public String getGroupManagePanel(@RequestParam("grpId") int groupId, @RequestParam("workspaceId") int workspaceId, @RequestParam("type") int entryType, ModelMap model) {
        User user = userDao.findByUserName(AuthUtil.getUsername());

        model.addAttribute("wsId", workspaceId);
        model.addAttribute("entryType", entryType);
        model.addAttribute("uid", user.getId());
        model.addAttribute("grpDb", dashboardService.getDashboardById(groupId));
        model.addAttribute("children", dashboardService.listChildrenOfGroup(groupId));
        return "partial/db_children";
    }


    @RequestMapping(value = "nongroup_dashboard_names")
    public @ResponseBody String listNonGroupDashboardNames() {
        List<Dashboard> dashboards = dashboardService.listNonGroupDashboards();
        JSONArray array = new JSONArray();
        for (Dashboard dashboard : dashboards) {
            JSONObject o = new JSONObject();
            o.put("id", dashboard.getId());
            o.put("label", dashboard.getId()+":"+dashboard.getName());
            array.add(o);
        }
        return array.toString();
    }

    @RequestMapping(value = "all_dashboard_names")
    public @ResponseBody String listAllDashboardNames() {
        List<Dashboard> dashboards = dashboardService.listAllDashboards();
        JSONArray array = new JSONArray();
        for (Dashboard dashboard : dashboards) {
            JSONObject o = new JSONObject();
            o.put("id", dashboard.getId());
            o.put("label",dashboard.getId()+":" + dashboard.getName());
            array.add(o);
        }
        return array.toString();
    }

    @RequestMapping(value = "list_groups")
    public @ResponseBody String listGroups() {
        User user = userDao.findByUserName(AuthUtil.getUsername());
        JSONArray array = new JSONArray();
        if (user != null) {

            List<Dashboard> groups = dashboardService.listGroupDashboards(user.getId());
            for (Dashboard group : groups) {
                JSONObject o = new JSONObject();
                o.put("id", group.getId());
                o.put("label", group.getName());
                array.add(o);
            }
        }
        return array.toString();
    }

    @RequestMapping(value = "search_db")
    public String getSearchDashboardResultPanel(@RequestParam("key") String key, ModelMap model) {
        List<Dashboard> dashboards = dashboardService.searchDashboardByName(key);

        User user = userDao.findByUserName(AuthUtil.getUsername());
        model.addAttribute("admin", false);
        if (user != null) {
            List<Dashboard> usersDashboards = dashboardService.getDashboardsOfUser(user.getUsername());
            List<Integer> ids = new LinkedList<Integer>();
            for (Dashboard userDashboard : usersDashboards) {
                ids.add(userDashboard.getId());
            }
            model.addAttribute("fav", ids);
            model.addAttribute("user", user);
            model.addAttribute("admin", adminDao.isAdmin(user.getUsername()));
        }
        model.addAttribute("search_term", key);
        model.addAttribute("search_results", dashboards);

        return "/partial/search_results";
    }

    @RequestMapping(value = "projcodes")
    public @ResponseBody String listProjectCodes() {
//        Set<String> set = ApiCaller.GetProjectCodes();
        List<String> names = dashboardService.listDashboardNames();

        JSONArray array = new JSONArray();
        array.addAll(names);
        return array.toString();
    }

    @RequestMapping(value = "get_all_users")
    public @ResponseBody String getAllUsers() {
        List<User> users = userDao.getAllUsers();
        JSONArray array = new JSONArray();
        for (User user : users) {
            JSONObject o = new JSONObject();
            o.put("id", user.getId());
            o.put("name", user.getUsername());
            o.put("fname", user.getFullname());
            array.add(o);
        }
        return array.toString();
    }

    @RequestMapping(value = "get_CECInfo")
    public @ResponseBody String getCECInfoAndInsertDB(@RequestParam("cecid") String cecid,
                                                      @RequestParam("dbid") int dashboardId,
                                                      @RequestParam("role") int role) {


        JSONArray array = new JSONArray();

//        List<UserDashboardEntry> entries = dashboardService.listUserOfDashboard(dashboardId, role);
//
//        for (UserDashboardEntry entry : entries) {
//            User user = userDao.findById(entry.getId().getUserId());
//            JSONObject o = new JSONObject();
//            o.put("id", user.getId());
//            o.put("name", user.getUsername());
//            o.put("fname", user.getFullname());
//            array.add(o);
//        }
//        if(!array.isEmpty()){
//            return array.toString();
//        }
        //db doesn't exist user
        CECInfo info =null ;

        try{
            info = cecDao.getCECInfoByCec(cecid);
        }catch (Exception e){

        }

        //insert user to db
        User user = userDao.findByUserName(info.getUid());
        if(user == null){
            userDao.addUser(info.getUid(),info.getGivenName()+" "+info.getSn());
        }
        user = userDao.findByUserName(info.getUid());

        if(info != null){
            JSONObject o = new JSONObject();
            o.put("id", user.getId());
            o.put("name", user.getUsername());
            o.put("fname", user.getFullname());
            array.add(o);
        }
        return array.toString();
    }

    @RequestMapping(value = "get_db_user")
    public @ResponseBody String getDashboardAdmins(@RequestParam("dbid") int dashboardId,
                                                   @RequestParam("role") int role) {
        List<UserDashboardEntry> entries = dashboardService.listUserOfDashboard(dashboardId, role);
        JSONArray array = new JSONArray();
        for (UserDashboardEntry entry : entries) {
            User user = userDao.findById(entry.getId().getUserId());
            JSONObject o = new JSONObject();
            o.put("id", user.getId());
            o.put("name", user.getUsername());
            o.put("fname", user.getFullname());
            array.add(o);
        }
        return array.toString();
    }


    @RequestMapping(value = "user_panel")
    public String getUserPanel(ModelMap model) {
//        model.addAttribute("admin", adminDao.isAdmin(AuthUtil.getUsername()));
        model.addAttribute("admin", true);
        return "/partial/user_manage";
    }


    @RequestMapping(value = "/header/{id}", method = {RequestMethod.GET})
    public String getDashboardHeader(@PathVariable("id") int id, ModelMap model) {
        Dashboard dashboard = dashboardService.getDashboardById(id);
        model.addAttribute("dashboard", dashboard);
        appendUserInformation(model, dashboard);
        return "/dashboard/admins";
    }


    @RequestMapping(value = "users_dashboards", method = RequestMethod.GET)
    public String usersDashboardsPage(@RequestParam("cec") String cecId,  ModelMap model) {
        List<Dashboard> dashboards = dashboardService.getDashboardsOfUser(cecId);
        model.put("dashboards", dashboards);
        return "/dashboard/users_dashboards";
    }


//    private List<Dashboard> getNonChildDashboards(String workspace) {
//        List<Dashboard> dashboards = getUserDashboardsOrSystemDashboards(workspace);
//        return removeChildDashboards(dashboards);
//    }

    private List<Dashboard> getUserDashboardsOrSystemDashboards(String workspaceName) {
        User user = userDao.findByUserName(AuthUtil.getUsername());
        if (user == null) {
            return dashboardService.getSystemDashboards(workspaceName);
        } else {
            List<Dashboard> usersDashboards = new LinkedList<Dashboard>();
            for (UserDashboardEntry entry : user.getDashboards()) {
                usersDashboards.add(entry.getDashboard());
            }
            if (usersDashboards.size() == 0) {
                return dashboardService.getSystemDashboards(workspaceName);
            }
            return usersDashboards;
        }
    }

    private List<Dashboard> removeChildDashboards(List<Dashboard> dashboards) {
        LinkedList<Dashboard> result = new LinkedList<Dashboard>();
        result.addAll(dashboards);
        for (Dashboard dashboard : dashboards) {
            if (dashboard.isGroup()) {
                Set<GroupDashboardEntry> children = dashboard.getGroupDashboardEntries();
                for (GroupDashboardEntry child : children) {
                    Iterator<Dashboard> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        Dashboard dashboard1 = iterator.next();
                        if (child.getId().getDashboardId() == dashboard1.getId()) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return result;
    }


    private List<Dashboard> removeGroupDashboards(List<Dashboard> dashboards) {
        LinkedList<Dashboard> result = new LinkedList<Dashboard>();
        result.addAll(dashboards);
        for (Dashboard dashboard : dashboards) {
            if (dashboard.isGroup()) {
                result.remove(dashboard);
            }
        }
        return result;
    }
}
