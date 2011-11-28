package org.christiankakesa.applications.java.shelltaskpool;

/**
 * All state of a JobExecution
 *
 */
public class JobStatus {
	static final String UNKNOWN = "UNKNOWN";
	static final String RUNNING = "RUNNING";
	static final String COMPLETED = "COMPLETED";
	static final String FAILED = "FAILED";
	static final String STOPPED = "STOPPED";
	
	private JobStatus() {
	}
}
