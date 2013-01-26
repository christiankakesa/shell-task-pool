package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.commons.lang.StringUtils;

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
    private String name = "NO BATCH NAME";

    /**
     * The batch id.
     */
    private final String id = Util.buildUUID();

    /**
     * Start date of the batch.
     */
    private Date startDate;

    /**
     * End date of the batch.
     */
    private Date endDate;

    /**
     * Status of the batch.
     */
    private final BatchStatus batchStatus = new BatchStatus();

    /**
     * Jobs file
     */
    private String jobsFile;

    /**
     * Log directory
     */
    private String logDirectory;

    /**
     * Batch parameters
     */
    private String[] parameters = new String[]{};

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
     * @param name Batch name
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
     * @param startDate Date of batch start
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
     * @param endDate Date of batch end
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
        NONE, STARTED, RUNNING, FAILED, COMPLETED_WITH_ERROR, COMPLETED
    }

    public static class BatchStatus {
        /**
         * Number of job success.
         */
        private volatile int successJob;
        /**
         * Synchronized monitor for successJob.
         */
        private final Object successJobMonitor = new Object();
        /**
         * Number of job failed.
         */
        private volatile int failedJob;
        /**
         * Synchronized monitor for failedJob.
         */
        private final Object failedJobMonitor = new Object();
        /**
         * Number of total job.
         */
        private volatile int totalJob;
        /**
         * Synchronized monitor for totalJob.
         */
        private final Object totalJobMonitor = new Object();
        /**
         * Status of the batch.
         */
        private volatile Status status = Status.NONE;

        /**
         * BatchStatus constructor.
         */
        BatchStatus() {
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
            synchronized (successJobMonitor) {// Synchronized because Add operator is not thread safe
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
            synchronized (failedJobMonitor) {// Synchronized because Add operator is not thread safe
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
         * @param status Status for BatchStatus instance.
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
            synchronized (totalJobMonitor) {// Synchronized because Add operator is not thread safe
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
         * <p/>
         * Try to determine end batch status inspecting <code>failedJob</code>
         * and <code>successJob</code>.
         */
        void doEndStatus() {
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
     * @return Batch unique instance.
     */
    @SuppressWarnings("SameReturnValue")
    public static Batch getInstance() {
        return Batch.INSTANCE;
    }

    /**
     * Get the batch jobs file.
     *
     * @return Jobs file.
     */
    public String getJobsFile() {
        return jobsFile;
    }

    /**
     * Set the batch jobs file.
     *
     * @param jobsFile File for jobs.
     */
    public void setJobsFile(final String jobsFile) {
        this.jobsFile = jobsFile;
    }

    /**
     * Get the batch log directory.
     *
     * @return Log directory.
     */
    public String getLogDirectory() {
        return logDirectory;
    }

    /**
     * Set the batch log directory.
     *
     * @param logDirectory Directory log storage.
     */
    public void setLogDirectory(final String logDirectory) {
        this.logDirectory = logDirectory;
    }

    /**
     * Get the batch parameters.
     *
     * @return Parameters array of String.
     */
    String[] getParameters() {
        return this.parameters;
    }

    /**
     * Set the batch parameters.
     *
     * @param params String array of batch parameters.
     */
    public void setParameters(final String[] params) {
        this.parameters = new String[params.length];
        System.arraycopy(params, 0, this.parameters, 0, params.length);
    }

    /**
     * String representation of batch parameters.
     *
     * @return String of batch parameters.
     */
    public String getStringParameters() {
        String res = "";
        if (null != this.parameters) {
            res = StringUtils.join(this.parameters, " ");
        }
        return res;
    }
}
