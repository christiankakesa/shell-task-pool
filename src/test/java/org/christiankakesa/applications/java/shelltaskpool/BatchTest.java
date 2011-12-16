package org.christiankakesa.applications.java.shelltaskpool;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.Date;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import org.christiankakesa.applications.java.shelltaskpool.Batch.BatchStatus;
import org.junit.Test;

public class BatchTest {
	
	@Test
	public void testSetBatchNameNull() {
		Batch.getInstance().setName(null);
		assertNull(Batch.getInstance().getName());
		assertNull(Batch.getInstance().getId());
	}

	@Test
	public void testSetBatchNameEquals() {
		Batch.getInstance().setName("Alimentation des omes");
		assertEquals("Alimentation des omes", Batch.getInstance().getName());
		assertNotNull(Batch.getInstance().getId());
	}
	
	@Test
	public void testSetBatchStartDate() {
		Batch.getInstance().setStartDate(new Date());
		assertNotNull(Batch.getInstance().getStartDate());
		assertEquals(Batch.getInstance().getStatus(), BatchStatus.STARTED);
	}
	
	@Test
	public void testSetBatchStatus() {
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
