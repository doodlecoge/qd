package com.webex.qd.tasche;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 2:51 PM
 */
public class TestStats {
    private int totalCase = 0;
    private int failedCase = 0;

    private float passrate = 0f;

    private String projectCode;
    private String buildNumber;

    private String dateCreated;

    private String url;


    public TestStats() {
    }

    public TestStats(String projectCode, String buildNumber, int totalCase, int failedCase, String dateCreated) {
        this.projectCode = projectCode;
        this.totalCase = totalCase;
        this.failedCase = failedCase;
        this.buildNumber = buildNumber;
        this.dateCreated = dateCreated;

        calculatePassRate();
    }

    public void merge(TestStats stat) {
        this.totalCase += stat.getTotalCase();
        this.failedCase += stat.getFailedCase();
        calculatePassRate();
    }

    private void calculatePassRate() {
        if (this.totalCase != 0) {
            this.passrate = (this.totalCase - this.failedCase) * 1.0f / this.totalCase;
        }

        this.passrate = Math.round(this.passrate * 1000) * 1.0f / 10;
    }

    public TestStats(String projectCode, String buildNumber, int totalCase, int failedCase, String dateCreated, String url) {
        this(projectCode, buildNumber, totalCase, failedCase, dateCreated);
        this.url = url;
    }

    public int getTotalCase() {
        return totalCase;
    }

    public void setTotalCase(int totalCase) {
        this.totalCase = totalCase;
    }

    public int getFailedCase() {
        return failedCase;
    }

    public void setFailedCase(int failedCase) {
        this.failedCase = failedCase;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public float getPassrate() {
        return this.passrate;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
