package com.webex.qd.exception;

/**
 * Created with IntelliJ IDEA.
 * User: huaiwang
 * Date: 8/9/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class QdRtException extends RuntimeException {
    public QdRtException() {
        super();
    }

    public QdRtException(String message) {
        super(message);
    }

    public QdRtException(String message, Throwable cause) {
        super(message, cause);
    }

    public QdRtException(Throwable cause) {
        super(cause);
    }
}
