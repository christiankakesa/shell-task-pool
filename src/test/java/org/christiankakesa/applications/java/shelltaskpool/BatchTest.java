package org.christiankakesa.applications.java.shelltaskpool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.christiankakesa.applications.java.shelltaskpool.BatchStatus.BatchStates;
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
		Batch.getInstance().getStatus().setState(BatchStates.NONE);
		assertEquals(BatchStates.NONE, Batch.getInstance().getStatus().getState());
		Batch.getInstance().getStatus().setState(BatchStates.STARTED);
		assertEquals(BatchStates.STARTED, Batch.getInstance().getStatus().getState());
		Batch.getInstance().getStatus().setState(BatchStates.RUNNING);
		assertEquals(BatchStates.RUNNING, Batch.getInstance().getStatus().getState());
		Batch.getInstance().getStatus().setState(BatchStates.FAILED);
		assertEquals(BatchStates.FAILED, Batch.getInstance().getStatus().getState());
		Batch.getInstance().getStatus().setState(BatchStates.COMPLETED_WITH_ERROR);
		assertEquals(BatchStates.COMPLETED_WITH_ERROR, Batch.getInstance().getStatus().getState());
		Batch.getInstance().getStatus().setState(BatchStates.COMPLETED);
		assertEquals(BatchStates.COMPLETED, Batch.getInstance().getStatus().getState());
	}
}
