package org.christiankakesa.applications.java.shelltaskpool;

//import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: christian
 * Date: 12/05/11
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public class Statistics {
    //private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(Statistics.class);

    private String jobStatsName;
    private ArrayList<JobStats> jobStatsList = (ArrayList<JobStats>) Collections.synchronizedList(new ArrayList<JobStats>());

    private Statistics() {
    }

    private static class StatisticsHolder {
        public static final Statistics INSTANCE = new Statistics();
    }

    public static Statistics getInstance() {
        return StatisticsHolder.INSTANCE;
    }

    public synchronized void setJobName(String jobName) {
        this.jobStatsName = jobName;
    }

    public String getJobName() {
        return jobStatsName;
    }

    public void addJobStats(JobStats js) {
        jobStatsList.add(js);
    }

    public ArrayList<JobStats> getJobList() {
        return jobStatsList;
    }
}
