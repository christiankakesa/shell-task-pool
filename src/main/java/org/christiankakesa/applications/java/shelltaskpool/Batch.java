package org.christiankakesa.applications.java.shelltaskpool;

//import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Store all informations about Batch and Jobs
 */
public final class Batch {
	// private static final org.apache.commons.logging.Log LOG =
	// LogFactory.getLog(Batch.class.getName());
	/**
	 * Static singleton idiom
	 * @link http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static final Batch INSTANCE = new Batch();
	private volatile String name;
	private volatile String id;
	private volatile Date startDate;
	private volatile Date endDate;
	private volatile BatchStatus status = BatchStatus.NONE;
	private AtomicLong jobCounterId = new AtomicLong();
	private AtomicLong jobSuccess = new AtomicLong();
	private AtomicLong jobFailed = new AtomicLong();
	private List<JobExecution> jobExecutionList = (List<JobExecution>) Collections
			.synchronizedList(new ArrayList<JobExecution>());

	private Batch() {
	}

	public static Batch getInstance() {
		return INSTANCE;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		/**
		 * Exit the method when parameter is <code>null</code> or empty.
		 */
		if (name == null || name.isEmpty()) {
			return;
		}
		/**
		 *  Set the batch name and batch id only if no name given before
		 */
		if (this.name == null) {
			this.name = name;
			//this.batchId = Utils.hexSHA1(this.batchName);
			this.id = Utils.buildUUID();
		}
	}

	public String getId() {
		return id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
		this.setStatus(BatchStatus.STARTED);
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public BatchStatus getStatus() {
		return status;
	}

	public void setStatus(final BatchStatus batchStatus) {
		this.status = batchStatus;
	}

	public AtomicLong getJobSuccess() {
		return jobSuccess;
	}

	public void setJobSuccess(final AtomicLong jobSuccess) {
		this.jobSuccess = jobSuccess;
	}

	public AtomicLong getJobFailed() {
		return jobFailed;
	}

	public void setJobFailed(final AtomicLong jobFailed) {
		this.jobFailed = jobFailed;
	}
	
	/**
	 * Add job to the jobExecutionList.
	 * @param je
	 * @return 
	 */
	public boolean addJobExecution(final JobExecution jobExecution) {
		boolean result = false;
		if (this.jobExecutionList.add(jobExecution)) { //Add a job and test if adding job is successful
			result = true;
			if (this.status != BatchStatus.RUNNING) { //Set Batch status to running if not set to BatchStatus.RUNNING
				this.status = BatchStatus.RUNNING;
			}
		}
		return result;
	}

//	public List<JobExecution> getJobExecutionList() {
//		return jobExecutionList;
//	}

	public AtomicLong getJobCounterId() {
		return jobCounterId;
	}

	public void setJobCounterId(final AtomicLong jobCounterId) {
		this.jobCounterId = jobCounterId;
	}

	/**
	 * Batch status enumeration.
	 * @author christian
	 */
	public static enum BatchStatus {
		NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED
	}
}
