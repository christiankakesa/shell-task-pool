package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Representing the job to execute
 */
public final class JobExecution {
    private static final Logger LOG = Logger.getLogger(JobExecution.class);
    private static final int MOINS_QUARANTE_DEUX = -42;

    /**
     * Job command line string
     */
    private final String commandLine;
    /**
     * Job id
     */
    private int id = 0;
    /**
     * Job start date
     */
    private Date startDate;
    /**
     * Job end date
     */
    private Date endDate;
    /**
     * Job status
     */
    private JobStatus status = JobStatus.NONE;
    /**
     * Job exit status code
     */
    private int exitCode = MOINS_QUARANTE_DEUX;
    /**
     * Job process
     */
    private Process process;

    /**
     * JobExecution constructor
     *
     * @param commandLine Command line to execute.
     */
    public JobExecution(final String commandLine) {
        this.commandLine = commandLine;
    }

    public void start() {
        this.id = Batch.getInstance().getBatchStatus().incrementAndGetTotalJOb();
        if (this.getStatus().equals(JobStatus.NONE)) { // Run the job only if job status is NONE (no state)
            this.run();
        } else {
            LOG.warn("JobId: " + this.getId() + ":" + this.getCommandLine() + " with status: "
                    + this.getStatus() + " couldn't be started");
        }
    }

    private void run() {
        this.setStartDate(Calendar.getInstance().getTime());
        ProcessBuilder processBuilder = new ProcessBuilder(
                Util.parseCommandLineToStringArray(this.commandLine));
        try {
            process = processBuilder.start();
            this.setStatus(JobStatus.RUNNING);
            if (Batch.getInstance().getLogDirectory() != null) {
                this.getProcessOutput(process, true);
            } else if (LOG.isDebugEnabled()) {
                LOG.debug(this.getProcessOutput(process));
            }
            this.setExitCode(process.waitFor());
            this.setEndDate(Calendar.getInstance().getTime());
            if (this.getExitCode() == 0) {
                this.setStatus(JobStatus.COMPLETED);
                Batch.getInstance().getBatchStatus().incrementSuccessJob();
            } else {
                this.setStatus(JobStatus.FAILED);
                Batch.getInstance().getBatchStatus().incrementFailedJob();
            }
            synchronized (JobExecution.class) { //We need synchronized here because "+" operator is not thread safe
                Logger.getLogger("STDOUT").log(Level.INFO, "batch:job|id:"
                        + Batch.getInstance().getId()
                        + "|job_id:"
                        + this.getId()
                        + "|command_line:"
                        + this.getCommandLine()
                        + "|start_date:"
                        + this.getStartDate()
                        + "|end_date:"
                        + this.getEndDate()
                        + "|duration:"
                        + Util.buildDurationFromDates(this.getStartDate(), this.getEndDate())
                        + "|status:"
                        + this.getStatus()
                        + "|exit_code:"
                        + this.getExitCode());
            }
        } catch (IOException e) {
            LOG.error(e);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
    }

    @SuppressWarnings(value = "unused")
    public void destroy() {
        if (null != this.process) { // Destroy JobExecution.process if not destroyed
            this.process.destroy();
        }
    }

    String getCommandLine() {
        return commandLine;
    }

    int getId() {
        return id;
    }

//	public void setId(final int id) {
//		this.id = id;
//	}

    Date getStartDate() {
        return new Date(startDate.getTime());
    }

    private void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    Date getEndDate() {
        return new Date(endDate.getTime());
    }

    private void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    JobStatus getStatus() {
        return status;
    }

    private void setStatus(final JobStatus running) {
        this.status = running;
    }

    int getExitCode() {
        return exitCode;
    }

    private void setExitCode(final int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Get the job process object
     *
     * @return job process object
     */
    /*public Process getProcess() {
		return process;
	}*/

    /**
     * Job status enumeration : NONE, RUNNING, FAILED, COMPLETED
     */
    public static enum JobStatus {
        NONE, RUNNING, FAILED, COMPLETED
    }

    /**
     * Return string representation of the object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final String separator = " - ";
        sb.append("JobExecution: ");
        sb.append(separator).append(this.getCommandLine());
        sb.append(separator).append(this.getId());
        sb.append(separator).append(this.getStartDate());
        sb.append(separator).append(this.getEndDate());
        sb.append(separator).append(this.getStatus());
        sb.append(separator).append(this.getExitCode());
        return sb.toString();
    }

    /**
     * Get the output of a process
     *
     * @param process The process for the output in string
     * @return String representation of the process output
     */
    private String getProcessOutput(final Process process) {
        return getProcessOutput(process, false);
    }

    /**
     * Get the output of a process.
     *
     * @param process            Process for getting logs.
     * @param isLogPrintedToFile Print process output to log file if true.
     * @return String representation of the process output.
     */
    private String getProcessOutput(final Process process, boolean isLogPrintedToFile) {
        final StringBuilder sbResult = new StringBuilder();
        sbResult.append("JobId: ").append(this.getId()).append(" - STDOUT: ");
        final StringBuilder sbLine = new StringBuilder();
        final InputStreamReader tempReader = new InputStreamReader(
                new BufferedInputStream(process.getInputStream()));
        final BufferedReader reader = new BufferedReader(tempReader);
        PrintWriter writer = null;
        if (isLogPrintedToFile) {
            final String logFile = JobExecution.buildLogFilename(this.getId(), this.getCommandLine(), Batch.getInstance().getLogDirectory());
            LOG.debug("log directory is : " + logFile);
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
            } catch (IOException e) {
                LOG.warn("Can't create a process output log file", e);
            }
        }
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (isLogPrintedToFile && writer != null) {
                    writer.println(line);
                }
                sbLine.append(line);
            }
        } catch (IOException e) {
            LOG.error(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
            try {
                reader.close();
            } catch (IOException e) {
                LOG.warn("Can't close the job process output stream", e);
            }
        }
        if (sbLine.toString().trim().length() > 0) {
            sbResult.append(sbLine);
            return sbResult.toString();
        } else {
            return "";
        }
    }

    /**
     * Build Log filename without non desired characters
     *
     * @param jobId   Job identifier
     * @param cmdLine Job Command line
     * @return Clean log filename
     */
    private static String buildLogFilename(int jobId, final String cmdLine, final String dirName) {
        final StringBuilder res = new StringBuilder();
        res.append(dirName).append(File.separator);
        res.append("batchid-").append(Batch.getInstance().getId()).append("_jobid-").append(String.valueOf(jobId)).append("_");
        final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSS");
        res.append(dateFormat.format(new Date().getTime())).append("_");
        final String regex = "[^a-zA-Z_-]";
        res.append(cmdLine.replaceAll(regex, "-"));
        res.append(".log");
        return res.toString();
    }
}
