package org.christiankakesa.applications.java.shelltaskpool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

public class BatchTest {
	static {
		//Set the default batch name : Batch.DEFAULT_BATCH_NAME
		Batch.getInstance().setName(null);
		Batch.getInstance().setId(Util.buildUUID());
	}
	
	@Test
	public void testGetInstance() {
		assertNotNull(Batch.getInstance());
		assertTrue(Batch.getInstance() instanceof Batch);
	}
	
	@Test
	public void testName() {
		//Test the default batch name : Batch.DEFAULT_BATCH_NAME
		assertEquals(Batch.DEFAULT_BATCH_NAME, Batch.getInstance().getName());
		
		final String batchName = "Shell Task Pool";
		Batch.getInstance().setName(batchName);
		assertEquals(batchName, Batch.getInstance().getName());
		assertNotNull(Batch.getInstance().getName());
		assertTrue(Batch.getInstance().getName().length() > 0);
		assertTrue(Batch.getInstance().getName() instanceof String);
	}
	
	@Test
	public void testId() {
		assertNotNull(Batch.getInstance().getId());
	}
	
	@Test
	public void testStartDate() {
		final Date startDate = new Date();
		Batch.getInstance().setStartDate(startDate);
		assertTrue(Batch.getInstance().getStartDate() instanceof Date);
		assertNotNull(Batch.getInstance().getStartDate());
		assertEquals(startDate, Batch.getInstance().getStartDate());
	}
	
	@Test
	public void testEndDate() {
		final Date endDate = new Date();
		Batch.getInstance().setEndDate(endDate);
		assertTrue(Batch.getInstance().getEndDate() instanceof Date);
		assertNotNull(Batch.getInstance().getEndDate());
		assertEquals(endDate, Batch.getInstance().getEndDate());
	}
	
	@Test
	public void testStatus() {
		Batch.getInstance().getStatus().setStatus(Batch.Status.NONE);
		assertEquals(Batch.Status.NONE, Batch.getInstance().getStatus().getStatus());
		Batch.getInstance().getStatus().setStatus(Batch.Status.STARTED);
		assertEquals(Batch.Status.STARTED, Batch.getInstance().getStatus().getStatus());
		Batch.getInstance().getStatus().setStatus(Batch.Status.RUNNING);
		assertEquals(Batch.Status.RUNNING, Batch.getInstance().getStatus().getStatus());
		Batch.getInstance().getStatus().setStatus(Batch.Status.FAILED);
		assertEquals(Batch.Status.FAILED, Batch.getInstance().getStatus().getStatus());
		Batch.getInstance().getStatus().setStatus(Batch.Status.COMPLETED_WITH_ERROR);
		assertEquals(Batch.Status.COMPLETED_WITH_ERROR, Batch.getInstance().getStatus().getStatus());
		Batch.getInstance().getStatus().setStatus(Batch.Status.COMPLETED);
		assertEquals(Batch.Status.COMPLETED, Batch.getInstance().getStatus().getStatus());
	}
}
