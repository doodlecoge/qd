package com.webex.qd.widget.tims;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-29
 * Time: 上午8:47
 */
public class SearchResult {
    private String searchId;
    private int count;

    public SearchResult(String searchId, int count) {
        this.searchId = searchId;
        this.count = count;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
