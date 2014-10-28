package com.webex.qd.dao;

import com.webex.qd.vo.Dashboard;
import com.webex.qd.vo.Widget;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 3:43 PM
 */
@Repository("widgetDao")
@Transactional
public class WidgetDaoImpl extends BaseDao implements WidgetDao {

    @Override
    public Widget getWidget(int id) {
        return (Widget) getCurrentSession().get(Widget.class, id);
    }

    @Override
    public void delete(Widget widget) {
        getCurrentSession().delete(widget);
    }

    public String getProjectMap() {
        String[] vals = new String[]{
                "product", "product_trends", "product_differ",
                "product_w_pkg", "product_differ_w_pkg", "build_history"
        };

        Session session = getCurrentSession();
        List<Widget> widgets = session.createCriteria(Widget.class)
                .add(Restrictions.in("type", vals))
                .list();

        Map<String, Set<String>> data = new HashMap<String, Set<String>>();

        for (Widget widget : widgets) {
            Set<String> projectCodes = getProjectCodes(widget);
            if(projectCodes.size() == 0) {
                continue;
            }
            String name = getDashboardName(widget.getDashboardId());
            if(name == null) {
                continue;
            }

            if(data.containsKey(name)) {
                data.get(name).addAll(projectCodes);
            } else {
                data.put(name, projectCodes);
            }
        }

        Set<String> dashboardNames = data.keySet();
        Iterator<String> iterator = dashboardNames.iterator();

        String ret = "{";
        while (iterator.hasNext()) {
            String name = iterator.next();
            Set<String> projectCodes = data.get(name);

            ret += "\"" + name.replace("\"", "\\\"") + "\":[";

            boolean fst = true;
            for (String projectCode : projectCodes) {
                if (!fst) ret += ",";
                fst = false;
                ret += "\"" + projectCode.replace("\"", "\\\"") + "\"";
            }

            ret += "]";

            if(iterator.hasNext()) ret += ",";
        }
        ret += "}";




        return ret;
    }

    @Override
    public List<Object[]> getWidgetUsage() {
        return getCurrentSession().createSQLQuery("select type,count(id) as `count` " +
                "from widgets group by type order by `count`;").list();
    }

    private String getDashboardName(int dbid) {
        Object obj = getCurrentSession().createCriteria(Dashboard.class)
                .add(Restrictions.eq("id", dbid))
                .uniqueResult();

        if(obj == null) return null;
        Dashboard db = (Dashboard) obj;

        return db.getName();
    }

    private Set<String> getProjectCodes(Widget widget) {
        Set<String> set = new HashSet<String>();

        String setting = widget.getSetting();
        JSONObject jsonObject = null;

        try {
            jsonObject = JSONObject.fromObject(setting);
        } catch (Exception e) {
        }

        if (jsonObject == null || !jsonObject.containsKey("project_codes")) return set;

        Object obj = jsonObject.get("project_codes");

        List<JSONArray> jarrs = new ArrayList<JSONArray>();

        if (obj instanceof JSONObject) {
            JSONObject jobj = (JSONObject) obj;
            Set keys = jobj.keySet();
            for (Object key : keys) {
                Object o = jobj.get(key);
                if (o instanceof JSONObject) jarrs.add((JSONArray) o);
            }
        } else if (obj instanceof JSONArray) {
            jarrs.add((JSONArray) obj);
        }

        for (JSONArray jarr : jarrs) {
            for (int i = 0; i < jarr.size(); i++) {
                set.add(jarr.getString(i));
            }
        }

        return set;
    }
}
