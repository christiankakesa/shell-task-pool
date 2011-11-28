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
    private static final Log LOG = LogFactory.getLog(MyThreadPoolExecutor.class);

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
    	LOG.info("Batch start -Batch id: " + Batch.getInstance().getBatchId()
				+ " | Batch name: " + Batch.getInstance().getBatchName()
				+ " | Batch startDate: " + Batch.getInstance().getBatchStartDate()
				+ " | Batch endDate: " + Batch.getInstance().getBatchEndDate()
				+ " | Batch status: " + Batch.getInstance().getBatchStatus());
    }

    public void addTask(Runnable r) {
        super.execute(r);
        LOG.debug("Task " + r.toString() + " added");
    }
    
    @Override
    public void terminated() {
    	super.terminated();
    	Batch.getInstance().setBatchEndDate(Calendar.getInstance().getTime());
    	LOG.info("Batch end -Batch id: " + Batch.getInstance().getBatchId()
				+ " | Batch name: " + Batch.getInstance().getBatchName()
				+ " | Batch startDate: " + Batch.getInstance().getBatchStartDate()
				+ " | Batch endDate: " + Batch.getInstance().getBatchEndDate()
				+ " | Batch status: " + Batch.getInstance().getBatchStatus());
    }
    
    /*@Override
    public void shutdown() {
        super.shutdown();
        LOG.debug("ThreadPool shutdown. All pool is working !!!");
    }
    
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        //TODO: What to do here
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        //TODO: What to do here
    }*/
}
