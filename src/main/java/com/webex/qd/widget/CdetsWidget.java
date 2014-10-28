package com.webex.qd.widget;

import com.webex.qd.api.ApiResult;
import com.webex.qd.widget.cdets.CdetsCriteria;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 3:59 PM
 */
public class CdetsWidget extends AbstractMultiTabWidget<CdetsWidget.Configuration, CdetsCriteria> {

    @Override
    public Object loadData() {
        return null;
    }

    @Override
    public boolean isLazyLoad() {
        return true;
    }

    @Override
    public Object loadTabData(CdetsCriteria configEntry) {
        return null;
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    @JsonIgnoreProperties({"name", "configEntries"})
    public static final class Configuration extends DefaultMultiTabWidgetConfiguration<CdetsCriteria> {

        private List<CdetsCriteria> criterias = new LinkedList<CdetsCriteria>();

        public List<CdetsCriteria> getCriterias() {
            return criterias;
        }

        public void setCriterias(List<CdetsCriteria> criterias) {
            this.criterias = criterias;
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        @Override
        public List<CdetsCriteria> getConfigEntries() {
            return getCriterias();
        }

        @Override
        public void setConfigEntries(List<CdetsCriteria> entries) {
            setCriterias(entries);
        }
    }
}
