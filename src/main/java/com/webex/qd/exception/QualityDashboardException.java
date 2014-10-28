package com.webex.qd.exception;

/**
 * Author: justin
 * Date: 7/16/13
 * Time: 9:32 AM
 */
public class QualityDashboardException extends Exception {
    public QualityDashboardException() {
        super();
    }

    public QualityDashboardException(String message) {
        super(message);
    }

    public QualityDashboardException(String message, Throwable cause) {
        super(message, cause);
    }

    public QualityDashboardException(Throwable cause) {
        super(cause);
    }
}
