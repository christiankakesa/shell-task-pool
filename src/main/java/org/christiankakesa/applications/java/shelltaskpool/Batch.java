package org.christiankakesa.applications.java.shelltaskpool;

//import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 
 */
public class Batch {
    //private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(Statistics.class);

    private String batchName;
	private String batchId;
	private Date batchStartDate;
	private Date batchEndDate;
	private volatile BatchStatus batchStatus = BatchStatus.NONE;
	private volatile long jobCounterForJobId = 0;
    private List<JobExecution> jobExecutionList = (List<JobExecution>) Collections.synchronizedList(new ArrayList<JobExecution>());

    private Batch() {
    }

    private static class BatchHolder {
        public static final Batch INSTANCE = new Batch();
    }

    /**
     * TODO: Is it safe to remove synchronized here ?
     * @return
     */
    public static Batch getInstance() {
        return BatchHolder.INSTANCE;
    }

    public String getBatchName() {
        return batchName;
    }
    
    public void setBatchName(final String batchName) {
    	if (this.batchName == null) {
    		this.batchName = batchName;
    		this.batchId = Utils.stringToSHA1(this.batchName);
    	}
	}

    public String getBatchId() {
		return batchId;
	}

	public Date getBatchStartDate() {
		return batchStartDate;
	}

	public void setBatchStartDate(Date batchStartDate) {
		this.batchStatus = BatchStatus.STARTED;
		this.batchStartDate = batchStartDate;
	}

	public Date getBatchEndDate() {
		return batchEndDate;
	}

	public void setBatchEndDate(Date batchEndDate) {
		this.batchEndDate = batchEndDate;
		if (this.batchStatus == BatchStatus.RUNNING) /** Complete only batch was running so is not FAILED */
		this.batchStatus = BatchStatus.COMPLETED;
	}

	public BatchStatus getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(BatchStatus batchStatus) {
		this.batchStatus = batchStatus;
	}

	public long addJobExecution(final JobExecution je) {
        if (jobExecutionList.add(je)) {
        	++jobCounterForJobId;
        	this.batchStatus = BatchStatus.RUNNING;
        }
        return jobCounterForJobId;
    }

    public List<JobExecution> getJobExecutionList() {
        return jobExecutionList;
    }
    
    public static enum BatchStatus {
    	NONE,
    	STARTED,
    	RUNNING,
    	FAILED,
    	COMPLETED
    }
}
