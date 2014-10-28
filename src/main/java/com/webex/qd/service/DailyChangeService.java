package com.webex.qd.service;

import com.webex.qd.vo.DailyChanges;
import com.webex.qd.widget.DailyChangesWidget;

import java.util.List;

/**
 * Created by chenshijie on 3/21/14.
 */
public interface DailyChangeService {
//    List<DailyChanges>  findDailyChangesByCondition(DailyChangesWidget.ConfigEntry d);
    List<DailyChanges>  findDailyChangesByConditionSum(DailyChangesWidget.ConfigEntry d);

}
