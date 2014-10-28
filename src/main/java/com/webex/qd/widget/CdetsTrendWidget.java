package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.widget.cdets.CdetsQuery;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: justin
 * Date: 7/18/13
 * Time: 10:04 AM
 */
public class CdetsTrendWidget extends IWidget<CdetsTrendWidget.Configuration> {
    @Override
    public Object loadData() {
        return null;
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }


    public static final class Configuration extends DefaultWidgetConfiguration {
        private List<CdetsQuery> queries = new LinkedList<CdetsQuery>();

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        @Override
        public boolean doNullCheck() {
            return queries.isEmpty();
        }

        public List<CdetsQuery> getQueries() {
            return queries;
        }

        public void setQueries(List<CdetsQuery> queries) {
            this.queries = queries;
        }
    }
}

