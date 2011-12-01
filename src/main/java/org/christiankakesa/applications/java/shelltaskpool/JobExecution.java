package org.christiankakesa.applications.java.shelltaskpool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.LogFactory;

/**
 * Representing the job to execute
 * 
 */
public class JobExecution {
	private static final org.apache.commons.logging.Log LOG = LogFactory
			.getLog(JobExecution.class);

	/**
	 * The entire command to execute in this job
	 */
	private String commandLine;
	private long id = 0;
	private Date startDate;
	private Date endDate;
	private JobStatus status = JobStatus.NONE;
	private int exitCode = -42;
	private ProcessBuilder processBuilder;
	private Process process;

	/**
	 * 
	 * @param commandLine
	 */
	public JobExecution(final String commandLine) {
		this.commandLine = commandLine;
		/** Add the job to the Batch.jobExecutionList and set a jobId */
		Batch.getInstance().addJobExecution(this);
	}

	public void start() {
		processBuilder = new ProcessBuilder(
				Utils.parseCommandLineToStringArray(this.commandLine));
		/** Start a job only when job status is NONE */
		if (this.getStatus() != JobStatus.NONE) {
			LOG.warn("JobId: " + this.getId() + " with status: "
					+ this.getStatus() + " couldn't be started");
			return;
		}
		this.setStartDate(Calendar.getInstance().getTime());
		try {
			process = processBuilder.start();
			this.setStatus(JobStatus.RUNNING);
			LOG.debug(getProcessOutput(process));
			/** Log the output of the process */
			this.setExitCode(process.waitFor());
			this.setEndDate(Calendar.getInstance().getTime());
			if (this.exitCode == 0) {
				this.setStatus(JobStatus.COMPLETED);
			} else {
				this.setStatus(JobStatus.FAILED);
			}
			LOG.info("Job id: " + this.getId() + " | Job command line:"
					+ this.getCommandLine() + " | Job duration: "
					+ this.getDuration() + " | Job status: " + this.getStatus()
					+ " | Job exit code: " + this.getExitCode());
		} catch (IOException e) {
			LOG.error(e);
		} catch (InterruptedException e) {
			LOG.error(e);
		}
		if (this.getStatus() == JobStatus.COMPLETED) {
			Batch.getInstance().incrementJobSuccess();
		} else {
			Batch.getInstance().incrementJobFailed();
		}
	}

	public void destroy() {
		if (process != null) {
			synchronized (JobExecution.class) {
				process.destroy();
			}
		}
	}

	public String getCommandLine() {
		return commandLine;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		if (id > 0)
			this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	private void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	private void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public JobStatus getStatus() {
		return status;
	}

	private void setStatus(JobStatus running) {
		this.status = running;
	}

	/**
	 * Get the string representation of job duration. - format : "00:00:00" <==>
	 * "hours:minutes:seconds"
	 * 
	 * @return
	 */
	public String getDuration() {
		return Utils.buildDurationFromDates(this.getEndDate(),
				this.getStartDate());
	}

	public int getExitCode() {
		return exitCode;
	}

	private void setExitCode(final int exitCode) {
		this.exitCode = exitCode;
	}

	private String getProcessOutput(final Process process) {
		final StringBuilder sb = new StringBuilder();
		final InputStreamReader tempReader = new InputStreamReader(
				new BufferedInputStream(process.getInputStream()));
		final BufferedReader reader = new BufferedReader(tempReader);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		} catch (IOException e) {
			LOG.error(e);
		} finally {
			line = null;
		}
		return sb.toString();
	}

	public static enum JobStatus {
		NONE, RUNNING, FAILED, COMPLETED
	}
}
