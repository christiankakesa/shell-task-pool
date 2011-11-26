package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: christian
 * Date: 30/04/11
 * Time: 00:27
 * .
 */
public class MyThreadPoolExecutor extends ThreadPoolExecutor {
    private static final Log LOG = LogFactory.getLog(MyThreadPoolExecutor.class);

    public MyThreadPoolExecutor(int poolSize) {
        super(poolSize, poolSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
    }

    public MyThreadPoolExecutor(int poolSize, int maxPoolSize) {
        super(poolSize, maxPoolSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
    }

    public MyThreadPoolExecutor(int poolSize, int maxPoolSize, long keepAliveTime) {
        super(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
    }

    public void addTask(Runnable r) {
        super.execute(r);
        LOG.debug("Task " + r.toString() + " added");
    }

    @Override
    public void shutdown() {
        super.shutdown();
        LOG.debug("ThreadPool shutdown. All pool is working !!!");
    }
    /*
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        //TODO: What to do here
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        //TODO: What to do here
    }
    */
}
