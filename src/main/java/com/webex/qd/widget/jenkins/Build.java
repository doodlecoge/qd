package com.webex.qd.widget.jenkins;

/**
 * Author: justin
 * Date: 9/4/13
 * Time: 1:15 PM
 */
public class Build {
    private int number;
    private long timestamp;
    private long duration;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationString() {
        return getTimeSpanString(duration);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTimeSpan() {
        long current = System.currentTimeMillis();
        return getTimeSpanString(current - timestamp);
    }


    @Override
    public String toString() {
        return getTimeSpan();
    }

    private static final long ONE_SECOND_MS = 1000;
    private static final long ONE_MINUTE_MS = 60 * ONE_SECOND_MS;
    private static final long ONE_HOUR_MS = 60 * ONE_MINUTE_MS;
    private static final long ONE_DAY_MS = 24 * ONE_HOUR_MS;
    private static final long ONE_MONTH_MS = 30 * ONE_DAY_MS;
    private static final long ONE_YEAR_MS = 365 * ONE_DAY_MS;

    public static String getTimeSpanString(long duration) {
        // Break the duration up in to units.
        long years = duration / ONE_YEAR_MS;
        duration %= ONE_YEAR_MS;
        long months = duration / ONE_MONTH_MS;
        duration %= ONE_MONTH_MS;
        long days = duration / ONE_DAY_MS;
        duration %= ONE_DAY_MS;
        long hours = duration / ONE_HOUR_MS;
        duration %= ONE_HOUR_MS;
        long minutes = duration / ONE_MINUTE_MS;
        duration %= ONE_MINUTE_MS;
        long seconds = duration / ONE_SECOND_MS;
        duration %= ONE_SECOND_MS;
        long millisecs = duration;

        if (years > 0)
            return makeTimeSpanString(years, years + " yr", months, months + " mo");
        else if (months > 0)
            return makeTimeSpanString(months, months + " mo", days, days + " days");
        else if (days > 0)
            return makeTimeSpanString(days, days + " days", hours, hours + " hr");
        else if (hours > 0)
            return makeTimeSpanString(hours, hours + " hr", minutes, minutes + " min");
        else if (minutes > 0)
            return makeTimeSpanString(minutes, minutes + " min", seconds, seconds + " sec");
        else if (seconds >= 10)
            return seconds + " sec";
        else if (seconds >= 1)
            return (seconds+(((float)millisecs/100)/10)) + " sec"; // render "1.2 sec"
        else if(millisecs >= 100)
            return (((float)millisecs/10)/100) + " sec"; // render "0.12 sec".
        else
            return millisecs + " ms";
    }

    private static String makeTimeSpanString(long bigUnit,
                                             String bigLabel,
                                             long smallUnit,
                                             String smallLabel) {
        String text = bigLabel;
        if (bigUnit < 10)
            text += ' ' + smallLabel;
        return text;
    }
}
