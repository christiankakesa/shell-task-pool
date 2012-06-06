package org.christiankakesa.applications.java.shelltaskpool;

import java.util.Date;

/**
 * Store all informations about Batch.
 * 
 * @author Christian Kakesa (christian.kakesa@gmail.com)
 */
public enum Batch {
	/**
	 * Unique instance of batch statically construct.
	 */
	INSTANCE;

	/**
	 * The batch name.
	 */
	private volatile String name = "NO BATCH NAME";

	/**
	 * The batch id.
	 */
	private volatile String id = Util.buildUUID();

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
	private volatile BatchStatus batchStatus = new BatchStatus();

	/**
	 * Get the name of the batch.
	 * 
	 * @return Batch name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the batch name. If empty or null, Batch.DEFAULT_BATCH_NAME is set.
	 * 
	 * @param Batch
	 *            name
	 */
	public void setName(final String name) {
		if (name != null && (name.length() > 0)) {
			this.name = name;
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
	 * Get the date of batch start.
	 * 
	 * @return Date of batch start
	 */
	public Date getStartDate() {
		return new Date(this.startDate.getTime());
	}

	/**
	 * Set the date of batch start.
	 * 
	 * @param Date
	 *            of batch start
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = new Date(startDate.getTime());
	}

	/**
	 * Get the date of batch end.
	 * 
	 * @return Date of batch end
	 */
	public Date getEndDate() {
		return new Date(this.endDate.getTime());
	}

	/**
	 * Set the date of batch end.
	 * 
	 * @param Date
	 *            of batch end
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = new Date(endDate.getTime());
	}

	/**
	 * Get the batch status.
	 * 
	 * @return Batch status
	 */
	public BatchStatus getBatchStatus() {
		return batchStatus;
	}

	/**
	 * Batch status enumeration : NONE, STARTED, RUNNING, FAILED,
	 * COMPLETED_WITH_ERROR, COMPLETED.
	 */
	public static enum Status {
		NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED;
	}

	public static class BatchStatus {
		/**
		 * Number of job success.
		 */
		private volatile int successJob;
		/**
		 * Synchronized monitor for successJob.
		 */
		private Object successJobMonitor = new Object();
		/**
		 * Number of job failed.
		 */
		private volatile int failedJob;
		/**
		 * Synchronized monitor for failedJob.
		 */
		private Object failedJobMonitor = new Object();
		/**
		 * Number of total job.
		 */
		private volatile int totalJob;
		/**
		 * Synchronized monitor for totalJob.
		 */
		private Object totalJobMonitor = new Object();
		/**
		 * Status of the batch.
		 */
		private volatile Status status;

		/**
		 * BatchStatus constructor.
		 */
		protected BatchStatus() {
		}

		/**
		 * Return the number of successful job.
		 * 
		 * @return Number of successful job.
		 */
		public int getSuccessJob() {
			return successJob;// No synchronization needed, successJob is volatile, not cached.
		}

		/**
		 * Increment <code>successJob</code> by 1.
		 */
		public void incrementSuccessJob() {
			synchronized(successJobMonitor) {// Synchronized because Add operator is not thread safe
				++(this.successJob);
			}
		}

		/**
		 * Return the number of failing job.
		 * 
		 * @return Number of failed job.
		 */
		public int getFailedJob() {
			return failedJob;// No synchronization needed, failedJob is volatile, not cached.
		}

		/**
		 * Increment <code>failedJob</code> by 1.
		 */
		public void incrementFailedJob() {
			synchronized(failedJobMonitor) {// Synchronized because Add operator is not thread safe
				++(this.failedJob);
			}
		}

		/**
		 * Return the status of BatchStatus.
		 * 
		 * @return Status of BatchStatus
		 */
		public Status getStatus() {
			return status;
		}

		/**
		 * Set the status of BatchStatus.
		 * 
		 * @param status
		 */
		public void setStatus(final Status status) {
			this.status = status;
		}

		/**
		 * Return the number of total job.
		 * 
		 * @return Number of total job.
		 */
		public int getTotalJob() {
			return totalJob;// No synchronization needed, totalJob is volatile, not cached.
		}

		/**
		 * Increment <code>totalJob</code> by 1.
		 */
		public void incrementTotalJob() {
			synchronized(totalJobMonitor) {// Synchronized because Add operator is not thread safe
				++(this.totalJob);
			}
		}

		/**
		 * Increment and return the number of total jobs.
		 * 
		 * @return total jobs number.
		 */
		public int incrementAndGetTotalJOb() {
			this.incrementTotalJob();
			return this.totalJob;
		}

		/**
		 * Determine the end of batch status.
		 * 
		 * Try to determine end batch status inspecting <code>failedJob</code>
		 * and <code>successJob</code>.
		 */
		protected void doEndStatus() {
			final Status endStatus;
			if (this.failedJob == 0 && this.successJob >= 1) {
				// Batch completed success full (at least one job has started)
				endStatus = Status.COMPLETED;
			} else if (this.failedJob > 0 && this.successJob >= 1) {
				// Batch completed (at least one job finished successfully) but
				// there are failed jobs
				endStatus = Status.COMPLETED_WITH_ERROR;
			} else {
				// Means that all jobs failed, Batch not complete or unknown
				// problem
				endStatus = Status.FAILED;
			}
			this.setStatus(endStatus);
		}
	}

	/**
	 * Get the unique batch instance.
	 * 
	 * @return Batch unique instance
	 */
	public static Batch getInstance() {
		return Batch.INSTANCE;
	}
}
