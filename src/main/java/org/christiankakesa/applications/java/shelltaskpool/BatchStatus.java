package org.christiankakesa.applications.java.shelltaskpool;

public class BatchStatus {
	private volatile int successJob;
	private volatile int failedJob;
	private volatile int totalJOb;
	private BatchStates state;

	public BatchStatus() {
	}
	/**
	 * Batch status enumeration : NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED.
	 */
	public static enum BatchStates {
		NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED;
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

	public BatchStates getState() {
		return state;
	}

	public void setState(BatchStates state) {
		this.state = state;
	}

	public synchronized void incrementTotalJOb() { //Synchronized because Add operator is not thread safe
		++(this.totalJOb);
	}
	
	public synchronized int incrementAndGetTotalJOb() {//Synchronized because Add operator is not thread safe
		++(this.totalJOb);
		return this.totalJOb;
	}
	
	protected void onBatchEnd() {
		final BatchStates endStatus;
		if (this.failedJob == 0 && this.successJob >= 1) { //Batch completed success full (at least one job has started)
			endStatus = BatchStates.COMPLETED;
		} else if (this.failedJob > 0 && this.successJob >= 1) { //Batch completed (at least one job has started) but there are job failed
			endStatus = BatchStates.COMPLETED_WITH_ERROR;
		} else { //Means that all jobs failed, Batch not complete or unknown problem
			endStatus = BatchStates.FAILED;
		}
		this.setState(endStatus);
	}
}
