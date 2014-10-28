package com.webex.qd.controller;

import com.webex.qd.apiclient.HttpEngine;
import com.webex.qd.widget.TimsAutomationCaseStatsWidget;
import com.webex.qd.widget.TimsReportWidget;
import com.webex.qd.widget.WidgetManager;
import com.webex.qd.widget.tims.SearchResult;
import com.webex.qd.widget.tims.TimsSearchRunner;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.StandardURLTagFragmentGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Author: justin
 * Date: 7/31/13
 * Time: 8:46 AM
 */
@Controller
@RequestMapping("/widget/tims")
public class TimsWidgetController {

    @Autowired
    private WidgetManager widgetManager;

    @RequestMapping(value = "entity_name/{entityId}", method = RequestMethod.GET )
    public @ResponseBody String translateEntityId(@PathVariable("entityId") String entityId) {
        HttpEngine engine = new HttpEngine();
        try {
            String xml = engine.get("http://tims.cisex.com/xml/" + entityId + "/entity.svc").getHtml();
            int cursorStart = xml.indexOf("<Title><![CDATA[");
            int cursorEnd = xml.indexOf("]]></Title>");
            return xml.substring(cursorStart + "<Title><![CDATA[".length(), cursorEnd);
        } catch (Exception e) {
            return entityId;
        }
    }

    @RequestMapping(value = "table_row", method = RequestMethod.GET)
    public String rowForTableView(@RequestParam("reportId") String reportId, ModelMap model) {
        TimsReportWidget.Data reportData = TimsReportWidget.retrieveReport(reportId);
        model.put("data", reportData);
        return "/widget/tims_report_table/row";
    }

