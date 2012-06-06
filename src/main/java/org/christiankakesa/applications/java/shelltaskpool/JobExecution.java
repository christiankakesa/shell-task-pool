package org.christiankakesa.applications.java.shelltaskpool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Representing the job to execute
 * 
 */
public class JobExecution {
	private static final Logger LOG = Logger.getLogger(JobExecution.class);
	private static final int MOINS_QUARANTE_DEUX = -42;

	/**
	 * Job command line string
	 */
	private String commandLine;
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
	 * @param commandLine
	 */
	public JobExecution(final String commandLine) {
		this.commandLine = commandLine;
	}

	public void start() {
		this.setId(Batch.getInstance().getBatchStatus().incrementAndGetTotalJOb());
		if (this.getStatus().equals(JobStatus.NONE)) { // Run the job only if job status is NONE (no state)
			this.run();
		} else {
			LOG.warn("JobId: " + this.getId() + " with status: "
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
			this.setExitCode(process.waitFor());
			this.setEndDate(Calendar.getInstance().getTime());
			if (this.getExitCode() == 0) {
				this.setStatus(JobStatus.COMPLETED);
				Batch.getInstance().getBatchStatus().incrementSuccessJob();
			} else {
				this.setStatus(JobStatus.FAILED);
				Batch.getInstance().getBatchStatus().incrementFailedJob();
			}
			synchronized (this.getClass()) {
				LOG.info("[JOB_EXECUTION] BatchId: "
						+ Batch.getInstance().getId()
						+ " | BatchName: "
						+ Batch.getInstance().getName()
						+ " | JobId: "
						+ this.getId()
						+ " | JobCommandLine: "
						+ this.getCommandLine()
						+ " | JobStartDate: "
						+ this.getStartDate()
						+ " | JobEndDate: "
						+ this.getEndDate()
						+ " | JobDuration: "
						+ Util.buildDurationFromDates(this.getEndDate(),
								this.getStartDate()) + " | JobStatus: "
						+ this.getStatus() + " | JobExitCode: "
						+ this.getExitCode());
				if (LOG.isDebugEnabled()) {
					LOG.debug(getProcessOutput(process));
				}
			}
		} catch (IOException e) {
			LOG.error(e);
		} catch (InterruptedException e) {
			LOG.error(e);
		}
	}

	public void destroy() {
		if (this.process != null) { // Destroy JobExecution.process if not destroyed
			this.process.destroy();
		}
	}

	public String getCommandLine() {
		return commandLine;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public Date getStartDate() {
		return new Date(startDate.getTime());
	}

	private void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return new Date(endDate.getTime());
	}

	private void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public JobStatus getStatus() {
		return status;
	}

	private void setStatus(final JobStatus running) {
		this.status = running;
	}

	public int getExitCode() {
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
	public Process getProcess() {
		return process;
	}

	/**
	 * Job status enumeration : NONE, RUNNING, FAILED, COMPLETED
	 */
	public static enum JobStatus {
		NONE, RUNNING, FAILED, COMPLETED;
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
	 * @param process
	 * @return String representation of the process output
	 */
	private String getProcessOutput(final Process process) {
		final StringBuilder sbResult = new StringBuilder();
		sbResult.append("JobId: ").append(this.getId()).append(" - STDOUT: ");
		final StringBuilder sbLine = new StringBuilder();
		final InputStreamReader tempReader = new InputStreamReader(
				new BufferedInputStream(process.getInputStream()));
		final BufferedReader reader = new BufferedReader(tempReader);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sbLine.append(line);
			}
		} catch (IOException e) {
			LOG.error(e);
		} finally {
			line = null;
			try {
				reader.close();
			} catch (IOException e) {
				LOG.warn("Can't close the proccess output stream : " + e);
			}
		}
		if (sbLine.toString().trim().length() > 0) {
			sbResult.append(sbLine);
			return sbResult.toString();
		} else {
			return "";
		}
	}
}
