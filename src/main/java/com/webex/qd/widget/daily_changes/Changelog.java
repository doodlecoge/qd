package com.webex.qd.widget.daily_changes;

import com.webex.qd.widget.IMultiTabWidgetConfigEntry;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by chenshijie on 3/17/14.
 */
@JsonIgnoreProperties({"empty", "null"})
public class Changelog implements IMultiTabWidgetConfigEntry {

    private String time;

    private String repo;

    private String branch;

    private String user;

    private String addNum;

    private String modifiedNum;

    private String delNum;

    public void setTime(String time) {
        this.time = time;
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

    public void setAddNum(String addNum) {
        this.addNum = addNum;
    }

    public void setModifiedNum(String modifiedNum) {
        this.modifiedNum = modifiedNum;
    }

    public void setDelNum(String delNum) {
        this.delNum = delNum;
    }


    @Override
    public boolean isNull() {
        return isEmpty();
    }

    public boolean isEmpty() {

        return true;
    }
}
