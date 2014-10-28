package com.webex.qd.widget.cdets;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-10-31
 * Time: 下午2:11
 */
public class CdetsQuery {
    private String name;
    private String query;

    public CdetsQuery() {
    }

    public CdetsQuery(String name, String query) {
        this.name = name;
        this.query = query;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
