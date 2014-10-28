package com.webex.qd.util;

import java.util.Date;

/**
 * Author: justin
 * Date: 7/18/13
 * Time: 2:06 PM
 */
public class DateUtils {
    private DateUtils() {

    }

    public static Date earlierOf(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        } else if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }

        if (date1.before(date2)) {
            return date1;
        } else {
            return date2;
        }
    }

    public static Date latterOf(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        } else if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        }

        if (date1.after(date2)) {
            return date1;
        } else {
            return date2;
        }
    }
}
