package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.service.DailyChangeService;
import com.webex.qd.spring.ApplicationContextProvider;
import com.webex.qd.vo.DailyChanges;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shijchen
 * Date: 3/18/14
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class DailyChangesWidget extends AbstractMultiTabWidget<DailyChangesWidget.Configuration, DailyChangesWidget.ConfigEntry> {

    @Override
    public boolean isLazyLoad() {
        return true;
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    @Override
    public Object loadTabData(ConfigEntry configEntry) {
        return retrieveReport(configEntry);
    }

    private Data retrieveReport(ConfigEntry entry) {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        DailyChangeService dailyChangeService = (DailyChangeService) ctx.getBean("dailyChangeService");
        Data data = new Data();
        data.setConfig(entry);
        data.setChangeLog(dailyChangeService.findDailyChangesByConditionSum(entry));
        return data;
    }

    public static final class Data {

        private ConfigEntry config;

        public ConfigEntry getConfig() {
            return config;
        }

        public List<DailyChanges> getChangeLog() {
            return changeLog;
        }

        public void setChangeLog(List<DailyChanges> changeLog) {

            this.changeLog = changeLog;
        }

        private List<DailyChanges> changeLog;

        public void setConfig(ConfigEntry config) {
            this.config = config;
        }

        public static String date2str(Date date) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            date = cal.getTime();
            return format.format(date);
        }

        public static String date2strYYYYMMDD(Date date) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        }
    }

    public static final class Configuration extends DefaultWidgetConfiguration implements IMultiTabWidgetConfiguration<ConfigEntry> {
        private List<ConfigEntry> configEntries = new LinkedList<ConfigEntry>();

        @Override
        public List<ConfigEntry> getConfigEntries() {
            return configEntries;
        }

        @Override
        public void setConfigEntries(List<ConfigEntry> entries) {
            this.configEntries = entries;
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        @Override
        public boolean doNullCheck() {
            return configEntries.isEmpty();
        }

    }

    public static final class ConfigEntry implements IMultiTabWidgetConfigEntry {
        private String displayName;
        private String fromTime;
        private String toTime;
        private String repo;
        private String branch;
        private String user;
        private String isChart;
        private String type;
        private String url;
        private Integer limitNum;


        public void setIsChart(String isChart) {
            this.isChart = isChart;
        }

        public String getFromTime() {
            return fromTime;
        }

        public String getToTime() {
            return toTime;
        }

        public String getIsChart() {
            return isChart;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ConfigEntry() {

        }

        public ConfigEntry(String displayName, String fromTime, String toTime, String repo, String branch, String user,Integer limitNum) {
            this.displayName = displayName;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.repo = repo;
            this.branch = branch;
            this.user = user;
            this.limitNum =limitNum;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }


        public void setRepo(String repo) {
            this.repo = repo;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setFromTime(String fromTime) {
            this.fromTime = fromTime;
        }

        public void setToTime(String toTime) {
            this.toTime = toTime;
        }

        public String getRepo() {
            return repo;
        }

        public String getBranch() {
            return branch;
        }

        public String getUser() {
            return user;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getLimitNum() {
            return limitNum;
        }

        public void setLimitNum(Integer limitNum) {
            this.limitNum = limitNum;
        }

        @Override
        public boolean isNull() {
            return StringUtils.isBlank(displayName);
        }
    }


}
