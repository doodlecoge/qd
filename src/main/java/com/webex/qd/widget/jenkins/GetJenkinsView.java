package com.webex.qd.widget.jenkins;

import com.webex.qd.apiclient.HttpEngine;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: justin
 * Date: 9/4/13
 * Time: 2:46 PM
 */
public class GetJenkinsView {

    private static final String QUERY_PARAM = "api/xml?depth=2&tree=jobs[name,displayName,color,healthReport[iconUrl,description,score]," +
            "lastSuccessfulBuild[timestamp,number,duration],lastFailedBuild[timestamp,number,duration],lastBuild[timestamp,number,duration]]";


    public static View getJenkinsView(String viewUrl) {
        HttpEngine engine = new HttpEngine();
        if (!viewUrl.endsWith("/")) {
            viewUrl = viewUrl + "/";
        }

        try {
            Document dom = engine.get(viewUrl + QUERY_PARAM).getXmlDocument();
            View view = new View();
            List jobNodes = dom.selectNodes("/*/job");
            for (Object o : jobNodes) {
                if (o instanceof Node) {
                    Job job = generateJob((Node)o);
                    if (job != null) {
                        view.addJob(job);
                    }
                }
            }
            return view;
        } catch (IOException ignore) {

        } catch (DocumentException ignore) {

        }
        return null;
    }

    private static Job generateJob(Node jobNode) {
        Node displayNameNode = jobNode.selectSingleNode("./displayName");
        Node nameNode = jobNode.selectSingleNode("./name");
        Job job;
        if (displayNameNode != null && nameNode != null) {
            job = new Job(nameNode.getText(), displayNameNode.getText());
        } else {
            return null;
        }

        job.setColor(jobNode.selectSingleNode("./color").getText());
        List<Job.HealthReport> healthReports = generateHealthReports(jobNode);
        for (Job.HealthReport healthReport : healthReports) {
            job.addHealthReport(healthReport);
        }

        Node lastFailedBuildNode = jobNode.selectSingleNode("./lastFailedBuild");
        if (lastFailedBuildNode != null) {
            job.setLastFailureBuild(generateBuild(lastFailedBuildNode));
        }

        Node lastSuccessfulBuildNode = jobNode.selectSingleNode("./lastSuccessfulBuild");
        if (lastSuccessfulBuildNode != null) {
            job.setLastSuccessBuild(generateBuild(lastSuccessfulBuildNode));
        }
        return job;
    }

    private static List<Job.HealthReport> generateHealthReports(Node jobNode) {
        List<Job.HealthReport> healthReports = new LinkedList<Job.HealthReport>();
        List healthReportNodes = jobNode.selectNodes("./healthReport");
        if (healthReportNodes != null) {
            for (Object n : healthReportNodes) {
                if (n instanceof Node) {
                    Node healthReportNode = (Node) n;
                    healthReports.add(new Job.HealthReport(
                            healthReportNode.selectSingleNode("./description").getText(),
                            healthReportNode.selectSingleNode("./iconUrl").getText(),
                            Integer.valueOf(healthReportNode.selectSingleNode("./score").getText())
                    ));
                }
            }
        }
        return healthReports;
    }

    private static Build generateBuild(Node buildNode) {
        if (buildNode == null) {
            return null;
        }
        Build build = new Build();
        build.setNumber(Integer.valueOf(buildNode.selectSingleNode("./number").getText()));
        build.setTimestamp(Long.valueOf(buildNode.selectSingleNode("./timestamp").getText()));
        build.setDuration(Long.valueOf(buildNode.selectSingleNode("./duration").getText()));
        return build;
    }
}
