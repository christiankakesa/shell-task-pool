package org.christiankakesa.applications.java.shelltaskpool;

import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.christiankakesa.applications.java.shelltaskpool.Batch.BatchStatus;
import org.junit.Test;

public class BatchTest {
	
	@Test
	public void testSetName() {
		Batch.getInstance().setName(null);
		assertEquals(Batch.getInstance().getName(), Batch.DEFAULT_BATCH_NAME);
		assertNotNull(Batch.getInstance().getId());
		final String batchName = "Alimentation des omes";
		Batch.getInstance().setName(batchName);
		assertEquals(batchName, Batch.getInstance().getName());
		assertNotNull(Batch.getInstance().getId());
	}

	@Test
	public void testSetStartDate() {
		Batch.getInstance().setStartDate(new Date());
		assertNotNull(Batch.getInstance().getStartDate());
		assertTrue(Batch.getInstance().getStartDate() instanceof Date);
	}
	
	@Test
	public void testSetStatus() {
		Batch.getInstance().setStatus(BatchStatus.NONE);
		assertEquals(BatchStatus.NONE, Batch.getInstance().getStatus());
		Batch.getInstance().setStatus(BatchStatus.STARTED);
		assertEquals(BatchStatus.STARTED, Batch.getInstance().getStatus());
		Batch.getInstance().setStatus(BatchStatus.RUNNING);
		assertEquals(BatchStatus.RUNNING, Batch.getInstance().getStatus());
		Batch.getInstance().setStatus(BatchStatus.FAILED);
		assertEquals(BatchStatus.FAILED, Batch.getInstance().getStatus());
		Batch.getInstance().setStatus(BatchStatus.COMPLETED_WITH_ERROR);
		assertEquals(BatchStatus.COMPLETED_WITH_ERROR, Batch.getInstance().getStatus());
		Batch.getInstance().setStatus(BatchStatus.COMPLETED);
		assertEquals(BatchStatus.COMPLETED, Batch.getInstance().getStatus());
	}
}
