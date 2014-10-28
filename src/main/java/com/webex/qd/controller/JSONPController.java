package com.webex.qd.controller;

import com.webex.qd.dao.CECInfoDao;
import com.webex.qd.dao.WidgetDao;
import com.webex.qd.service.DashboardService;
import com.webex.qd.vo.CECInfo;
import com.webex.qd.vo.Dashboard;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * author:Tony Wang
 * version:1.0
 * date:12/24/13
 */
@Controller
@RequestMapping("/jsonp")
@Transactional
public class JSONPController {
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private WidgetDao widgetDao;
    @Autowired
    private CECInfoDao cecDao;

    /**
     * This api is used for get the specified user's dashboard.
     * It supports cross domain access.
     * @param callback The jsonp callback function name
     * @param username      The user name
     * @return jsonp
     * @throws IOException
     */

    @RequestMapping("/user_dashboards")
    @ResponseBody
    public String getDashboardsByUserName(@RequestParam("callback") String callback,
                                          @RequestParam("username") String username) throws IOException {
        List<Dashboard> dashboards = dashboardService.getDashboardsOfUser(username);
        if (CollectionUtils.isEmpty(dashboards)) {
            return callback + "([]);";
        }
        List<DashboardMetaData> datas = new ArrayList<DashboardMetaData>(dashboards.size());
        for (Dashboard dashboard : dashboards) {
            datas.add(new DashboardMetaData(dashboard.getId(), dashboard.getName()));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return callback + "(" + objectMapper.writeValueAsString(datas) + ")";
    }
    @RequestMapping("/user_own_dashboards")
    @ResponseBody
    public String getOwnDashboardsByUserName(@RequestParam("callback") String callback,
                                          @RequestParam("username") String username) throws IOException {
        List<Dashboard> dashboards = dashboardService.getUserOwnedDashboards(username);
        if (CollectionUtils.isEmpty(dashboards)) {
            return callback + "([]);";
        }
        List<DashboardMetaData> datas = new ArrayList<DashboardMetaData>(dashboards.size());
        for (Dashboard dashboard : dashboards) {
            datas.add(new DashboardMetaData(dashboard.getId(), dashboard.getName()));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return callback + "(" + objectMapper.writeValueAsString(datas) + ")";
    }

    public class DashboardMetaData {
        private int id;
        private String name;

        public DashboardMetaData(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
    @RequestMapping("/widget_type")
    @ResponseBody
    public String getWidgetType(@RequestParam("callback") String callback) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Object[]> dataList = widgetDao.getWidgetUsage();
        return callback + "(" + mapper.writeValueAsString(dataList) + ")";
    }
    public String toJsonStrWithIncludeProperties(ObjectMapper objectMapper, Object value, String[] properties2Include) throws JsonGenerationException,
            IOException {
        SimpleFilterProvider provider = new SimpleFilterProvider();
        Set<String> excuteSet = new HashSet<String>();
        for (String property : properties2Include) {
            excuteSet.add(property);
        }
        SimpleBeanPropertyFilter filter = new SimpleBeanPropertyFilter.FilterExceptFilter(excuteSet);
        provider.addFilter("info", filter);
        return objectMapper.writer(provider).writeValueAsString(value);

    }
    @RequestMapping("/user_info")
    @ResponseBody
    public String getCecInfo(
    @RequestParam("request") String request)throws IOException{
        JSONArray jsonArray = JSONArray.fromObject(request);
        List<String> cecList = JSONArray.toList(jsonArray,String.class);
        String list ="[]";
        try {
            List<CECInfo> infoList =cecDao.getCECInfoByCecList(cecList);
            ObjectMapper mapper = new ObjectMapper();
            list =toJsonStrWithIncludeProperties(mapper,infoList,new String[]{"city","prettyName","detailUrl","manager"});

        } catch (Exception e) {

        }

        return list;
    }

}
