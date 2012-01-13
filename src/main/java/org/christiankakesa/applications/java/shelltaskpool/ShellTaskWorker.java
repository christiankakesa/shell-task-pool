package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.log4j.Logger;

/**
 * 
 */
public class ShellTaskWorker implements Runnable {
	private static final Logger LOG = Logger.getLogger(ShellTaskWorker.class);
	private final String commandLine;

	public ShellTaskWorker(String commandLine) {
		this.commandLine = commandLine;
	}

	public void run() {
		final JobExecution job = new JobExecution(this.commandLine);
		LOG.debug("Starting job " + job.getId() + " - Command line: "
				+ this.commandLine);
		job.start();
	}

}
