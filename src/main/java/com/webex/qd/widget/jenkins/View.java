package com.webex.qd.widget.jenkins;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: justin
 * Date: 9/4/13
 * Time: 2:46 PM
 */
public class View {
    private List<Job> jobs = new LinkedList<Job>();

    public View() {

    }

    public void addJob(Job job) {
        jobs.add(job);
    }

    public List<Job> getJobs() {
        return jobs;
    }
}
