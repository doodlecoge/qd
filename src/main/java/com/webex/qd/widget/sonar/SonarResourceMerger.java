package com.webex.qd.widget.sonar;

import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vega
 * Date: 13-10-28
 * Time: 下午3:23
 */
public class SonarResourceMerger {

    public static final List<String> MERGABLE_METRICS = new LinkedList<String>();
    static {
        MERGABLE_METRICS.add("ncloc");
        MERGABLE_METRICS.add("lines");
        MERGABLE_METRICS.add("statements");
        MERGABLE_METRICS.add("files");
        MERGABLE_METRICS.add("classes");
        MERGABLE_METRICS.add("packages");
        MERGABLE_METRICS.add("functions");
        MERGABLE_METRICS.add("accessors");
        MERGABLE_METRICS.add("violations");
        MERGABLE_METRICS.add("blocker_violations");
        MERGABLE_METRICS.add("critical_violations");
        MERGABLE_METRICS.add("major_violations");
        MERGABLE_METRICS.add("minor_violations");
        MERGABLE_METRICS.add("info_violations");
        MERGABLE_METRICS.add("technical_debt_days");
        MERGABLE_METRICS.add("technical_debt");
//        MERGABLE_METRICS.add("technical_debt_repart");     // TODO
        MERGABLE_METRICS.add("test_failures");
        MERGABLE_METRICS.add("test_errors");
        MERGABLE_METRICS.add("tests");
        MERGABLE_METRICS.add("skipped_tests");
        MERGABLE_METRICS.add("test_execution_time");
        MERGABLE_METRICS.add("lines_to_cover");
        MERGABLE_METRICS.add("uncovered_lines");
        MERGABLE_METRICS.add("conditions_to_cover");
        MERGABLE_METRICS.add("uncovered_conditions");
    }


    private Resource merged = new Resource();

    public SonarResourceMerger() {
    }

    public Resource getMergedResource() {
        return merged;
    }

    public void merge(Resource resource) {
        Resource merged = new Resource();
        merged.setMeasures(new LinkedList<Measure>());
        for (String metric : MERGABLE_METRICS) {
            Measure mergedMeasure = merge(this.merged.getMeasure(metric), resource.getMeasure(metric));
            if (mergedMeasure != null)
            merged.getMeasures().add(mergedMeasure);
        }
        this.merged = merged;
        calcCoverage();
        calcUnitTestPassrate();
    }

    private void calcUnitTestPassrate() {
        double tests = 0;
        double failures = 0;
        if (merged.getMeasure("tests") != null) {
            tests = merged.getMeasureValue("tests");
        }

        if (merged.getMeasure("test_failures") != null) {
            failures += merged.getMeasureValue("test_failures");
        }

        if (merged.getMeasure("test_errors") != null) {
            failures += merged.getMeasureValue("test_errors");
        }

        if (tests > 0) {
            DecimalFormat df = new DecimalFormat("#.0");
            double passrate = (tests - failures) / tests * 100;
            Measure passrateMeasure = new Measure();
            passrateMeasure.setMetricKey("test_success_density");
            passrateMeasure.setValue(passrate);
            passrateMeasure.setFormattedValue(df.format(passrate) + "%");
            merged.getMeasures().add(passrateMeasure);
        }
    }

    private void calcCoverage() {
        double total_to_cover = 0, total_un_covered = 0;
        double lines_to_cover = 0, lines_un_covered = 0;
        double branches_to_cover = 0, branches_un_covered = 0;
        if (merged.getMeasure("lines_to_cover") != null) {
            total_to_cover += merged.getMeasureValue("lines_to_cover");
            lines_to_cover = merged.getMeasureValue("lines_to_cover");
        }
        if (merged.getMeasure("conditions_to_cover") != null) {
            total_to_cover += merged.getMeasureValue("conditions_to_cover");
            branches_to_cover = merged.getMeasureValue("conditions_to_cover");
        }
        if (merged.getMeasure("uncovered_lines") != null) {
            total_un_covered += merged.getMeasureValue("uncovered_lines");
            lines_un_covered = merged.getMeasureValue("uncovered_lines");
        }
        if (merged.getMeasure("uncovered_conditions") != null) {
            total_un_covered += merged.getMeasureValue("uncovered_conditions");
            branches_un_covered = merged.getMeasureValue("uncovered_conditions");
        }

        DecimalFormat df = new DecimalFormat("#.0");
        if (total_to_cover > 0) {
            double coverage = (total_to_cover - total_un_covered) / total_to_cover * 100;
            Measure coverage_measure = new Measure();
            coverage_measure.setMetricKey("coverage");
            coverage_measure.setValue(coverage);
            coverage_measure.setFormattedValue(df.format(coverage) + "%");
            merged.getMeasures().add(coverage_measure);
        }

        if (lines_to_cover > 0) {
            double line_coverage = (lines_to_cover - lines_un_covered) / lines_to_cover * 100;
            Measure coverage_measure = new Measure();
            coverage_measure.setMetricKey("line_coverage");
            coverage_measure.setValue(line_coverage);
            coverage_measure.setFormattedValue(df.format(line_coverage) + "%");
            merged.getMeasures().add(coverage_measure);
        }

        if (branches_to_cover > 0) {
            double branch_coverage = (branches_to_cover - branches_un_covered) / branches_to_cover * 100;
            Measure coverage_measure = new Measure();
            coverage_measure.setMetricKey("branch_coverage");
            coverage_measure.setValue(branch_coverage);
            coverage_measure.setFormattedValue(df.format(branch_coverage) + "%");
            merged.getMeasures().add(coverage_measure);
        }
    }

    private static Measure merge(Measure measure1, Measure measure2) {
        if (measure1 == null && measure2 == null) {
            return null;
        } else if (measure2 == null) {
            return measure1;
        } else if (measure1 == null) {
            return measure2;
        }

        Measure merged = new Measure() {
            @Override
            public String getFormattedValue() {
                if(super.getFormattedValue() != null) {
                    return super.getFormattedValue();
                } else {
                    return "-";
                }
            }
        };
        merged.setMetricKey(measure1.getMetricKey());
        merged.setValue(measure1.getValue() + measure2.getValue());
        merged.setFormattedValue(DecimalFormat.getNumberInstance().format(merged.getValue()));
        return merged;
    }
}
