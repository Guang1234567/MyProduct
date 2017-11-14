package com.example.myproduct.lib.common.utils.thread.executors;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lihanguang
 * @date 2017/10/13 17:13
 */

public class PausableScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor implements Pausable {
    private boolean mIsPaused;
    private ReentrantLock mPauseLock = new ReentrantLock();
    private Condition mUnPaused = mPauseLock.newCondition();

    public PausableScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public PausableScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public PausableScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public PausableScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }


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

    public void pause() {
        mPauseLock.lock();
        try {
            mIsPaused = true;
        } finally {
            mPauseLock.unlock();
        }
    }

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
