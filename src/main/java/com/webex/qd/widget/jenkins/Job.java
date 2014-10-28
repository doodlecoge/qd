package com.webex.qd.widget.jenkins;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: justin
 * Date: 9/4/13
 * Time: 1:13 PM
 */
public class Job {
    private String name;
    private String displayName;
    private String color;
    private List<HealthReport> healthReport = new LinkedList<HealthReport>();
    private Build lastSuccessBuild = null;
    private Build lastFailureBuild = null;

    public Job(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getColorIcon() {
        if ("notbuilt".equals(color)) {
            return "grey";
        } else if ("disabled".equals(color)) {
            return "grey";
        } else if ("blue".equals(color)) {
            return "green";
        }
        return color;
    }

    public String getHealthIcon() {
        int score = getLowestHealthScore();
        if (score > 80) {
            return "health-80plus.gif";
        } else if (score <= 80 && score > 60) {
            return "health-60to79.gif";
        } else if (score <= 60 && score > 40) {
            return "health-40to59.gif";
        } else if (score <= 40 && score > 20) {
            return "health-20to39.gif";
        } else if (score <=20 && score >= 0) {
            return "health-00to19.gif";
        } else {
            return "empty.gif";
        }
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void addHealthReport(HealthReport healthReport) {
        this.healthReport.add(healthReport);
    }

    public List<HealthReport> getHealthReport() {
        return healthReport;
    }

    public void setHealthReport(List<HealthReport> healthReport) {
        this.healthReport = healthReport;
    }

    public int getLowestHealthScore() {
        int lowest = -1;
        for (HealthReport healthReport : this.healthReport) {
            if (lowest == - 1) {
                lowest = healthReport.score;
            } else if (lowest > healthReport.score) {
                lowest = healthReport.score;
            }

        }
        return lowest;
    }


    public Build getLastSuccessBuild() {
        return lastSuccessBuild;
    }

    public void setLastSuccessBuild(Build lastSuccessBuild) {
        this.lastSuccessBuild = lastSuccessBuild;
    }

    public Build getLastFailureBuild() {
        return lastFailureBuild;
    }

    public void setLastFailureBuild(Build lastFailureBuild) {
        this.lastFailureBuild = lastFailureBuild;
    }

    public static final class HealthReport {
        private String description;
        private String iconUrl;
        private int score;

        public HealthReport(String description, String iconUrl, int score) {
            this.description = description;
            this.iconUrl = iconUrl;
            this.score = score;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }


}
