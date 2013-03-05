package org.christiankakesa.applications.java.shelltaskpool;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class BatchTest {
    static {
        //Set the default batch name to the default name
        Batch.getInstance().setName(null);
    }

    @Test
    public void testGetInstance() {
        assertNotNull(Batch.getInstance());
    }

    @Test
    public void testName() {
        //Test the default batch name is always set
        assertTrue(Batch.getInstance().getName().length() > 0);

        final String batchName = "Shell Task Pool";
        Batch.getInstance().setName(batchName);
        assertNotNull(Batch.getInstance().getName());
        assertTrue(Batch.getInstance().getName().length() > 0);
        assertEquals(batchName, Batch.getInstance().getName());
    }

    @Test
    public void testId() {
        assertNotNull(Batch.getInstance().getId());
    }

    @Test
    public void testLogDirectory() {
        final String SET_LOG_DIRECTORY = "my_log_dir";
        Batch.getInstance().setLogDirectory(SET_LOG_DIRECTORY);
        final String GET_LOG_DIRECTORY = Batch.getInstance().getLogDirectory();
        assertEquals(SET_LOG_DIRECTORY, GET_LOG_DIRECTORY);
    }

    @Test
    public void testParameters() {
        final String[] MY_PARAMS = new String[]{"-p", "titi", "-l", "my_log_dir"};
        Batch.getInstance().setParameters(MY_PARAMS);
        assertArrayEquals(MY_PARAMS, Batch.getInstance().getParameters());
        final String STRING_PARAMS = Batch.getInstance().getStringParameters();
        assertTrue(STRING_PARAMS.length() == "-p titi -l my_log_dir".length());
    }

    @Test
    public void testNumberOfWorkers() {
        assertTrue(0 <= Batch.getInstance().getNumberOfWorkers());
    }

    @Test
    public void testStartDate() {
        final Date startDate = new Date();
        Batch.getInstance().setStartDate(startDate);
        assertTrue(null != Batch.getInstance().getStartDate());
        assertNotNull(Batch.getInstance().getStartDate());
        assertEquals(startDate, Batch.getInstance().getStartDate());
    }

    @Test
    public void testEndDate() {
        final Date endDate = new Date();
        Batch.getInstance().setEndDate(endDate);
        assertTrue(null != Batch.getInstance().getEndDate());
        assertNotNull(Batch.getInstance().getEndDate());
        assertEquals(endDate, Batch.getInstance().getEndDate());
    }

    @Test
    public void testStatus() {
        Batch.getInstance().getBatchStatus().setStatus(Batch.Status.NONE);
        assertEquals(Batch.Status.NONE, Batch.getInstance().getBatchStatus().getStatus());
        Batch.getInstance().getBatchStatus().setStatus(Batch.Status.STARTED);
        assertEquals(Batch.Status.STARTED, Batch.getInstance().getBatchStatus().getStatus());
        Batch.getInstance().getBatchStatus().setStatus(Batch.Status.RUNNING);
        assertEquals(Batch.Status.RUNNING, Batch.getInstance().getBatchStatus().getStatus());
        Batch.getInstance().getBatchStatus().setStatus(Batch.Status.FAILED);
        assertEquals(Batch.Status.FAILED, Batch.getInstance().getBatchStatus().getStatus());
        Batch.getInstance().getBatchStatus().setStatus(Batch.Status.COMPLETED_WITH_ERROR);
        assertEquals(Batch.Status.COMPLETED_WITH_ERROR, Batch.getInstance().getBatchStatus().getStatus());
        Batch.getInstance().getBatchStatus().setStatus(Batch.Status.COMPLETED);
        assertEquals(Batch.Status.COMPLETED, Batch.getInstance().getBatchStatus().getStatus());
    }

    @Test
    public void testBatchStatus() {
        int successCounter = 0;
        int failedCounter = 0;
        int totalCounter = 0;

        Batch.BatchStatus b = new Batch.BatchStatus();
        assertNotNull(b);
        assertEquals(b.getSuccessJob(), successCounter);
        assertEquals(b.getFailedJob(), failedCounter);
        assertEquals(b.getTotalJob(), totalCounter);
        b.incrementSuccessJob();
        b.incrementTotalJob();
        assertEquals(b.getSuccessJob(), ++successCounter);
        b.incrementFailedJob();
        b.incrementTotalJob();
        assertEquals(b.getFailedJob(), ++failedCounter);
        totalCounter += 2;
        assertEquals(b.getTotalJob(), totalCounter);
        assertEquals(b.incrementAndGetTotalJOb(), ++totalCounter);
        b.doEndStatus();
        assertNotNull(b.getStatus());

        Batch.BatchStatus c = new Batch.BatchStatus();
        c.doEndStatus();
        assertEquals(c.getStatus(), Batch.Status.FAILED);
        c.incrementSuccessJob();
        c.incrementAndGetTotalJOb();
        c.doEndStatus();
        assertEquals(c.getStatus(), Batch.Status.COMPLETED);
        c.incrementFailedJob();
        c.incrementAndGetTotalJOb();
        c.incrementSuccessJob();
        c.incrementAndGetTotalJOb();
        c.doEndStatus();
        assertEquals(c.getStatus(), Batch.Status.COMPLETED_WITH_ERROR);
        assertEquals(c.getTotalJob(), 3);
    }
}
