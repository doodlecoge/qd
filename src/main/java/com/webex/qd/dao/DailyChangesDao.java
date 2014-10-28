package com.webex.qd.dao;

import com.webex.qd.vo.DailyChanges;

import java.util.List;

/**
 * Created by chenshijie on 3/21/14.
 */

public interface DailyChangesDao{

    List<DailyChanges> findDailyChangesByCondition(DailyChanges find,String flag);

}
