package com.example.myproduct.lib.common.utils.thread.executors;

import android.os.Looper;

import com.example.myproduct.lib.common.utils.thread.ThreadUtils;

/**
 * 按顺序异步执行 SQLiteDatabase 相关任务的 Executor.
 * 另外, 额外提供
 * {@link SQLiteDatabaseThreadExecutor#pause()}
 * {@link SQLiteDatabaseThreadExecutor#resume()}
 * 这两个接口用于应对临时要暂停 SQLiteDatabaseThreadExecutor 的情景.
 *
 * @author lihanguang
 * @date 2017/10/13 10:06
 */

public class SQLiteDatabaseThreadExecutor extends LooperExecutor /*implements Pausable*/ {

    /*private boolean mIsPaused;
    private final ReentrantLock mPauseLock = new ReentrantLock();
    private final Condition mUnPaused = mPauseLock.newCondition();*/

    private SQLiteDatabaseThreadExecutor(Looper looper) {
        super(looper);
    }

    public static SQLiteDatabaseThreadExecutor create(Looper databaseLoop) {
        if (Looper.getMainLooper() == databaseLoop) {
            throw new IllegalArgumentException("Cannot be a MainLooper!");
        }
        return new SQLiteDatabaseThreadExecutor(databaseLoop);
    }

    /*@Override
    protected void beforeExecute(Thread t, Runnable r) {
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
    }*/
}

/*
public class SQLiteDatabaseThreadExecutor<T extends ExecutorService>
        extends DelegatedExecutorService<T>
        implements Pausable {
    private Pausable mPausable;

    private SQLiteDatabaseThreadExecutor(T es, Pausable pausable) {
        super(es);
        mPausable = pausable;
    }

    public static SQLiteDatabaseThreadExecutor createSingle() {
        //ExecutorService es = Executors.newSingleThreadExecutor(...);
        PausableThreadPoolExecutor es = new PausableThreadPoolExecutor(
                1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new RxThreadFactory("SQLiteDatabaseThreadExecutor_Single", Thread.NORM_PRIORITY));
        return new SQLiteDatabaseThreadExecutor(es, es);
    }

    public static SQLiteDatabaseThreadExecutor createCached() {
        //ExecutorService es = Executors.newCachedThreadPool(...);
        PausableThreadPoolExecutor es = new PausableThreadPoolExecutor(
                0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new RxThreadFactory("SQLiteDatabaseThreadExecutor_Cached", Thread.NORM_PRIORITY));
        return new SQLiteDatabaseThreadExecutor(es, es);
    }

    public static SQLiteDatabaseThreadExecutor createFixed(int nThreads) {
        //ExecutorService es = Executors.newFixedThreadPool(...);
        PausableThreadPoolExecutor es = new PausableThreadPoolExecutor(
                nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new RxThreadFactory("SQLiteDatabaseThreadExecutor_Fixed_nThreads(" + nThreads + ')', Thread.NORM_PRIORITY));
        return new SQLiteDatabaseThreadExecutor(es, es);
    }

    public static SQLiteDatabaseThreadExecutor createScheduled(int corePoolSize) {
        //ExecutorService es = Executors.newScheduledThreadPool(...);
        PausableScheduledThreadPoolExecutor es = new PausableScheduledThreadPoolExecutor(
                corePoolSize,
                new RxThreadFactory("SQLiteDatabaseThreadExecutor_Scheduled_corePoolSize("+corePoolSize +')', Thread.NORM_PRIORITY));
        return new SQLiteDatabaseThreadExecutor(es, es);
    }

    @Override
    public void pause() {
        mPausable.pause();
    }

    @Override
    public void resume() {
        mPausable.resume();
    }
}
*/

