package com.webex.qd.api;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 4:41 PM
 */
public class ErrorResult extends ApiResult {

    public ErrorResult() {
        this("");
    }

    public ErrorResult(String message) {
        super(ApiResult.ERROR_CODE, message);
    }
}
