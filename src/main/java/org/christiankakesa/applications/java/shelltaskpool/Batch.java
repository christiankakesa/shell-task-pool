package org.christiankakesa.applications.java.shelltaskpool;

//import org.apache.commons.logging.LogFactory;
import java.util.Date;

/**
 * Store all informations about Batch and Jobs
 */
public final class Batch {
	//private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(Batch.class.getName());
	public static final String DEFAULT_BATCH_NAME = "NO NAME";
	
	private static final Batch INSTANCE = new Batch(); //NOSONAR
	private volatile String name;
	private volatile String id;
	private volatile Date startDate;
	private volatile Date endDate;
	private volatile BatchStatus status = BatchStatus.NONE;
	private volatile int successJob = 0;
	private volatile int failedJob = 0;
	private volatile int totalJOb = 0;
	
	private Batch() {
	}

	public static Batch getInstance() {
		return INSTANCE;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		if (name != null && !name.isEmpty()) { //Test in name is not null and not empty
			this.name = name;
		} else {
			this.name = Batch.DEFAULT_BATCH_NAME;
		}
		this.setId(Utils.buildUUID()); //Set the Batch Id when name is set
	}

	public String getId() {
		return id;
	}
	
	private void setId(final String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
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

	public int getSuccessJob() {
		return successJob;
	}

	public synchronized void incrementSuccessJob() { //Synchronized because Add operator is not thread safe
		++(this.successJob);
	}

	public int getFailedJob() {
		return failedJob;
	}

	public synchronized void incrementFailedJob() { //Synchronized because Add operator is not thread safe
		++(this.failedJob);
	}

	public int getTotalJOb() {
		return totalJOb;
	}

	public synchronized void incrementTotalJOb() { //Synchronized because Add operator is not thread safe
		++(this.totalJOb);
	}
	
	public synchronized int incrementAndGetTotalJOb() {//Synchronized because Add operator is not thread safe
		++(this.totalJOb);
		return this.totalJOb;
	}

	/**
	 * Batch status enumeration : NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED.
	 */
	public static enum BatchStatus {
		NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED;
	}

	protected void onTerminated() {
		if (this.failedJob == 0 && this.successJob >= 1) { //Batch completed success full (at least one job has started)
			this.setStatus(BatchStatus.COMPLETED);
		} else if (this.failedJob > 0 && this.successJob >= 1) { //Batch completed (at least one job has started) but there are job failed
			this.setStatus(BatchStatus.COMPLETED_WITH_ERROR);
		} else { //Means that all jobs failed, Batch not complete or unknown problem
			this.setStatus(BatchStatus.FAILED);
		}
	}
}
