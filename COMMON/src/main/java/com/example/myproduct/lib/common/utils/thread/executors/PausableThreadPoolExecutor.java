package com.example.myproduct.lib.common.utils.thread.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lihanguang
 * @date 2017/10/13 10:32
 */

public class PausableThreadPoolExecutor extends ThreadPoolExecutor implements Pausable {

    private boolean mIsPaused;
    private ReentrantLock mPauseLock = new ReentrantLock();
    private Condition mUnPaused = mPauseLock.newCondition();

    public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        mPauseLock.lock();
        try {
            while (mIsPaused) mUnPaused.await();
        } catch (InterruptedException ie) {
            t.interrupt();
        } finally {
            mPauseLock.unlock();
        }
    }

    @Override
    public void pause() {
        mPauseLock.lock();
        try {
            mIsPaused = true;
        } finally {
            mPauseLock.unlock();
        }
    }

    @Override
    public void resume() {
        mPauseLock.lock();
        try {
            mIsPaused = false;
            mUnPaused.signalAll();
        } finally {
            mPauseLock.unlock();
        }
    }
}
