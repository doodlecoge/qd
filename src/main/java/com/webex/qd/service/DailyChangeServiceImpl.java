package com.webex.qd.service;

import com.webex.qd.apiclient.HttpEngine;
import com.webex.qd.dao.BaseDao;
import com.webex.qd.dao.DailyChangesDao;
import com.webex.qd.vo.DailyChanges;
import com.webex.qd.widget.DailyChangesWidget;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenshijie on 3/21/14.
 */
@Repository("dailyChangeService")
@Transactional
public class DailyChangeServiceImpl extends BaseDao implements DailyChangeService {


    @Autowired
    private DailyChangesDao dailyChangesDao;


//    @Override
//    public List<DailyChanges> findDailyChangesByCondition(DailyChangesWidget.ConfigEntry find) {
//
//        StringBuffer sql = new StringBuffer();
//        sql.append("from DailyChanges where 1=1");
//        if(find!=null && !StringUtils.isBlank(find.getRepo())){
//            sql.append(" and repo in (");
//            sql.append(addSingleQuotes(find.getRepo()));
//            sql.append(")");
//        }
//        if(find!=null && !StringUtils.isBlank(find.getFromTime())){
//            sql.append(" and time>='");
//            sql.append(find.getFromTime());
//            sql.append("'");
//        }
//        if(find!=null && !StringUtils.isBlank(find.getToTime())){
//            sql.append(" and time<='");
//            sql.append(find.getToTime());
//            sql.append("'");
//        }
//        if(find!=null && !StringUtils.isBlank(find.getUser())){
//            sql.append(" and user in (");
//            sql.append(addSingleQuotes(getDirectorEmployees(find.getUser())));
//            sql.append(")");
//        }
//        if(find!=null && !StringUtils.isBlank(find.getBranch())){
//            sql.append(" and branch in (");
//            sql.append(addSingleQuotes(find.getBranch()));
//            sql.append(")");
//        }
//
//        sql.append(" order by time,repo,branch,user");
//        Session session =getCurrentSession();
//
//    if (find != null && find.getLimitNum()>0) {
//        sql.append(" and added < ");
//        sql.append(find.getLimitNum());
//    }
//        return session.createQuery(sql.toString()).list();
//    }

    @Override
    public List<DailyChanges> findDailyChangesByConditionSum(DailyChangesWidget.ConfigEntry find) {
        StringBuffer sql = new StringBuffer();
        Integer type =Integer.valueOf(find.getType());

        //selects
        switch (type) {
            case 1:
                sql.append("select time,max(id) id,max(branch) branch,max(user) user,max(repo) repo,");
                break;
            case 2:
                sql.append("select repo,max(time) time,max(branch) branch ,max(user) user,max(id) id,");
                break;
            case 3:
                sql.append("select branch,max(time) time,max(repo) repo,max(user) user,max(id) id,");
                break;
            case 4:
                sql.append("select user,max(time) time,max(repo) repo,max(branch) branch,max(id) id,");
                break;
            case 5:
                sql.append("select max(id) id,str_to_date(substring(time,1,7),'%Y-%m') time,max(repo) repo,max(branch) branch,max(user) user,  ");
                break;
            case 6://group by user ,monthly
                sql.append("select max(id) id,str_to_date(substring(time,1,7),'%Y-%m') time,max(repo) repo,max(branch) branch,user, ");
                break;
            case 7://group by week
                sql.append("select max(id) id,adddate(time, INTERVAL 1-DAYOFWEEK(time) DAY) time,max(repo) repo,max(branch) branch,max(user) user, ");
                break;
            default:
                return null;
        }

        //added,modified,deleted
        sql.append("sum(added) added ,sum(modified) modified,sum(deleted) deleted from daily_changes  where 1=1");

        //wheres
        if (find != null && !StringUtils.isBlank(find.getRepo())) {
            sql.append(" and repo in (");
            sql.append(addSingleQuotes(find.getRepo()));
            sql.append(")");
        }
        if (find != null && !StringUtils.isBlank(find.getFromTime())) {
            sql.append(" and `time`>='");
            sql.append(find.getFromTime());
            sql.append("'");
        }
        if (find != null && !StringUtils.isBlank(find.getToTime())) {
            sql.append(" and `time`<='");
            sql.append(find.getToTime());
            sql.append("'");
        }
        if (find != null && !StringUtils.isBlank(find.getUser())) {
            sql.append(" and user in (");
            sql.append(addSingleQuotes(getDirectorEmployees(find.getUser(),find.getUrl())));
            sql.append(")");
        }
        if (find != null && !StringUtils.isBlank(find.getBranch())) {
            sql.append(" and branch in (");
            sql.append(addSingleQuotes(find.getBranch()));
            sql.append(")");
        }
        if (find != null && find.getLimitNum()>0) {
            sql.append(" and added < ");
            sql.append(find.getLimitNum());
        }

        //group by s
        switch (type) {
            case 1:
                sql.append(" group by `time`");
                break;
            case 2:
                sql.append(" group by `repo`");
                break;
            case 3:
                sql.append(" group by `branch`");
                break;
            case 4:
                sql.append(" group by `user`");
                break;
            case 5:
                sql.append(" group by substring(time,1,7)");
                break;
            case 6:
                sql.append(" group by user,substring(time,1,7)");
                break;
            case 7:
                sql.append(" group by adddate(time, INTERVAL 1-DAYOFWEEK(time) DAY) ");
                break;
            default:
                return null;
        }

        //order
        switch (type) {

            case 6:
                sql.append(" order by user,time,repo,branch");
                break;
            case 7:
                sql.append(" order by time");
                break;
            default:
                sql.append(" order by time,repo,branch,user");
        }

        Session session = getCurrentSession();

        return session.createSQLQuery(sql.toString()).addEntity(DailyChanges.class).list();
    }

    private String addSingleQuotes(String res) {
        StringBuffer result = new StringBuffer();
        if (res.contains(",")) {
            String[] list = res.split(",");
            if(list.length <=0){
                return "''";
            }
            for (String s : list) {
                result.append(",'" + s + "'");
            }
        } else {
            return "'" + res + "'";
        }
        return result.toString().substring(1);
    }

    private String getDirectorEmployees(String str,String url) {
        if (str.equals("")) {
            return "";
        }
        StringBuffer result = new StringBuffer();
        String strEmployee = "null";
        try {
            if (str.contains(",")) {
                String[] managers = str.split(",");

                for (String manager : managers) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("cecId", manager);
                    HttpEngine engine = new HttpEngine();
                    strEmployee = engine.post(url+"/tasche/widget/managerList.action", params).getHtml();
//                    strEmployee = engine.post("http://localhost:8080/widget/managerList.action", params).getHtml();
                    if (!"null".equals(strEmployee)) {
                        result.append(",");
                        result.append(strEmployee);
                    }
                }
            } else {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cecId", str);
                HttpEngine engine = new HttpEngine();
                strEmployee = engine.post(url+"/tasche/widget/managerList.action", params).getHtml();
//                strEmployee = engine.post("http://localhost:8080/widget/managerList.action", params).getHtml();
                if (!"null".equals(strEmployee)) {
                    result.append(",");
                    result.append(strEmployee);
                } else {
                    return str;
                }
            }
        } catch (Exception e) {

        }
        return (result != null && result.length() > 1) ? result.toString().substring(1) : str;
    }
}
