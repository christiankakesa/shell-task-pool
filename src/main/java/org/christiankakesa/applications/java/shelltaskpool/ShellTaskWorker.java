package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: christian
 * Date: 30/04/11
 * Time: 00:27
 * .
 */
public class ShellTaskWorker implements Runnable {
    private static final Log LOG = LogFactory.getLog(ShellTaskWorker.class);
    private int exitCode = -42;
    private String commandLine;

    public ShellTaskWorker(String commandLine) {
    	this.commandLine = commandLine;
    }

    public void run() {
        LOG.debug("Shell command to execute : " + this.commandLine);
        ProcessBuilder pb = new ProcessBuilder(Utils.buildCommandLineArray(this.commandLine));
        LOG.trace("\tProcess cmd => " + pb.command());
        LOG.trace("\tProcess env [" + pb.environment() + "]");
        try {
            JobStats jobStats = new JobStats(this.commandLine);
            jobStats.setStartDate(Calendar.getInstance().getTime());
            Process proc = pb.start();
            if (LOG.isDebugEnabled()) {
                writeProcessOutput(proc);
            }
            exitCode = proc.waitFor();
            jobStats.setEndDate(Calendar.getInstance().getTime());
            jobStats.setExitStatus(exitCode);
            LOG.info(jobStats.getShellUID() + " | " + jobStats.getShellCommand() + " | " + jobStats.getJobDuration() + " | " + jobStats.getExitStatus());
        } catch (IOException e) {
            LOG.error(e);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        LOG.debug("Command : " + pb.command() + " - Terminated with status code : [" + getExitCode() + "] !!!\n");
    }

    private void writeProcessOutput(final Process process) throws IOException {
        final InputStreamReader tempReader = new InputStreamReader(
                new BufferedInputStream(process.getInputStream()));
        final BufferedReader reader = new BufferedReader(tempReader);
        String line;
        System.out.flush();
        while ((line = reader.readLine()) != null) {
            System.out.println("writeProcessOutput :: " + line);
        }
        System.out.flush();
        reader.close();
    }

    public int getExitCode() {
        return exitCode;
    }
}
