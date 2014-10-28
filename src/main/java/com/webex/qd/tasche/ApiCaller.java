package com.webex.qd.tasche;

import com.webex.qd.apiclient.HttpEngine;
import org.apache.commons.lang.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: justin
 * Date: 7/10/13
 * Time: 2:52 PM
 */
public class ApiCaller {
    private static final Logger LOG = LoggerFactory.getLogger(ApiCaller.class);

    private static final String URL = "http://10.224.138.200/tasche/api/call.action";
    private static final boolean DEBUG = false;

    private ApiCaller() {

    }

//    http://10.224.138.200/tasche/api/call.action?api=GetOverallCoverageStats&xml=%3Cprojects%3E%3Cproject%3EWebex11_PT%3C/project%3E%3Cbuilds%3E5%3C/builds%3E%3C/projects%3E

    private static Set<String> getProjectCodes(String api) throws IOException, DocumentException {
        HttpEngine eng = new HttpEngine();
        Map<String, String> params = new HashMap<String, String>();
        params.put("api", api);
        String xml = eng.post(URL, params).getHtml();

        SAXReader reader = new SAXReader();
        Document doc = reader.read(new StringReader(xml));

        List<Node> nodes = doc.selectNodes("/ApiResult/xml/projectCodes/projectCode");

        Set<String> set = new HashSet<String>();

        int len = nodes.size();

        for (int i = 0; i < len; i++) {
            String txt = nodes.get(i).getText().trim();
            txt = txt.replace("\"", "\\\"");

            if (!"".equals(txt)) {
                set.add(txt);
            }
        }

        return set;
    }

    public static Set<String> getProjectCodes() throws IOException, DocumentException {
        if (!DEBUG) {
            Set<String> set1 = getProjectCodes("ListProjectCode");
            Set<String> set2 = getProjectCodes("ListCoverageProjectCode");

            set1.addAll(set2);

            return set1;
        } else {

            Set<String> set = new HashSet<String>();

            String str = "GlobalService,WAPI1.X,CAS,ConferenceService,CSAPI,CAS,DMS1.4.3,GlobalService,WAPI1.X,CSAPI,GlobalService,CAS,ConferenceService,WAPI1.X,CSAPI,aaaa,CAS,UserService,GlobalService,CSAPI,WAPI_Connect,Athena Waaf,cisex Jabber for Mac 8.6.3,URLAPI-WCC,Webex11_Feed_Notification,CAS,UserService,GlobalService,CSAPI,CAS,UserService,GlobalService,CSAPI,CAS,UserService,GlobalService,CSAPI,CAS,UserService,GlobalService,CSAPI,WAPI_Connect,CAS,UserService,GlobalService,CSAPI,WAPI_Connect,WAPI1.X,CSAPI,ServiceManager,UserService,DMS1.2.1,conference service,WAPI1.7EP1,UserService,ServiceManager,CSAPI,Webex11_Dashboard,Webex11_WebDoc,Webex11_Framework,Webex11_Identity,Webex11_Support,Webex11_FTE,Webex11_Feed_Notification,Webex11_EmailComponent,WebEx11_PT,Webex11_FTE,Webex11_Feed_Notification,Webex11_EmailComponent,Webex11_WebDoc,Webex11_Framework,Webex11_Dashboard,Webex11_Identity,Webex11_Support,CAS,WebEx11_PT,Webex11_Dashboard,Webex11_Support,Webex11_FTE,Webex11_Feed_Notification,Webex11_EmailComponent,Webex11_Account,Webex11_WebDoc,Webex11_Framework,Webex11_Search,Webex11_Identity,WebEx11_PT,Orion1.0_SSO,Orion1.0_Admin,Orion1.0_Web,Orion1.0_Client,Orion1.0_URLAPI,Orion1.0_PT,Orion1.0_XMLAPI,Orion1.0_SSO,Orion1.0_Admin,Orion1.0_Web,Orion1.0_Client,Orion1.0_URLAPI,Orion1.0_PT,Orion1.0_XMLAPI,Orion1.0_SSO,Orion1.0_Web,Orion1.0_Admin,Orion1.0_Client,Orion1.0_URLAPI,Orion1.0_PT,Orion1.0_XMLAPI,lisaxml571_400_410,DMS_BVT,lisaCertification_570_420_470,Orion1.0,0_0-85WAPI0512New,C6DevBVT,Orion-1348,,,,,CWCAPI,CommonPolicy1.0,RAMP_DI,RAMP_API,CSP,CommonIdentity,CWAPI_BVT,W11_comments,W11_contacts,W11_dashboard,W11_feed,W11_files,W11_login,W11_loginAssistant,W11_meetingDetail,W11_meetingList,W11_meetingpreference,W11_meetingScheduler,W11_meetingUrlAPI,W11_search,W11_support,W11_userpreference,W11_webdoc,W11_comments,W11_contacts,W11_dashboard,W11_feed,W11_files,W11_login,W11_loginAssistant,W11_meetingDetail,W11_meetingList,W11_meetingpreference,W11_meetingScheduler,W11_meetingUrlAPI,W11_search,W11_support,W11_userpreference,W11_webdoc,W11_comments,W11_contacts,W11_dashboard,W11_feed,W11_files,W11_login,W11_loginAssistant,W11_meetingDetail,W11_meetingList,W11_meetingpreference,W11_meetingScheduler,W11_meetingUrlAPI,W11_search,W11_support,W11_userpreference,W11_webdoc,RAMP_DI,RAMP_API,RAMP_DI,RAMP_API,UserService_2.3.3,UserService,CSAPI-1.2,CSAPI,GlobalService,JabberDemo,CAS,JabberDemo,CAS,WebIM_SDK_All_Interfaces,WebIM_SDK_For_Pebble_Beach,WebIM_SDK_All_Interfaces,WebIM_SDK_For_Pebble_Beach,WebIM_SDK_All_Interfaces,WebIM_SDK_For_Pebble_Beach,Pebble Beach CWCAPI,Pebble Beach Chat,Pebble Beach Conference,Pebble Beach Notes,Pebble Beach Participants,Pebble Beach Audio,Pebble Beach Share,Pebble Beach CWCAPI,Pebble Beach Chat,Pebble Beach Conference,Pebble Beach Notes,Pebble Beach Participants,Pebble Beach Audio,Pebble Beach Share,Pebble Beach CWCAPI,Pebble Beach Chat,Pebble Beach Conference,Pebble Beach Notes,Pebble Beach Participants,Pebble Beach Audio,Pebble Beach Share,CAS,UserService,CSAPI,CSAPI,UserService,CAS,CommonIdentity,CommonPolicy1.0,CWCAPI,CommonIdentity,CommonPolicy1.0,CWCAPI";

            String[] arr = str.split(",");

            Collections.addAll(set, arr);

            return set;
        }
    }

