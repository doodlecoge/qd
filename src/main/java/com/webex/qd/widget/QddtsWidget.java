package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.widget.qddts.QddtsCriteria;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * author:Tony Wang
 * version:1.0
 * date:1/9/14
 */

public class QddtsWidget extends AbstractMultiTabWidget<QddtsWidget.Configuration, QddtsCriteria> {
    @Override
    public boolean isLazyLoad() {
        return true;
    }
    @Override
    public Object loadData() {
        return null;
    }
    @Override
    public Object loadTabData(QddtsCriteria configEntry) {
        return null;
    }

    @Override
    public QddtsWidget.Configuration newConfiguration() {
        return new Configuration();
    }
    @JsonIgnoreProperties({"name", "configEntries"})
    public static class Configuration extends DefaultMultiTabWidgetConfiguration<QddtsCriteria> {
        private List<QddtsCriteria> criterias = new LinkedList<QddtsCriteria>();

        public List<QddtsCriteria> getCriterias() {
            return criterias;
        }

        public void setCriterias(List<QddtsCriteria> criterias) {
            this.criterias = criterias;
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        @Override
        public List<QddtsCriteria> getConfigEntries() {
            return getCriterias();
        }

        @Override
        public void setConfigEntries(List<QddtsCriteria> entries) {
            setCriterias(entries);
        }
    }
}
