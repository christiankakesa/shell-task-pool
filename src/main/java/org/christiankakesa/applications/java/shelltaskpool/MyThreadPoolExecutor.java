package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 */
public class MyThreadPoolExecutor extends ThreadPoolExecutor {
	private static final Logger LOG = Logger.getLogger(MyThreadPoolExecutor.class);

	public MyThreadPoolExecutor(int poolSize) {
		super(poolSize, poolSize, 0L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
		this.myInit();
	}

	public MyThreadPoolExecutor(int poolSize, int maxPoolSize) {
		super(poolSize, maxPoolSize, 0L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
		this.myInit();
	}

	public MyThreadPoolExecutor(int poolSize, int maxPoolSize,
			long keepAliveTime) {
		super(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
		this.myInit();
	}

	private void myInit() {
		Batch.getInstance().getBatchStatus().setStatus(Batch.Status.STARTED);
		Batch.getInstance().setStartDate(Calendar.getInstance().getTime());
		LOG.info("[BATCH_START] BatchId: " + Batch.getInstance().getId()
				+ " | BatchName: " + Batch.getInstance().getName()
				+ " | BatchStartDate: " + Batch.getInstance().getStartDate()
				+ " | BatchStatus: " + Batch.getInstance().getBatchStatus().getStatus());
	}

	public void addTask(Runnable r) {
		super.execute(r);
		LOG.debug("Task " + r.toString() + " added");
	}

	@Override
	public void terminated() {
		Batch.getInstance().setEndDate(Calendar.getInstance().getTime());
		Batch.getInstance().getBatchStatus().doEndStatus();
		LOG.info("[BATCH_END] - BatchId: "
				+ Batch.getInstance().getId()
				+ " | BatchName: "
				+ Batch.getInstance().getName()
				+ " | BatchStartDate: "
				+ Batch.getInstance().getStartDate()
				+ " | BatchEndDate: "
				+ Batch.getInstance().getEndDate()
				+ " | BatchDuration: "
				+ Util.buildDurationFromDates(Batch.getInstance().getStartDate(), Batch.getInstance().getEndDate()) + " | BatchStatus: "
				+ Batch.getInstance().getBatchStatus().getStatus());
		super.terminated();
	}

	
//	@Override
//	public void shutdown() {
//		try {
//			LOG.debug("ThreadPool shutdown. All pool is working !!!");
//		} finally {
//			super.shutdown();
//		} 
//	}
 
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		if (Batch.getInstance().getBatchStatus().getStatus() != Batch.Status.RUNNING) { //Ensure that Batch state is set to Batch.RUNNING 
			Batch.getInstance().getBatchStatus().setStatus(Batch.Status.RUNNING);
		}
	}

//	@Override
//	protected void afterExecute(Runnable r, Throwable t) {
//		try {
//			//TODO: What to do here
//		} finally {
//			super.afterExecute(r, t);
//		}
//	 }
}
