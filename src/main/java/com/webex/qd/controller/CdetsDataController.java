package com.webex.qd.controller;

import com.webex.qd.apiclient.HttpEngine;
import com.webex.qd.dao.CECInfoDao;
import com.webex.qd.util.JsonUtils;
import com.webex.qd.util.QdProperties;
import com.webex.qd.vo.CECInfo;
import com.webex.qd.widget.CdetsTrendWidget;
import com.webex.qd.widget.CdetsWidget;
import com.webex.qd.widget.IWidget;
import com.webex.qd.widget.WidgetManager;
import com.webex.qd.widget.cdets.CdetsCriteria;
import com.webex.qd.widget.cdets.CdetsServiceClient;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: justin
 * Date: 7/18/13
 * Time: 10:14 AM
 */
@Controller
@RequestMapping("/widget/cdets")
@Transactional
public class CdetsDataController {

    private static final String ERROR_RESULT = "{error:true}";
    @Autowired
    private WidgetManager widgetManager;
    @Autowired
    private CECInfoDao cecDao;
    @ResponseBody
    @RequestMapping(value = "cdets_daily_bugs")
    public String getCdetsDailyBugs(@RequestParam("wid") int widgetId,
                                    @RequestParam("idx") int index) throws IOException, ParseException {
        IWidget w = widgetManager.getWidgetById(widgetId);
        if (!(w instanceof CdetsTrendWidget)) {
            return "[]";
        }

        CdetsTrendWidget widget = (CdetsTrendWidget) w;
        CdetsTrendWidget.Configuration config = widget.getConfiguration();
        String whereClause = config.getQueries().get(index).getQuery();


        String[] sDailyDefects = getDailyDefects("S", whereClause);
        String[] rDailyDefects = getDailyDefects("R", whereClause);
        String[] vDailyDefects = getDailyDefects("V", whereClause);


        String begin = minDate(sDailyDefects, rDailyDefects, vDailyDefects);
        String end = maxDate(sDailyDefects, rDailyDefects, vDailyDefects);

        if (begin == null || end == null) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        dataToBuffer(sb, begin, end, sDailyDefects);
        sb.append(",");
        dataToBuffer(sb, begin, end, rDailyDefects);
        sb.append(",");
        dataToBuffer(sb, begin, end, vDailyDefects);

        sb.append("]");

        return sb.toString();
    }

    private static String[] ptns = new String[]{
            "yyyy-MM-dd"
    };

    private static void dataToBuffer(StringBuilder sb, String begin, String end, String[] data) throws ParseException {
        long days = getDaySpan(begin, end) + 1;

        List<Integer> lst = new ArrayList<Integer>();
        for (int i = 0; i < days; i++) {
            lst.add(0);
        }

        for (String d : data) {
            String[] dc = d.split(",");
            int idx = (int) getDaySpan(begin, dc[0]);
            lst.set(idx, Integer.parseInt(dc[1]));
        }

        long beginTs = DateUtils.parseDate(begin, ptns).getTime();
        sb.append("[");
        for (int i = 0; i < days; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append("[");
            sb.append(beginTs + MILLISECONDS_IN_A_DAY * i);
            sb.append(",");
            sb.append(lst.get(i));
            sb.append("]");
        }
        sb.append("]");
    }

    private static long getDaySpan(String begin, String cur) throws ParseException {
        Date beginDate = DateUtils.parseDate(begin, ptns);
        Date endDate = DateUtils.parseDate(cur, ptns);
        return (endDate.getTime() - beginDate.getTime()) / MILLISECONDS_IN_A_DAY;
    }

    private String maxDate(String[]... stringMatrix) {
        String max = "";
        if (stringMatrix.length > 0) {
            for(String[] sArray : stringMatrix) {
                if (sArray.length > 0) {
                    String lastElement = sArray[sArray.length - 1].split(",")[0];
                    max = max.compareTo(lastElement) < 0 ? lastElement : max;
                }
            }
        }
        return max;
    }

    private String minDate(String[]... stringMatrix) {
        String max = "9999";

        if (stringMatrix.length > 0) {
            for (String[] sArray : stringMatrix) {
                if (sArray.length > 0) {
                    String firstElement = sArray[0].split(",")[0];
                    max = max.compareTo(firstElement) > 0 ? firstElement : max;
                }
            }
        }
        return max.equals("9999") ? null : max;
    }