    @RequestMapping(value = "/chart")
    public void showChart(@RequestParam("numbers")String numbers,
                          HttpServletResponse response) {
        response.setContentType("image/png");
        String[] nums = numbers.split(",");

        ChartRenderingInfo info = new ChartRenderingInfo();
        DefaultPieDataset dataSet = new DefaultPieDataset();

        dataSet.setValue("Automated", extractNumber(nums, 0));
        dataSet.setValue("To-Be Automated", extractNumber(nums, 1));
        dataSet.setValue("Not Automatable", extractNumber(nums, 2));
        JFreeChart chart = ChartFactory.createPieChart(null, dataSet, false, false, false);
        chart.setBackgroundPaint(Color.white);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setOutlinePaint(null);
        plot.setBackgroundPaint(Color.white);
        plot.setSectionPaint(0, Color.green);
        plot.setSectionPaint(1, Color.blue);
        plot.setSectionPaint(2, Color.yellow);

        BufferedImage image = chart.createBufferedImage(500, 300, info);
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "PNG", os);
        } catch (IOException ignore) {
        }
    }

    private static class ChartLabel implements Comparable<ChartLabel> {
        private String displayName;
        private TimsAutomationCaseStatsWidget.ConfigEntry config;

        public ChartLabel(String displayName, TimsAutomationCaseStatsWidget.ConfigEntry config) {
            this.displayName = displayName;
            this.config = config;
        }

        @Override
        public int compareTo(ChartLabel chartLabel) {
            return displayName.compareTo(chartLabel.displayName);
        }

        private String getDisplayName() {
            return displayName;
        }

        private void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        private TimsAutomationCaseStatsWidget.ConfigEntry getConfig() {
            return config;
        }

        private void setConfig(TimsAutomationCaseStatsWidget.ConfigEntry config) {
            this.config = config;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public static JFreeChart buildChart(TimsAutomationCaseStatsWidget.Configuration widgetConfig) {
        java.util.List<TimsAutomationCaseStatsWidget.ConfigEntry> configEntries = widgetConfig.getConfigEntries();
        List<String> searchIds = new LinkedList<String>();
        for(TimsAutomationCaseStatsWidget.ConfigEntry configEntry : configEntries) {
            searchIds.add(configEntry.getAutomatedSearchId());
            searchIds.add(configEntry.getNotAutomatedSearchId());
            searchIds.add(configEntry.getTobeAutomatedSearchId());
        }
        TimsSearchRunner runner = new TimsSearchRunner();
        Map<String, SearchResult> searchResults = runner.runSearches(searchIds.toArray(new String[searchIds.size()]));
        double[][] data = new double[3][configEntries.size()];
        Comparable[] yAxis = new Comparable[configEntries.size()];
        int i = 0;
        for (TimsAutomationCaseStatsWidget.ConfigEntry configEntry : configEntries) {
            data[0][i] = searchResults.get(configEntry.getAutomatedSearchId()).getCount();
            data[1][i] = searchResults.get(configEntry.getTobeAutomatedSearchId()).getCount();
            data[2][i] = searchResults.get(configEntry.getNotAutomatedSearchId()).getCount();
            yAxis[i] = new ChartLabel(configEntry.getDisplayName(), configEntry);
            i++;
        }


//        double[][] data = new double[][]{
//                {210, 300, 320, 265, 299, 200},   //Automated
//                {200, 304, 201, 201, 340, 300},   //Tobe Automated
//                {100, 0, 1, 3, 10, 20}            //Not Automatable
//        };
        CategoryDataset dataSet = DatasetUtilities.createCategoryDataset(
                new Comparable[]{"Automated", "To-Be Automated", "Not Automatable"},
                yAxis,
//                new Comparable[]{new ChartLabel("OAuth", new TimsAutomationCaseStatsWidget.ConfigEntry("", "Tyb14294s", "Tyb14297s", "Tyb14296s")),
//                        new ChartLabel("Common Identity", new TimsAutomationCaseStatsWidget.ConfigEntry("", "Tyb18421s", "Tyb18424s", "Tyb18425s")),
//                        new ChartLabel("Messenger", new TimsAutomationCaseStatsWidget.ConfigEntry("", "Tyb14305s", "Tyb14308s", "Tyb14306s")),
//                        new ChartLabel("RAMP", new TimsAutomationCaseStatsWidget.ConfigEntry("", "Tyb14314s", "Tyb14316s", "Tyb14315s")),
//                        new ChartLabel("OAuth", new TimsAutomationCaseStatsWidget.ConfigEntry("", "Tyb14350s", "Tyb14352s", "Tyb14351s")),
//                        new ChartLabel("OAuth", new TimsAutomationCaseStatsWidget.ConfigEntry("", "Tyb18049s", "Tyb18052s", "Tyb18053s"))},
                data);

        JFreeChart chart = ChartFactory.createStackedBarChart(null, null, "amount", dataSet, PlotOrientation.VERTICAL, true, true, true);
        Plot plot = chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        CategoryPlot categoryPlot = (CategoryPlot) plot;
        categoryPlot.getRenderer().setSeriesPaint(0, Color.green);
        categoryPlot.getRenderer().setSeriesPaint(1, Color.blue);
        categoryPlot.getRenderer().setSeriesPaint(2, Color.orange);
        categoryPlot.getRenderer().setBaseItemURLGenerator(new CategoryURLGenerator() {
            @Override
            public String generateURL(CategoryDataset dataset, int series, int category) {
                ChartLabel label = (ChartLabel)dataset.getColumnKey(category);
                String searchId = "";
                switch (series) {
                    case 0:
                        searchId = label.getConfig().getAutomatedSearchId();
                        break;
                    case 1:
                        searchId = label.getConfig().getTobeAutomatedSearchId();
                        break;
                    case 2:
                        searchId = label.getConfig().getNotAutomatedSearchId();
                        break;
                }
                return "http://tims.cisex.com/warp.cmd?ent=" + searchId;
            }
        });
        categoryPlot.getRenderer().setBaseToolTipGenerator(new CategoryToolTipGenerator() {
            @Override
            public String generateToolTip(CategoryDataset dataset, int row, int column) {
                ChartLabel label = (ChartLabel)dataset.getColumnKey(column);
                Number value = dataset.getValue(row, column);
                String searchId = "";
                switch (row) {
                    case 0:
                        searchId = label.getConfig().getAutomatedSearchId();
                        break;
                    case 1:
                        searchId = label.getConfig().getTobeAutomatedSearchId();
                        break;
                    case 2:
                        searchId = label.getConfig().getNotAutomatedSearchId();
                        break;
                }
                return String.valueOf(value) + " (" + searchId + ")";
            }
        });
        return chart;
    }

    public static String createMap(TimsAutomationCaseStatsWidget.Configuration widgetConfig) {
        JFreeChart chart = buildChart(widgetConfig);
        ChartRenderingInfo info = new ChartRenderingInfo();
        chart.createBufferedImage(500, 300, info);
        return ChartUtilities.getImageMap("infoMap", info, new StandardToolTipTagFragmentGenerator(), new StandardURLTagFragmentGenerator() {
            @Override
            public String generateURLFragment(String urlText) {
                return " href=\"" + urlText + "\" target=\"_blank\"";
            }
        });
    }

    @RequestMapping(value = "/barchart")
    public void showBarChart(@RequestParam("widgetId") int widgetId, HttpServletResponse response) {
        response.setContentType("image/png");
        JFreeChart chart = buildChart((TimsAutomationCaseStatsWidget.Configuration) widgetManager.getWidgetById(widgetId).getConfiguration());
        ChartRenderingInfo info = new ChartRenderingInfo();
        BufferedImage image = chart.createBufferedImage(500, 300, info);
        try {

            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "PNG", os);
        } catch (IOException ignore) {
            ignore.printStackTrace();
        }
    }

    private int extractNumber(String[] numbers, int index) {
        if (numbers == null || numbers.length == 0) {
            return 0;
        }
        if (numbers.length > index) {
            try {
                return Integer.valueOf(numbers[index]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }


    @RequestMapping(value = "/map/${widgetId}")
    public void showMap(@PathVariable("widgetId") int widgetId, HttpServletResponse response) {

    }

}
