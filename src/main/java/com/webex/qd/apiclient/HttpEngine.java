package com.webex.qd.apiclient;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;


import javax.net.ssl.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 2:53 PM
 */
public class HttpEngine {
    private HttpClient client;
    private HttpResponse resp;

    private static final int DEFAULT_TIMEOUT = 120000;

    // ***************************************************************
    // https
    private X509TrustManager tm = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    };

    private X509HostnameVerifier verifier = new X509HostnameVerifier() {
        public boolean verify(String arg0, SSLSession arg1) {
            // TODO Auto-generated method stub
            return true;
        }

        public void verify(String arg0, String[] arg1, String[] arg2)
                throws SSLException {
            // TODO Auto-generated method stub

        }

        public void verify(String arg0, X509Certificate arg1)
                throws SSLException {
            // TODO Auto-generated method stub

        }

        public void verify(String arg0, SSLSocket arg1) throws IOException {
            // TODO Auto-generated method stub

        }
    };

    // end
    // ***************************************************************

    {
        try {
            this.client = new DefaultHttpClient();

            this.client.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_TIMEOUT);

            this.client.getParams().setParameter(
                    CoreConnectionPNames.SO_TIMEOUT, DEFAULT_TIMEOUT);

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx, verifier);
            ClientConnectionManager ccm = this.client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", 443, ssf));

        } catch (NoSuchAlgorithmException e) {
        } catch (KeyManagementException e) {
        }
    }

    public HttpEngine get(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        this.exec(get);

        return this;
    }

    public HttpEngine post(String url, List<NameValuePair> postData) throws IOException {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData,
                "UTF-8");

        HttpPost post = new HttpPost(url);
        post.setEntity(entity);

        this.exec(post);
        return this;
    }

    public HttpEngine post(String url, Map<String, String> postData) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        for (Map.Entry<String, String> entry : postData.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return this.post(url, params);
    }

    private void exec(HttpUriRequest req) throws IOException {
        this.resp = this.client.execute(req);
    }

    public void consume() {
        if (this.resp == null) {
            return;
        }

        try {
            this.resp.getEntity().getContent().close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Header[] getAllHeaders() {
        if (this.resp != null) {
            return this.resp.getAllHeaders();
        } else {
            return null;
        }
    }

    public Header[] getHeaders(String key) {
        if (this.resp != null) {
            return this.resp.getHeaders(key);
        } else {
            return null;
        }
    }

    public int getStatusCode() {
        if (this.resp != null) {
            return this.resp.getStatusLine().getStatusCode();
        } else {
            return -1;
        }
    }

    public String getHtml() throws IllegalStateException, IOException {
        InputStream is = this.resp.getEntity().getContent();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b = -1;

        while ((b = is.read()) != -1) {
            baos.write(b);
        }

        is.close();
        return baos.toString();
    }

    public Document getXmlDocument() throws IOException, DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new InputStreamReader(this.resp.getEntity().getContent()));
    }

    public CookieStore getCookieStore() {
        return ((DefaultHttpClient) this.client).getCookieStore();
    }

    public void setCookie(CookieStore cs) {
        ((DefaultHttpClient) this.client).setCookieStore(cs);
    }
}
