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
		Batch.getInstance().setBatchName(null);
		assertNull(Batch.getInstance().getBatchName());
		assertNull(Batch.getInstance().getBatchId());
	}

	@Test
	public void testSetBatchNameEquals() {
		Batch.getInstance().setBatchName("Alimentation des omes");
		assertEquals("Alimentation des omes", Batch.getInstance().getBatchName());
		assertNotNull(Batch.getInstance().getBatchId());
	}
	
	@Test
	public void testSetBatchStartDate() {
		Batch.getInstance().setBatchStartDate(new Date());
		assertNotNull(Batch.getInstance().getBatchStartDate());
		assertEquals(Batch.getInstance().getBatchStatus(), BatchStatus.STARTED);
	}
}