    private static String getReqXml(List<String> pcs, int builds, String filter) {
        Document doc = DocumentHelper.createDocument();

        Element xml = doc.addElement("xml");

        // builds
        xml.addElement("builds").addText("" + builds);

        if (StringUtils.isNotBlank(filter)) {
            xml.addElement("filter").addAttribute("like", filter);
        }

        // projects
        Element els = xml.addElement("projects");

        for (String pc : pcs) {
            els.addElement("project").addText(pc);
        }


        return doc.asXML();
    }

    private static String getReqXml(List<String> pcs, int builds, Date from, Date to) {
        Document doc = DocumentHelper.createDocument();

        Element xml = doc.addElement("xml");

        // builds
        xml.addElement("builds").addText("" + builds);

        // projects
        Element els = xml.addElement("projects");

        for (String pc : pcs) {
            els.addElement("project").addText(pc);
        }

        if (from == null && to == null) {
            return doc.asXML();
        }

        // date range filter
        Element filter = xml.addElement("filter");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (from != null) {
            filter.addAttribute("from", sdf.format(from));
        }

        if (to != null) {
            filter.addAttribute("to", sdf.format(to));
        }

        return doc.asXML();
    }

    private static String getRespXml(String reqXml, String api) throws IOException {
        HttpEngine eng = new HttpEngine();

        Map<String, String> params = new HashMap<String, String>();
        params.put("api", api);
        params.put("xml", reqXml);

        LOG.debug(URL);

        String respXml = eng.post(URL, params).getHtml();

        return respXml;
    }

    private static List<String> set2list(Set<String> set) {
        List<String> lst = new ArrayList<String>();

        for (String s : set) {
            lst.add(s);
        }

        return lst;
    }

    public static Map<String, List<TestStats>> getProjectPassrate(Set<String> pcs, int builds) throws IOException, DocumentException {
        return getProjectPassrate(pcs, builds, null, null);
    }

