package org.christiankakesa.applications.java.shelltaskpool;

import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

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
		Batch.getInstance().getStatus().setStatus(Batch.Status.STARTED);
		Batch.getInstance().setStartDate(Calendar.getInstance().getTime());
		LOG.info("[BATCH_START] BatchId: " + Batch.getInstance().getId()
				+ " | BatchName: " + Batch.getInstance().getName()
				+ " | BatchStartDate: " + Batch.getInstance().getStartDate()
				+ " | BatchStatus: " + Batch.getInstance().getStatus().getStatus());
	}

	public void addTask(Runnable r) {
		super.execute(r);
		LOG.debug("Task " + r.toString() + " added");
	}

	@Override
	public void terminated() {
		Batch.getInstance().setEndDate(Calendar.getInstance().getTime());
		Batch.getInstance().getStatus().doEndStatus();
		LOG.info("[BATCH_END] - BatchId: "
				+ Batch.getInstance().getId()
				+ " | BatchName: "
				+ Batch.getInstance().getName()
				+ " | BatchStartDate: "
				+ Batch.getInstance().getStartDate()
				+ " | BatchEndDate: "
				+ Batch.getInstance().getEndDate()
				+ " | BatchDuration: "
				+ Util.buildDurationFromDates(
						Batch.getInstance().getEndDate(), Batch.getInstance()
								.getStartDate()) + " | BatchStatus: "
				+ Batch.getInstance().getStatus().getStatus());
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
		if (Batch.getInstance().getStatus().getStatus() != Batch.Status.RUNNING) { //Ensure that Batch state is set to Batch.RUNNING 
			Batch.getInstance().getStatus().setStatus(Batch.Status.RUNNING);
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
