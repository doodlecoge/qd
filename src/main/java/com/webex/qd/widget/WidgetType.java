package com.webex.qd.widget;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 4:39 PM
 */
public enum WidgetType {
    CDETS                   ("cdets", "CDETS Bug Table", "CDETS Bug Table", "Show Bugs in CDETS."),
    QDDTS                   ("qddts", "QDDTS Bug Table", "QDDTS Bug Table", "Show Bugs in QDDTS."),
    CDETS_TREND             ("cdets_trend", "CDETS Bug Trend", "CDETS Bug Trend", "Show daily bug number submitted, resolved, verified.", true),
    CDETS_ACCUMULATION      ("cdets_accumulation", "QDDTS Cumulative Trend", "QDDTS Bug Cumulative Trend", "Show outstanding bug (S,N,A,O,W,H,I,P,M) cumulative trend, daily incoming and closed bug."),
    TIMS_REPORT             ("tims_report", "TIMS Report", "TIMS Report", "Show Report from TIMS."),
    TIMS_REPORT_TABLE             ("tims_report_table", "TIMS Report (Table View)", "TIMS Report (Table View)", "Show Report from TIMS in a table."),
    TIMS_AUTOMATION_CASE_STATS ("tims_ta_stats", "TIMS Automation Cases Stats", "TIMS Automation Cases Stats", "List Case Automation Statistics in TIMS"),

    LATEST_BUILD            ("last_build", "TA Latest Build Report", "Latest Build TA Report", "Show latest build TA pass rate, and diff with previous build."),
    BUILD_HISTORY           ("build_history", "TA Build History", "Build History", "Show summarized pass rate of recent days."),
    PRODUCT_TRENDS          ("product_trends", "TA Pass Rate Trend", "TA Pass Rate Trend", "TA pass rate of recent several builds, shown in line chart.", true),
    CASE_NUMBER             ("case_number", "TA Test Case Trend", "Case Number", "Show case number of each build."),

    RALLY                   ("rally", "Rally Chart", "Rally Chart", "Show Rally Burn Down/Up /Velocity/CycleLeadTime/... Charts."),
    RALLY_DEFECT            ("rally_defect", "Rally Defect Table", "Rally Defect", "Show Rally defects and filter results by criteria."),
    RALLY_DEFECT_CUMULATIVE ("rally_defect_acc", "Rally Defect Trend", "Rally Defect Widget Cumulative Trend", "Show Rally defect cumulative trend."),
    RALLY_TEST_CASE         ("rally_test_case", "Rally TestCase", "Rally Test Case", "Show Rally test cases."),

    SONAR                   ("sonar", "Sonar Widget", "Sonar", "Show sonar data"),

    JENKINS_VIEW            ("jenkins_view", "Jenkins View", "Jenkins View", "Show Jenkins View"),

    PRODUCT                 ("product", "Build Widget", "Build Widget", "Show latest build TA pass rate and code coverage rate.", true),
    PRODUCT_DIFFER          ("product_differ", "Diff Widget", "Diff Widget", "Show differences of two latest builds.", true),
    PRODUCT_W_PKG           ("product_w_pkg", "Normal Widget with Package", "Normal Widget", "Show latest build pass rate.", true),
    PRODUCT_DIFFER_W_PKG    ("product_differ_w_pkg", "Diff Widget with Package", "Diff Widget", "Show differences of two latest builds.", true),
    PROJECT_TOTAL           ("project_total","Project TA case summary","Project TA case summary","show project TA case summary."),

    NOT_FOUND               ("not_found", "Not Found", "Not Found", "Not Found", true),

    EMBEDDED                ("embedded","Embedded Widget","Embedded Widget","Embed your own content to create a widget" ),

    DAILY_CHANGES                ("daily_changes","SCM Changes","SCM Changes","show the coding changes" );



    private String key;
    private String name;
    private String displayName;
    private String description;
    private boolean deprecate;

    private WidgetType(String key, String name, String displayName, String description) {
        this(key, name, displayName, description, false);
    }

    private WidgetType(String key, String name, String displayName, String description, boolean deprecate) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.displayName = displayName;
        this.deprecate = deprecate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeprecate() {
        return deprecate;
    }

    public void setDeprecate(boolean deprecate) {
        this.deprecate = deprecate;
    }
}
