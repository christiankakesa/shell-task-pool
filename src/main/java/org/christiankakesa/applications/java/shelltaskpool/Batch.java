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

	public void setStartDate(Date startDate) {
		this.setStatus(BatchStatus.STARTED);
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		this.setStatusWithJobsState();
	}

	/**
	 * Get the string representation of batch duration.
	 * <ul>
	 * <li>format : "00:00:00" - "hours:minutes:seconds"</li>
	 * </ul>
	 * @return string duration formated
	 */
	public String getDuration() {
		return Utils.buildDurationFromDates(this.getEndDate(),
				this.getStartDate());
	}

	public BatchStatus getStatus() {
		return status;
	}

	public void setStatus(BatchStatus batchStatus) {
		this.status = batchStatus;
	}

	/**
	 * Look at the jobSuccess and jobFailed to determine last Batch status.
	 * @return void
	 */
	public void setStatusWithJobsState() {
		/** Batch completed success full. */
		if (this.jobFailed.get() == 0 && this.jobSuccess.get() >= 1) {
			this.setStatus(BatchStatus.COMPLETED);
		} /** Batch completed but there are job failed */
		else if (this.jobFailed.get() > 0 && this.jobSuccess.get() >= 1) {
			this.setStatus(BatchStatus.COMPLETED_WITH_ERROR);
		} else {/** Means that all jobs failed or unknown problem. */
			this.setStatus(BatchStatus.FAILED);
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
			if (this.status != BatchStatus.RUNNING) {
				this.status = BatchStatus.RUNNING;
			}
			this.jobCounterId.incrementAndGet();
			je.setId(this.jobCounterId.get());
		}
	}

	public List<JobExecution> getJobExecutionList() {
		return jobExecutionList;
	}

	/**
	 * Batch status enumeration.
	 * @author christian
	 */
	public static enum BatchStatus {
		NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED
	}
}
