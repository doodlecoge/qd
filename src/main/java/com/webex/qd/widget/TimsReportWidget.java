package com.webex.qd.widget;

import com.cisex.tims.namespace.Tims;
import com.webex.qd.api.ApiResult;
import com.webex.qd.apiclient.HttpEngine;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: justin
 * Date: 7/25/13
 * Time: 5:02 PM
 */
public class TimsReportWidget extends AbstractMultiTabWidget<TimsReportWidget.Configuration, TimsReportWidget.TimsReportConfigEntry> {

    @Override
    public Object loadTabData(TimsReportWidget.TimsReportConfigEntry configEntry) {
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

    public static Data retrieveReport(String reportId) {
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
            data.resultsSummaryReport = tims.getResultsSummaryReport().get(0);
            data.resultSummaries = tims.getResultsSummaryReport().get(0).getResultSummary();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static final class Data {
        private String reportId;
        private Tims.ResultsSummaryReport.Specifications tableSpec;
        private Tims.ResultsSummaryReport resultsSummaryReport;
        private List<Tims.ResultsSummaryReport.ResultSummary> resultSummaries;


        public Tims.ResultsSummaryReport.ResultSummary getResultSummary(String id) {
            for (Tims.ResultsSummaryReport.ResultSummary resultSummary : resultSummaries) {
                if (resultSummary.getID().get(0).getValue().equals(id)) {
                    return resultSummary;
                }
            }
            return null;
        }

        public long aggregateByCategory(String category) {

            if (tableSpec.containsColumn(category)) {
                long total = 0;
                for (Tims.ResultsSummaryReport.ResultSummary resultSummary : resultSummaries) {
                    try {
                        if(tableSpec.getStartingContext().get(0).getID().get(0).getValue().equals( resultSummary.getID().get(0).getValue())){
                            total += new DecimalFormat("#.0").parse(resultSummary.getResultTicker(category).getCount()).longValue();
                        }
                    } catch (Exception ignore) {
                    }
                }
                return total;
            } else {
                return 0;
            }
        }


        public  String aggregatedPercentageByCategory(String category) {
            double total = aggregateByCategory("total");
            if (total == 0)
                return "0.0%";
            else {
                double sub = aggregateByCategory(category);
                return new DecimalFormat("#.0").format(sub * 100 / total) + "%";
            }
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

        public Tims.ResultsSummaryReport getResultsSummaryReport() {
            return resultsSummaryReport;
        }

        public void setResultsSummaryReport(Tims.ResultsSummaryReport resultsSummaryReport) {
            this.resultsSummaryReport = resultsSummaryReport;
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
