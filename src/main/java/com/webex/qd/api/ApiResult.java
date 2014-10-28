package com.webex.qd.api;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: justin
 * Date: 7/9/13
 * Time: 4:41 PM
 */
public class ApiResult {
    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = -1;

    public static final ApiResult SUCCESS = new ApiResult(SUCCESS_CODE);
    public static final ApiResult FAILURE = new ApiResult(ERROR_CODE);

    private int statusCode;

    private String statusMessage = "";

    private Map<String, Object> ext = new HashMap<String, Object>();

    public ApiResult(int statusCode) {
        this(statusCode, "");
    }

    public ApiResult(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public boolean isSuccess() {
        return statusCode == SUCCESS_CODE;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Map getExt() {
        return ext;
    }

    public void putExt(String key, Object value) {
        this.ext.put(key, value);
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("statusCode", getStatusCode());
        json.put("statusMessage", getStatusMessage());
        json.put("ext", getExt());
        return json.toString();
    }
}
