package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.api.ErrorResult;
import com.webex.qd.apiclient.HttpEngine;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


/**
 * author:Tony Wang
 * version:1.0
 * date:19/11/2013
 */

/**
 * Embedded Widget  will try to save the user defined url
 * You can edit the web on <a href='www.jsfiddle.net'>jsfiddle</a> , the website site will provide a sandbox for user edit the widget.
 * After the work is done, paste the full screen result link in the configuration view.
 */
public class EmbeddedWidget extends IWidget<EmbeddedWidget.Configuration> {

    @Override
    public Object loadData() {
        return null;
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    @JsonIgnoreProperties({"name"})
    public static class Configuration extends DefaultWidgetConfiguration {

        private String url = null;
        private Integer height = 450;
        @Override
        public ApiResult validate() {
            //try to touch the url,if it doesn't work, return failure
             if (url == null){
                 return new ErrorResult("Invalid Url");
             }
//            ApiResult result;
//            HttpEngine engine = new HttpEngine();
//            try {
//                engine.get(getUrl());
//                if (engine.getStatusCode() == 200) { // if returns 200, we can regard it as valid
//                    result = ApiResult.SUCCESS;
//                } else {
//                    result = new ErrorResult("Invalid Url");
//                }
//            } catch (IOException e) {
//                result = new ErrorResult("Invalid Url");
//            }

            return ApiResult.SUCCESS;
        }

        @Override
        public boolean doNullCheck() {
            return StringUtils.isBlank(url);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public Configuration fromJsonString(String json) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Configuration configuration = objectMapper.readValue(json, this.getClass());
                this.url = configuration.url;
                this.height = configuration.height;
            } catch (IOException ignore) {
            }
            return this;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
    }


}
