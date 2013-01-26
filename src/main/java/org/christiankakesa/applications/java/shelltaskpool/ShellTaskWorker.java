package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.log4j.Logger;

/**
 * A runnable worker for command line JobExecution.
 */
class ShellTaskWorker implements Runnable {
    private static final Logger LOG = Logger.getLogger(ShellTaskWorker.class);
    private final String commandLine;

    public ShellTaskWorker(String commandLine) {
        this.commandLine = commandLine;
    }

    public void run() {
        final JobExecution job = new JobExecution(this.commandLine);
        job.start();
        LOG.debug("Starting job command line: " + this.commandLine);
    }

}
