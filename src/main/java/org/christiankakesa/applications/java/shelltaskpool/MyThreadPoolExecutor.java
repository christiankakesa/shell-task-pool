package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 */
public class MyThreadPoolExecutor extends ThreadPoolExecutor {
    private static final Log LOG = LogFactory.getLog(MyThreadPoolExecutor.class.getName());

    public MyThreadPoolExecutor(int poolSize) {
        super(poolSize, poolSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
        this.myInit();
    }

    public MyThreadPoolExecutor(int poolSize, int maxPoolSize) {
        super(poolSize, maxPoolSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
        this.myInit();
    }

    public MyThreadPoolExecutor(int poolSize, int maxPoolSize, long keepAliveTime) {
        super(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
        this.myInit();
    }
    
    private void myInit() {
    	Batch.getInstance().setBatchStartDate(Calendar.getInstance().getTime());
    	LOG.info("[BATCH_START] BatchId: " + Batch.getInstance().getBatchId()
				+ " | BatchName: " + Batch.getInstance().getBatchName()
				+ " | BatchStartDate: " + Batch.getInstance().getBatchStartDate()
				+ " | BatchStatus: " + Batch.getInstance().getBatchStatus());
    }

    public void addTask(Runnable r) {
        super.execute(r);
        LOG.debug("Task " + r.toString() + " added");
    }
    
    @Override
    public void terminated() {
    	try {
    	Batch.getInstance().setBatchEndDate(Calendar.getInstance().getTime());
    	LOG.info("[BATCH_END] - BatchId: " + Batch.getInstance().getBatchId()
				+ " | BatchName: " + Batch.getInstance().getBatchName()
				+ " | BatchStartDate: " + Batch.getInstance().getBatchStartDate()
				+ " | BatchEndDate: " + Batch.getInstance().getBatchEndDate()
				+ " | BatchDuration: " + Batch.getInstance().getBatchDuration()
				+ " | BatchStatus: " + Batch.getInstance().getBatchStatus());
    	} finally {
    		super.terminated();
    	}
    }
    
    /*@Override
    public void shutdown() {
        try {
        	LOG.debug("ThreadPool shutdown. All pool is working !!!");
        } finally {
        	super.shutdown();
        }
    }
    
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        //TODO: What to do here
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) { 
        try {
        	//TODO: What to do here
        } finally {
        	super.afterExecute(r, t);
        }
    }*/
}
