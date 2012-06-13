package org.christiankakesa.applications.java.shelltaskpool;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Runnable class for JobExecution class.
 * No test needed.
 */
public class ShellTaskWorkerTest {
	@Test
	public void test() {
		final ShellTaskWorker stw = new ShellTaskWorker("sh dosomestuff.sh");
		assertNotNull(stw);
	}
}
