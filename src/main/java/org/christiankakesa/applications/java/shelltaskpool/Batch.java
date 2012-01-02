package org.christiankakesa.applications.java.shelltaskpool;

import java.util.Date;

import javax.annotation.PostConstruct;

/**
 * Store all informations about Batch.
 * 
 * @author Christian Kakesa (christian.kakesa@gmail.com)
 */
public final class Batch {
	/**
	 * Default Batch name when not given.
	 */
	public static final String DEFAULT_BATCH_NAME = "NO BATCH NAME";
	
	/**
	 * Unique instance of batch statically construct.
	 */
	private static final Batch INSTANCE = new Batch();
	
	/**
	 * The batch name.
	 */
	private volatile String name;
	
	/**
	 * The batch id.
	 */
	private volatile String id;
	
	/**
	 * Start date of the batch.
	 */
	private volatile Date startDate;
	
	/**
	 * End date of the batch.
	 */
	private volatile Date endDate;
	
	/**
	 * Status of the batch.
	 */
	private volatile BatchStatus status = new BatchStatus();
	
	/**
	 * The Batch private constructor.
	 */
	private Batch() {
	}

	/**
	 * Get the Batch singleton instance.
	 * 
	 * @return unique instance of Batch
	 */
	public static Batch getInstance() {
		return INSTANCE;
	}

	/**
	 * Get the name of the batch.
	 * 
	 * @return Batch name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the batch name.
	 * If empty or null, Batch.DEFAULT_BATCH_NAME is set.
	 * 
	 * @param Batch name
	 */
	@PostConstruct
	public void setName(final String name) {
		if (name != null && !name.isEmpty()) {
			this.name = name;
		} else {
			this.name = Batch.DEFAULT_BATCH_NAME;
		}
	}

	/**
	 * Get the batch id.
	 * 
	 * @return Unique string representation of bath.
	 * 
	 * @see Util.buildUUID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the batch id.
	 * 
	 * @param Batch id
	 */
	@PostConstruct
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Get the date of batch start.
	 * 
	 * @return Date of batch start
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Set the date of batch start.
	 * 
	 * @param Date of batch start
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the date of batch end.
	 * 
	 * @return Date of batch end
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Set the date of batch end.
	 * 
	 * @param Date of batch end
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get the batch status.
	 * 
	 * @return Batch status
	 */
	BatchStatus getStatus() {
		return status;
	}
	
	public static enum Status {
		NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED;
	}
	
	public static class BatchStatus {
		private volatile int successJob;
		private volatile int failedJob;
		private volatile int totalJOb;
		private Status status;

		public BatchStatus() {
		}
		/**
		 * Batch status enumeration : NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED.
		 */

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

		public Status getStatus() {
			return status;
		}

		public void setStatus(final Status status) {
			this.status = status;
		}

		public synchronized void incrementTotalJOb() { //Synchronized because Add operator is not thread safe
			++(this.totalJOb);
		}
		
		public synchronized int incrementAndGetTotalJOb() {//Synchronized because Add operator is not thread safe
			++(this.totalJOb);
			return this.totalJOb;
		}
		
		protected void doEndStatus() {
			final Status endStatus;
			if (this.failedJob == 0 && this.successJob >= 1) { //Batch completed success full (at least one job has started)
				endStatus = Status.COMPLETED;
			} else if (this.failedJob > 0 && this.successJob >= 1) { //Batch completed (at least one job has started) but there are job failed
				endStatus = Status.COMPLETED_WITH_ERROR;
			} else { //Means that all jobs failed, Batch not complete or unknown problem
				endStatus = Status.FAILED;
			}
			this.setStatus(endStatus);
		}
	}
}
