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
	private volatile long id = 0;
	private volatile Date startDate;
	private volatile Date endDate;
	private volatile String status = JobStatus.UNKNOWN;
	private volatile int exitCode = -42;
	private ProcessBuilder processBuilder;
	private Process process;
	/**
	 * 
	 * @param commandLine
	 */
	public JobExecution(final String commandLine) {
		this.commandLine = commandLine;
		this.setId(Batch.getInstance().addJobExecution(this));
	}
	
	public synchronized void start() {
		processBuilder = new ProcessBuilder(
				Utils.buildCommandLineArray(this.commandLine));
		this.setStatus(JobStatus.RUNNING);
		try {
			this.setStartDate(Calendar.getInstance().getTime());
			process = processBuilder.start();
			LOG.debug(getProcessOutput(process)); /** Log the output of the process */
			this.setExitCode(process.waitFor());
			this.setEndDate(Calendar.getInstance().getTime());
		} catch (IOException e) {
			LOG.error(e);
		} catch (InterruptedException e) {
			LOG.error(e);
		}
		if (this.exitCode == 0) {
			this.setStatus(JobStatus.COMPLETED);
		} else {
			this.setStatus(JobStatus.FAILED);
		}
		LOG.debug("Command: " + processBuilder.command() + " - Exit code: ["
				+ this.getExitCode() + "] !!!\n");
	}
	
	public synchronized void destroy() {
		if (process != null)
			process.destroy();
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

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Get the string representation of job duration. - format : "00:00:00" <==>
	 * "hours:minutes:seconds"
	 * 
	 * @return
	 */
	public String getJobDuration() {
		if (endDate != null && startDate != null) {
			final long tsTime = (endDate.getTime() - startDate.getTime()) / 1000;
			return String.format("%02d:%02d:%02d", tsTime / 3600,
					(tsTime % 3600) / 60, (tsTime % 60));
		}
		LOG.debug("Can't determine job duration : endDate = " + endDate
				+ " - startDate = " + startDate);
		return "00:00:00";
	}

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(final int exitCode) {
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
}
