package com.webex.qd.controller;

import com.webex.qd.service.DailyChangeService;
import com.webex.qd.vo.DailyChanges;
import com.webex.qd.widget.DailyChangesWidget;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by chenshijie on 3/24/14.
 */
@Controller
@RequestMapping("/widget/daily_changes")
@Transactional
public class DailyChangesController {

    private String url = "/tasche/widget/managerList.action";

    @Autowired
    private DailyChangeService dailyChangeService;

    @RequestMapping(value = "/chart")
    public void showBarChart(@RequestParam("type") String type,
                             @RequestParam("fromTime") String fromTime,
                             @RequestParam("toTime") String toTime,
                             @RequestParam("repo") String repo,
                             @RequestParam("branch") String branch,
                             @RequestParam("user") String user,
                             @RequestParam("url") String url,
                             @RequestParam("limitNum") Integer limitNum,
                             HttpServletResponse response) {

        DailyChangesWidget.ConfigEntry configEntry = new DailyChangesWidget.ConfigEntry();
        configEntry.setType(type);
        configEntry.setFromTime(fromTime);
        configEntry.setToTime(toTime);
        configEntry.setRepo(repo);
        configEntry.setBranch(branch);
        configEntry.setUser(user);
        configEntry.setType(type);
        configEntry.setUrl(url);
        configEntry.setLimitNum(limitNum);
        List<DailyChanges> data = dailyChangeService.findDailyChangesByConditionSum(configEntry);
        Integer count = data.size();
        if (data == null || count == 0) {
            return;
        }
        ChartRenderingInfo info = new ChartRenderingInfo();
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        //set line name
        String series1 = "added";
//        String series2 = "modified";
        String series3 = "deleted";

        DailyChanges row = null;
        String title = "";
        String xBarTitle = "";
        String yBarTitle = "num";
        for (int i = 0; i < count; i++) {
            row = data.get(i);
            switch (Integer.valueOf(type)) {
                case 1://time
                    title = "sum of datas in per date";
                    dataSet.addValue(row.getAdd(), series1, row.getTime());
//                    dataSet.addValue(row.getModified(), series2, row.getTime());
                    dataSet.addValue(row.getDelete(), series3, row.getTime());
                    break;
                case 2://repo
                    title = "sum of datas in per repo";
                    dataSet.addValue(row.getAdd(), series1, row.getRepo());
//                    dataSet.addValue(row.getModified(), series2, row.getRepo());
                    dataSet.addValue(row.getDelete(), series3, row.getRepo());
                    break;
                case 3://branch
                    title = "sum of datas in per branch";
                    dataSet.addValue(row.getAdd(), series1, row.getBranch());
//                    dataSet.addValue(row.getModified(), series2, row.getBranch());
                    dataSet.addValue(row.getDelete(), series3, row.getBranch());
                    break;
                case 4://user
                    title = "sum of datas in per user";
                    dataSet.addValue(row.getAdd(), series1, row.getUser());
//                    dataSet.addValue(row.getModified(), series2, row.getUser());
                    dataSet.addValue(row.getDelete(), series3, row.getUser());
                    break;
                case 5://monthly
                    title = "monthly total";
                    xBarTitle = "month";
                    dataSet.addValue(row.getAdd(), series1, date2str(row.getTime()));
//                    dataSet.addValue(row.getModified(), series2, date2str(row.getTime()));
                    dataSet.addValue(row.getDelete(), series3, date2str(row.getTime()));
                    break;
                case 6: //tpye =6 group by user ,time
                    title = "monthly per person total";
                    xBarTitle = "month";
                    dataSet.addValue(row.getAdd(), row.getUser()+"__add", date2str(row.getTime()));
                    dataSet.addValue(row.getDelete(), row.getUser()+"__del", date2str(row.getTime()));
                    break;
                case 7: //tpye =7 group by week
                    title = "weekly total";
                    xBarTitle = "first day in week";
                    dataSet.addValue(row.getAdd(),series1, date2strYYYYMMDD(row.getTime()));
                    dataSet.addValue(row.getDelete(), series3, date2strYYYYMMDD(row.getTime()));
                    break;
                default:
                    return;
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(title, xBarTitle, yBarTitle, dataSet, PlotOrientation.VERTICAL, true, true, false);
        CategoryPlot plot = chart.getCategoryPlot();
        //设置jfreechart序列图曲线颜色
        CategoryItemRenderer xyLineRenderer = (CategoryItemRenderer) plot.getRenderer();

        if(type!="6"){
            xyLineRenderer.setSeriesPaint(0, new Color(0, 255, 0));
    //        xyLineRenderer.setSeriesPaint(1, new Color(0, 0, 255));
            xyLineRenderer.setSeriesPaint(1, new Color(255, 0, 0));
        }

        plot.setRangeGridlinesVisible(true); //是否显示格子线
        plot.setBackgroundAlpha(0.3f); //设置背景透明度

        Font labelFont = new Font("Arial", Font.TRUETYPE_FONT, 8);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(labelFont);//X轴坐标上数值字体
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        Integer width = 500;
        if (count > 6) {
            width = 500 + (count - 6) * 20;
        }

        BufferedImage image = chart.createBufferedImage(width, 300, info);
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "PNG", os);
        } catch (IOException ignore) {
        }
    }

    /**
     * sum monthly
     * date+1 day
     *
     * @param date
     * @return
     */
    private String date2str(Date date) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        date = cal.getTime();
        return format.format(date);
    }

    /**
     * sum monthly
     * date+1 day
     *
     * @param date
     * @return
     */
    private String date2strYYYYMMDD(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
