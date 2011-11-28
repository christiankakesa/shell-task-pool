package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 */
public class ShellTaskWorker implements Runnable {
	private static final Log LOG = LogFactory.getLog(ShellTaskWorker.class);
	private final String commandLine;

	public ShellTaskWorker(String commandLine) {
		this.commandLine = commandLine;
	}

	public void run() {
		final JobExecution job = new JobExecution(this.commandLine);
		LOG.debug("Starting job " + job.getId() + " from batch "
				+ Batch.getInstance().getBatchId() + " - Command line: "
				+ this.commandLine);
		job.start();
	}

}
