package com.webex.qd.util;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-11-19
 * Time: 下午2:33
 */
public class JsonUtils {
    public static String toJson(Object o) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(o);
    }


    public static <T> T fromJson(String json, java.lang.Class<T> objectType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, objectType);
    }

}
