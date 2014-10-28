package com.webex.qd.widget.cdets;

import com.webex.qd.apiclient.HttpEngine;
import com.webex.qd.util.JsonUtils;
import com.webex.qd.util.QdProperties;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-19
 * Time: 下午2:10
 */
public class CdetsServiceClient {
    private String serviceUrl;

    public CdetsServiceClient() {
        this(QdProperties.getCdetsServiceUrl());
    }

    public CdetsServiceClient(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public Map<String, Integer> queryDefectNumberBySeverityAndStatus(String query) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("query", query));

        HttpEngine eng = new HttpEngine();
        eng.post(serviceUrl + "/act/service/ssc", params);
        String html = eng.getHtml();
        return JsonUtils.fromJson(html, Map.class);
    }


    public String queryAccumulativeTrend(String query) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("query", query));
        HttpEngine eng = new HttpEngine();
        return eng.post(serviceUrl + "/act/service/qddts", params).getHtml();
    }

    public String refreshQddts(String query) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("query", query));
        HttpEngine eng = new HttpEngine();
        return eng.post(serviceUrl + "/act/service/qddts_ref", params).getHtml();
    }

    public String refreshCdets(String query) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("query", query));
        HttpEngine eng = new HttpEngine();
        try {
            return eng.post(serviceUrl + "/act/cdets/refresh", params).getHtml();
        } catch (IOException e) {
            return "{error:true}";
        }
    }

    public String cdetsUpdatedTime(String query) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("query", query));
        HttpEngine eng = new HttpEngine();
        try {
            return eng.post(serviceUrl + "/act/cdets/updated_time", params).getHtml();
        } catch (IOException e) {
            return "err";
        }
    }

    public static void main(String args[]) throws IOException {
        CdetsServiceClient client = new CdetsServiceClient("http://10.224.138.205/qd-cdets-rs");
        Map<String, Integer> result = client.queryDefectNumberBySeverityAndStatus("[Product] = 'identity' AND [Project] = 'CSC.csg' AND [To-be-fixed] = '3.0'");


//        String s = out.toString();
    }
}