    private static String[] getDailyDefects(String time, String query) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("time", time));
        params.add(new BasicNameValuePair("query", query));

        String url = QdProperties.getCdetsServiceUrl();
        HttpEngine eng = new HttpEngine();
        eng.post(url + "/act/service/daily_defects", params);
        String html = eng.getHtml().trim();

        if ("".equals(html)) {
            return new String[0];
        }

        return html.split(";");
    }

    private static final long MILLISECONDS_IN_A_DAY = 1000 * 60 * 60 * 24;

    @ResponseBody
    @RequestMapping(value = "cdets_data_provider")
    public String cdetsDataProvider(@RequestParam("widgetId") int widgetId) {
        IWidget w = widgetManager.getWidgetById(widgetId);
        if (w instanceof CdetsWidget) {
            CdetsWidget widget = (CdetsWidget) w;
            List<CdetsCriteria> criterias = widget.getConfiguration().getCriterias();

            if (criterias == null || criterias.size() == 0) {
                return "[]";
            }

            try {
                return JsonUtils.toJson(loadData(criterias));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "[]";
    }

    private List<QueryResultMatrix> loadData(List<CdetsCriteria> criterias) throws IOException {
         List<QueryResultMatrix> matrixes = new LinkedList<QueryResultMatrix>();

        for (CdetsCriteria criteria : criterias) {
            matrixes.add(loadDefectNumberBySeverityStatus(criteria));
        }
        return matrixes;
    }

    public static final class QueryResultMatrix {
        private String name;
        private Map<String, Integer> data;
        private String query;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Integer> getData() {
            return data;
        }

        public void setData(Map<String, Integer> data) {
            this.data = data;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }


    private QueryResultMatrix loadDefectNumberBySeverityStatus(CdetsCriteria criteria) throws IOException {
        CdetsServiceClient client = new CdetsServiceClient();
        Map<String, Integer> data = client.queryDefectNumberBySeverityAndStatus(criteria.getAdvancedQuery());
        QueryResultMatrix matrix = new QueryResultMatrix();
        matrix.setName(criteria.getDisplayName());
        matrix.setQuery(replaceVariables(criteria.getAdvancedQuery()));
        if (StringUtils.isNotBlank(criteria.getDetailsUrl())) {
            matrix.setUrl(criteria.getDetailsUrl());
        }
        matrix.setData(data);
        return matrix;
    }


    private String replaceVariables(String query) {
        Pattern pattern = Pattern.compile("\\[?([\\w\\d\\-_]+)\\]?\\s*(<|=|>|<>|<=|>=|LIKE|NOT LIKE|IS NULL|IS NOT NULL)\\s*'([^']*)'", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        StringBuilder result = new StringBuilder();

        int cursor = 0;
        while (!matcher.hitEnd()) {
            if (matcher.find()) {
                int startOfFieldValue = matcher.start(3);
                int endOfFieldValue = matcher.end(3);

                if (startOfFieldValue > cursor) {
                    result.append(query.substring(cursor, startOfFieldValue));
                }

                String fieldName = matcher.group(1);
                String fieldValue = matcher.group(3);

                if (fieldName.endsWith("-on") || fieldName.equalsIgnoreCase("Sys-Last-Updated")) {
                    String newDatetime = datetimeReplace(fieldValue);
                    result.append(newDatetime);
                } else {
                    result.append(fieldValue);
                }
                cursor = endOfFieldValue;
            } else {
                result.append(query.substring(cursor));
            }
        }

        return result.toString();
    }


    private String datetimeReplace(String strDatetime) {
        Date date = strToDate(strDatetime);

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        if (date != null) {
            return sdf.format(date);
        }

        if (strDatetime.toLowerCase().contains("today")) {
            Pattern p = Pattern.compile("today\\s*(\\+|-)\\s*(\\d+)(d|m|y)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(strDatetime);

            if (matcher.find()) {
                int sign = matcher.group(1).equalsIgnoreCase("+") ? 1 : -1;
                int val = Integer.parseInt(matcher.group(2));
                int field = matcher.group(3).equalsIgnoreCase("d") ? Calendar.DAY_OF_YEAR : matcher.group(3).equalsIgnoreCase("m") ? Calendar.MONTH : Calendar.YEAR;
                cal.add(field, sign * val);
            }
            return sdf.format(cal.getTime());
        }

        return strDatetime;
    }

    private Date strToDate(String str) {
        try {
            return org.apache.commons.lang.time.DateUtils.parseDate(str, new String[]{
                    "MM/dd/yyyy",
                    "MM/dd/yyyy hh:mm:ss",
                    "yyyy-MM-dd",
                    "yyyy-MM-dd hh:mm:ss"
            });
        } catch (ParseException e) {
            return null;
        }
    }


    @ResponseBody
    @RequestMapping(value = "/bycomp")
    public String groupByComponent(int widgetId, int idx) {
        IWidget w = widgetManager.getWidgetById(widgetId);
        if (!(w instanceof CdetsWidget)) {
            return ERROR_RESULT;
        }

        CdetsWidget widget = (CdetsWidget) w;
        CdetsWidget.Configuration config = widget.getConfiguration();

        if (config.getCriterias().size() < idx) {
            return ERROR_RESULT;
        }

        String advancedQuery = config.getCriterias().get(idx).getAdvancedQuery();
        try {
            return getGroupByComponent(advancedQuery);
        } catch (IOException e) {
            return ERROR_RESULT;
        }
    }


    private String getGroupByComponent(String query) throws IOException {
        String url = QdProperties.getCdetsServiceUrl();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("query", query));


        HttpEngine eng = new HttpEngine();
        eng.post(url + "/act/service/bycomp", params);
        String html = eng.getHtml().trim();

        return html;
    }


    @ResponseBody
    @RequestMapping(value = "/by")
    public String groupBy(String field, int widgetId, int idx) {
        IWidget w = widgetManager.getWidgetById(widgetId);
        if (!(w instanceof CdetsWidget)) {
            return ERROR_RESULT;
        }

        CdetsWidget widget = (CdetsWidget) w;
        CdetsWidget.Configuration config = widget.getConfiguration();

        if (config.getCriterias().size() < idx) {
            return ERROR_RESULT;
        }

        String advancedQuery = config.getCriterias().get(idx).getAdvancedQuery();


        try {
            return getGroupBy(field, advancedQuery);
        } catch (IOException e) {
            return ERROR_RESULT;
        }
    }


    private String getGroupBy(String field, String query) throws IOException {
        String url = QdProperties.getCdetsServiceUrl();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("query", query));
        params.add(new BasicNameValuePair("field", field));


        HttpEngine eng = new HttpEngine();
        eng.post(url + "/act/service/groupby", params);
        String html = eng.getHtml().trim();

        return html;
    }
    public String toJsonStrWithExcludeProperties (ObjectMapper objectMapper, Object value, String[] properties2Exclude) throws JsonGenerationException,
            IOException {
        SimpleFilterProvider provider = new SimpleFilterProvider();
        Set<String> excuteSet = new HashSet<String>();
        for (String property : properties2Exclude) {
            excuteSet.add(property);
        }
        SimpleBeanPropertyFilter filter = new SimpleBeanPropertyFilter.SerializeExceptFilter(excuteSet);
        provider.addFilter("info", filter);
        return objectMapper.writer(provider).writeValueAsString(value);

    }
    @ResponseBody
    @RequestMapping(value = "/by/advanced")
    public String advancedGroupBy(String field, int widgetId, int idx) {
        String data = groupBy(field, widgetId, idx);
        ObjectMapper objectMapper = new ObjectMapper();
        Group group;
        data = fixJSON(data);
        try {
           group = objectMapper.readValue(data,Group.class);
           List<AdvancedElement> list = createElementsByGroup(group);
           data = toJsonStrWithExcludeProperties(objectMapper,list,new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
           data = null;
        }
        return data == null?"{}":data;
    }
    private String fixJSON(String data){
        data = data.contains("\"data\"")?data:data.replaceFirst("data","\"data\"");
        data = data.contains("\"error\"")?data:data.replaceFirst("error","\"error\"");
        return data;
    }
    private List<AdvancedElement> createElementsByGroup(Group group) throws Exception {
        List<AdvancedElement> list = new LinkedList<AdvancedElement>();
        for (String cec:group.data.keySet()){
            list.add(new AdvancedElement(cecDao.getCECInfoByCec(cec),group.data.get(cec)));
        }
        return list;
    }
    public static class Group{
        private String error;
        private HashMap<String,Integer> data;

        public HashMap<String, Integer> getData() {
            return data;
        }

        public void setData(HashMap<String, Integer> data) {
            this.data = data;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
    public static class AdvancedElement{
       private CECInfo cec_info;
       private Integer value;

        public CECInfo getCec_info() {
            return cec_info;
        }

        public void setCec_info(CECInfo cec_info) {
            this.cec_info = cec_info;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public AdvancedElement() {
        }

        public AdvancedElement(CECInfo cecInfo, Integer value) {
            this.cec_info = cecInfo;
            this.value = value;
        }
    }
}