    public static Map<String, List<TestStats>> getProjectPassrate(Set<String> pcs, int builds, Date from, Date to) throws IOException, DocumentException {
        return getProjectPassrate(set2list(pcs), builds, from, to);
    }

    public static Map<String, List<TestStats>> getProjectPassrate(List<String> pcs, int builds) throws IOException, DocumentException {
        return getProjectPassrate(pcs, builds, null, null);
    }

    public static Map<String, List<TestStats>> getProjectPassrate(List<String> pcs, int builds, Date from, Date to) throws IOException, DocumentException {
        if (!DEBUG) {
            String reqXml = getReqXml(pcs, builds, from, to);

            String respXml = getRespXml(reqXml, "GetOverallStats");

            SAXReader reader = new SAXReader();
            Document doc = reader.read(new StringReader(respXml));
            List<Node> nodes = doc.selectNodes("/ApiResult/xml/projectStats/projectStat");

            Map<String, List<TestStats>> stats = new HashMap<String, List<TestStats>>();

            for (Node node : nodes) {
                Element el = (Element) node;
                String pc = el.attribute("projectCode").getValue();

                List<Node> buildStatNodes = el.selectNodes(".//buildStat");

                List<TestStats> lst = new ArrayList<TestStats>();
                if(builds != 0){
                    for (int i = 0; i < buildStatNodes.size() && i < builds; i++) {
                        Element element = (Element) buildStatNodes.get(i);

                        String bn = element.attribute("buildNumber").getValue();
                        int tt = Integer.valueOf(element.attribute("totalCase").getValue());
                        int ff = Integer.valueOf(element.attribute("failedCase").getValue());
                        String dt = element.attribute("dateCreated").getValue();

                        if (element.attribute("lastModifiedTime") != null) {
                            dt = element.attribute("lastModifiedTime").getValue();
                        }


                        lst.add(new TestStats(pc, bn, tt, ff, dt));
                    }
                }else{
                    //i =0 ,current row
                    //i>0, previous rows
                    int totalTt = 0;
                    int totalFf =0;
                    String createDate ="";
                    String buildNumCount ="0";

                    for (int i = 0; i < buildStatNodes.size() ; i++) {
                        Element element = (Element) buildStatNodes.get(i);

                        String bn = element.attribute("buildNumber").getValue();
                        int tt = Integer.valueOf(element.attribute("totalCase").getValue());
                        totalTt+=tt;
                        int ff = Integer.valueOf(element.attribute("failedCase").getValue());
                        totalFf+=ff;
                        String dt = element.attribute("dateCreated").getValue();
                        if(i == buildStatNodes.size()-1){
                            createDate = dt;
                        }
                        if (element.attribute("lastModifiedTime") != null) {
                            dt = element.attribute("lastModifiedTime").getValue();
                        }


                        if(i==0){
                            lst.add(new TestStats(pc, bn, tt, ff, dt));
                        }
                    }
                    if(buildStatNodes.size()>0){
                        buildNumCount = ""+buildStatNodes.size();
                        lst.add(new TestStats(pc,buildNumCount , totalTt, totalFf, createDate));
                    }
                }
                stats.put(pc, lst);
            }

            return stats;
        } else {


            Map<String, List<TestStats>> stats = new HashMap<String, List<TestStats>>();


            for (int x = 0; x < pcs.size(); x++) {
                List<TestStats> lst = new ArrayList<TestStats>();
                long ts = Calendar.getInstance().getTimeInMillis();

                for (int i = 0; i < builds; i++) {
                    int t = (int) (Math.random() * 1000 + 100);
                    int p = (int) (Math.random() * t);

                    long rnd = (long) (Math.random() * 1000 * 3600 * 24 * 30);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(ts - rnd);

                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    lst.add(new TestStats(pcs.get(x),
                            "build number - " + x + " - " + i, t, p, fmt.format(cal.getTime())));
                }

                stats.put(pcs.get(x), lst);
            }

            return stats;
        }
    }


