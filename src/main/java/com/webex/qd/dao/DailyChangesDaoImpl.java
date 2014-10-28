package com.webex.qd.dao;

import com.webex.qd.vo.DailyChanges;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chenshijie on 3/21/14.
 */
@Repository("DailyChangesDao")
@Transactional
public class DailyChangesDaoImpl  extends BaseDao implements DailyChangesDao {

    @Override
    public List<DailyChanges> findDailyChangesByCondition(DailyChanges find,String flag) {
        String repo = find.getRepo();
        StringBuffer sql = new StringBuffer();
        sql.append("select 1,time,repo,branch,user,sum(add) add,sum(modified) modified,sum(delete) delete from DailyChanges d where 1=1");
        if(!StringUtils.isBlank(repo)){
            sql.append("and d.repo='");
            sql.append(repo);
            sql.append("'");
        }
        sql.append(" group by ");
        if(StringUtils.equals("1",flag)){
            sql.append(" month");
        }
        if(StringUtils.equals("1",flag)){
            sql.append(" week");
        }
        Query query = getCurrentSession().createSQLQuery("FROM daily_changes");
        List resultSet = query.list();

        return resultSet;
    }
}
