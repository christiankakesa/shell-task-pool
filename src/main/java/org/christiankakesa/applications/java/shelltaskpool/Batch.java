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
	 * 
	 * @link http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	public static final Batch INSTANCE = new Batch();
	private volatile String batchName;
	private volatile String batchId;
	private volatile Date batchStartDate;
	private volatile Date batchEndDate;
	private volatile BatchStatus batchStatus = BatchStatus.NONE;
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

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(final String batchName) {
		/**
		 * Exit the method when parameter is <code>null</code> or empty.
		 */
		if (batchName == null || batchName.isEmpty()) {
			return;
		}
		/**
		 *  Set the batch name and batch id only if no name given before
		 */
		if (this.batchName == null) {
			this.batchName = batchName;
			//this.batchId = Utils.hexSHA1(this.batchName);
			this.batchId = Utils.buildUUID();
		}
	}

	public String getBatchId() {
		return batchId;
	}

	public Date getBatchStartDate() {
		return batchStartDate;
	}

	public void setBatchStartDate(Date batchStartDate) {
		this.setBatchStatus(BatchStatus.STARTED);
		this.batchStartDate = batchStartDate;
	}

	public Date getBatchEndDate() {
		return batchEndDate;
	}

	public void setBatchEndDate(Date batchEndDate) {
		this.batchEndDate = batchEndDate;
		this.setBatchStatusWithJobStatus();
	}

	/**
	 * Get the string representation of batch duration.
	 * <ul>
	 * <li>format : "00:00:00" - "hours:minutes:seconds"</li>
	 * </ul>
	 * @return string duration formated
	 */
	public String getBatchDuration() {
		return Utils.buildDurationFromDates(this.getBatchEndDate(),
				this.getBatchStartDate());
	}

	public BatchStatus getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(BatchStatus batchStatus) {
		this.batchStatus = batchStatus;
	}

	/**
	 * Look at the jobSuccess and jobFailed to determine last Batch status.
	 * @return void
	 */
	public void setBatchStatusWithJobStatus() {
		/** Batch completed success full. */
		if (this.jobFailed.get() == 0 && this.jobSuccess.get() >= 1) {
			this.setBatchStatus(BatchStatus.COMPLETED);
		} /** Batch completed but there are job failed */
		else if (this.jobFailed.get() > 0 && this.jobSuccess.get() >= 1) {
			this.setBatchStatus(BatchStatus.COMPLETED_WITH_ERROR);
		} else {/** Means that all jobs failed or unknown problem. */
			this.setBatchStatus(BatchStatus.FAILED);
		}
	}

	public long getJobSuccess() {
		return jobSuccess.get();
	}

	public void setJobSuccess(long jobSuccess) {
		this.jobSuccess.set(jobSuccess);
	}

	public void incrementJobSuccess() {
			this.jobSuccess.incrementAndGet();
	}

	public long getJobFailed() {
		return jobFailed.get();
	}

	public void setJobFailed(long jobFailed) {
		this.jobFailed.set(jobFailed);
	}

	public void incrementJobFailed() {
		this.jobFailed.incrementAndGet();
	}

	/**
	 * Add job to the jobExecutionList and set a job ID.
	 * @param je
	 */
	public void addJobToExecute(final JobExecution je) {
		/**
		 * Add a job and test if adding job is successful.
		 */
		if (jobExecutionList.add(je)) {
			/**
			 * Set Batch status to running if not set to BatchStatus.RUNNING
			 */
			if (this.batchStatus != BatchStatus.RUNNING) {
				this.batchStatus = BatchStatus.RUNNING;
			}
			this.jobCounterId.incrementAndGet();
			je.setId(this.jobCounterId.get());
		}
	}

	public List<JobExecution> getJobExecutionList() {
		return jobExecutionList;
	}

	/**
	 * Enum of all the BatchStatus.
	 * @author christian
	 */
	public static enum BatchStatus {
		NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED
	}
}
