package org.christiankakesa.applications.java.shelltaskpool;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JobExecutionTest {

    private final String cmd = "sudo aptitude full-upgrade";
    private final JobExecution jobExecution = new JobExecution(cmd);

    @Test
    public void testCommandLine() {
        assertNotNull(jobExecution.getCommandLine());
        assertTrue(0 <= jobExecution.getCommandLine().length());
        assertTrue(cmd.equals(jobExecution.getCommandLine()));
    }

    @Test
    public void testId() {
        assertTrue(0 < jobExecution.getId());
    }
}
