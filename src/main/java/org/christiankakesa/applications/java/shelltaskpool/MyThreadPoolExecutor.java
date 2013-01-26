package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
class MyThreadPoolExecutor extends ThreadPoolExecutor {
    private static final Logger LOG = Logger.getLogger(MyThreadPoolExecutor.class);

	/*public MyThreadPoolExecutor(int poolSize) {
        super(poolSize, poolSize, 0L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
		this.myInit();
	}*/

    public MyThreadPoolExecutor(int poolSize, int maxPoolSize) {
        super(poolSize, maxPoolSize, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
        this.myInit();
    }

	/*public MyThreadPoolExecutor(int poolSize, int maxPoolSize,
			long keepAliveTime) {
		super(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
		this.myInit();
	}*/

    private void myInit() {
        Batch.getInstance().getBatchStatus().setStatus(Batch.Status.STARTED);
        Batch.getInstance().setStartDate(Calendar.getInstance().getTime());
        synchronized (MyThreadPoolExecutor.class) { // We need synchronized here because "+" operator is not thread safe
            Logger.getLogger("STDOUT").log(Level.INFO, " batch:start|id:" + Batch.getInstance().getId()
                    + "|name:" + Batch.getInstance().getName()
                    + "|parameters:" + Batch.getInstance().getStringParameters()
                    + "|workers:" + this.getCorePoolSize()
                    + "|number_of_jobs:" + this.getTaskCount()
                    + "|jobs_file:" + Batch.getInstance().getJobsFile()
                    + "|log_dir:" + Batch.getInstance().getLogDirectory()
                    + "|start_date:" + Batch.getInstance().getStartDate()
                    + "|status:" + Batch.getInstance().getBatchStatus().getStatus());
        }
    }

    public void addTask(Runnable r) {
        super.execute(r);
        LOG.debug("Task " + r.toString() + " added");
    }

    @Override
    public void terminated() {
        Batch.getInstance().setEndDate(Calendar.getInstance().getTime());
        Batch.getInstance().getBatchStatus().doEndStatus();
        synchronized (MyThreadPoolExecutor.class) { // We need synchronized here because "+" operator is not thread safe
            Logger.getLogger("STDOUT").log(Level.INFO, "batch:end|id:" + Batch.getInstance().getId()
                    + "|name:" + Batch.getInstance().getName()
                    + "|start_date:" + Batch.getInstance().getStartDate()
                    + "|end_date:" + Batch.getInstance().getEndDate()
                    + "|duration:" + Util.buildDurationFromDates(Batch.getInstance().getStartDate(),
                    Batch.getInstance().getEndDate())
                    + "|status:" + Batch.getInstance().getBatchStatus().getStatus());
        }
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
//			// Perhaps launching a web server for monitoring all batch
//		} finally {
//			super.afterExecute(r, t);
//		}
//	 }
}
