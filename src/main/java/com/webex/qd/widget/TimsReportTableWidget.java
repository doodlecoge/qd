package com.webex.qd.widget;

import com.cisex.tims.namespace.Tims;
import com.webex.qd.api.ApiResult;
import com.webex.qd.apiclient.HttpEngine;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 14-3-24
 * Time: 上午9:56
 */
public class TimsReportTableWidget extends AbstractMultiTabWidget<TimsReportTableWidget.Configuration, TimsReportTableWidget.TimsReportConfigEntry> {

    @Override
    public Object loadTabData(TimsReportTableWidget.TimsReportConfigEntry configEntry) {
        return retrieveReport(configEntry.reportId);
    }

    @Override
    public Configuration newConfiguration() {
        return new Configuration();
    }

    @Override
    public boolean isLazyLoad() {
        return true;
    }

    private Data retrieveReport(String reportId) {
        HttpEngine engine = new HttpEngine();
        try {
//            String xml = engine.get("http://tims.cisex.com/xml/Tyb753d/results-summary-report.svc").getHtml();
            String xml = engine.get("http://tims.cisex.com/xml/" + reportId + "/results-summary-report.svc").getHtml();
            ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
            JAXBContext jaxbContext = JAXBContext.newInstance(Tims.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Tims tims = (Tims) jaxbUnmarshaller.unmarshal(input);

            Data data = new Data();
            data.tableSpec = tims.getResultsSummaryReport().get(0).getSpecifications().get(0);
            data.reportId = reportId;
            data.resultSummaries = tims.getResultsSummaryReport().get(0).getResultSummary();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static final class Data {
        private String reportId;
        private Tims.ResultsSummaryReport.Specifications tableSpec;
        private List<Tims.ResultsSummaryReport.ResultSummary> resultSummaries;


        public Tims.ResultsSummaryReport.ResultSummary getResultSummary(String id) {
            for (Tims.ResultsSummaryReport.ResultSummary resultSummary : resultSummaries) {
                if (resultSummary.getID().get(0).getValue().equals(id)) {
                    return resultSummary;
                }
            }
            return null;
        }

        public Tims.ResultsSummaryReport.Specifications getTableSpec() {
            return tableSpec;
        }

        public List<Tims.ResultsSummaryReport.ResultSummary> getResultSummaries() {
            return resultSummaries;
        }

        public String getReportId() {
            return reportId;
        }
    }

    public static final class TimsReportConfigEntry implements IMultiTabWidgetConfigEntry {
        private String displayName;
        private String reportId;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getReportId() {
            return reportId;
        }

        public void setReportId(String reportId) {
            this.reportId = reportId;
        }

        @Override
        public boolean isNull() {
            return StringUtils.isBlank(displayName) &&
                    StringUtils.isBlank(reportId);
        }
    }
    @JsonIgnoreProperties({"name", "configEntries"})
    public static final class Configuration extends DefaultMultiTabWidgetConfiguration<TimsReportConfigEntry> {
        private List<TimsReportConfigEntry> timsReports = new LinkedList<TimsReportConfigEntry>();

        public List<TimsReportConfigEntry> getTimsReports() {
            return timsReports;
        }

        public void setTimsReports(List<TimsReportConfigEntry> timsReports) {
            this.timsReports = timsReports;
        }

        @Override
        public ApiResult validate() {
            return ApiResult.SUCCESS;
        }

        @Override
        public List<TimsReportConfigEntry> getConfigEntries() {
            return getTimsReports();
        }

        @Override
        public void setConfigEntries(List<TimsReportConfigEntry> entries) {
            setTimsReports(entries);
        }
    }
}