    public static Map<String, List<TestStats>> getBuildPassrate(List<String> pcs, int builds, String filter) throws IOException, DocumentException {
        String reqXml = getReqXml(pcs, builds, filter);

        String respXml = getRespXml(reqXml, "GetBuildOverallStats");

        SAXReader reader = new SAXReader();
        Document doc = reader.read(new StringReader(respXml));
        List<Node> nodes = doc.selectNodes("/ApiResult/xml/buildStats/buildStat");

        Map<String, List<TestStats>> stats = new HashMap<String, List<TestStats>>();

        for (Node node : nodes) {
            Element el = (Element) node;
            String bn = el.attribute("buildNumber").getValue();

            List<Node> projectStatNodeList = el.selectNodes(".//projectStat");

            List<TestStats> lst = new ArrayList<TestStats>();
            for (int i = 0; i < projectStatNodeList.size() && i < builds; i++) {
                Element element = (Element) projectStatNodeList.get(i);


                String pc = element.attribute("projectCode").getValue();
                int tt = Integer.valueOf(element.attribute("totalCase").getValue());
                int ff = Integer.valueOf(element.attribute("failedCase").getValue());
                String dt = element.attribute("dateCreated").getValue();

                if (element.attribute("lastModifiedTime") != null) {
                    dt = element.attribute("lastModifiedTime").getValue();
                }

                lst.add(new TestStats(pc, bn, tt, ff, dt));
            }

            stats.put(bn, lst);
        }

        return stats;
    }


    /**
     * <ApiResult>
     * <statusCode>0</statusCode>
     * <statusMessage/>
     * <xml>
     * <projectStats>
     * <projectStat projectCode="Webex11_PT">
     * <buildStat buildNumber="Build Tue Apr 16 2013" total="3583" uncovered="1757" dateCreated="2013-04-16 03:10"/>
     * <buildStat buildNumber="Build Mon Apr 15 2013" total="3583" uncovered="1765" dateCreated="2013-04-15 07:36"/>
     * <buildStat buildNumber="Build Sun Apr 14 2013" total="3583" uncovered="1862" dateCreated="2013-04-14 08:17"/>
     * <buildStat buildNumber="Build Sat Apr 13 2013" total="3583" uncovered="1733" dateCreated="2013-04-13 07:27"/>
     * <buildStat buildNumber="Build Fri Apr 12 2013" total="3583" uncovered="1726" dateCreated="2013-04-12 02:26"/>
     * </projectStat>
     * <projectStat projectCode="Webex11_PT111"/>
     * </projectStats>
     * </xml>
     * </ApiResult>
     *
     * @param pcs
     * @param builds
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static Map<String, List<TestStats>> getCoverages(Set<String> pcs, int builds) throws IOException, DocumentException {
        return getCoverages(pcs, builds, null, null);
    }

    public static Map<String, List<TestStats>> getCoverages(Set<String> pcs, int builds, Date from, Date to) throws IOException, DocumentException {
        return getCoverages(set2list(pcs), builds, from, to);
    }

    public static Map<String, List<TestStats>> getCoverages(List<String> pcs, int builds) throws IOException, DocumentException {
        return getCoverages(pcs, builds, null, null);
    }

    public static Map<String, List<TestStats>> getCoverages(List<String> pcs, int builds, Date from, Date to) throws IOException, DocumentException {
        String reqXml = getReqXml(pcs, builds, from, to);

        String respXml = getRespXml(reqXml, "GetOverallCoverageStats");

        SAXReader reader = new SAXReader();
        Document doc = reader.read(new StringReader(respXml));
        List<Node> nodes = doc.selectNodes("/ApiResult/xml/projectStats/projectStat");

        Map<String, List<TestStats>> stats = new HashMap<String, List<TestStats>>();

        for (Node n : nodes) {
            Element el = (Element) n;


            String projectCode = el.attributeValue("projectCode");

            List<TestStats> coverages = new ArrayList<TestStats>();

            List<Node> nodesBuildStats = el.selectNodes(".//buildStat");

            for (Node nn : nodesBuildStats) {
                Element bs = (Element) nn;
                String bn = bs.attributeValue("buildNumber");
                int total = Integer.parseInt(bs.attributeValue("total"));
                int uncovered = Integer.parseInt(bs.attributeValue("uncovered"));
                String dateCreated = bs.attributeValue("dateCreated");
                String url = bs.attributeValue("detailUrl");
                if (url == null) {
                    url = "#";
                }

                if (!url.startsWith("/")) {
                    url = "/" + url;
                }

                url = "http://ta.webex.com.cn" + url;
                TestStats ts = new TestStats(projectCode, bn, total, uncovered, dateCreated, url);
                coverages.add(ts);
            }

            if (coverages.size() > 0) {
                stats.put(projectCode, coverages);
            }
        }


        return stats;

    }
}
