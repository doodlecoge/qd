package com.webex.qd.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: huaiwang
 * Date: 7/31/13
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class QdProperties {
    private static final Logger LOG = LoggerFactory.getLogger(QdProperties.class);
    private static Properties props = new Properties();

    static {
        InputStream is = QdProperties.class.getResourceAsStream("/qd.properties");

        try {
            props.load(is);
        } catch (IOException e) {
            LOG.error("load /qd.properties error.", e);
        }
    }

    private QdProperties() {
    }


    public static String getProperty(String key) {
        return String.valueOf(props.get(key));
    }

    public static String getCdetsServiceUrl() {
        return getProperty("qd-cdets-rs-url");
    }
}
