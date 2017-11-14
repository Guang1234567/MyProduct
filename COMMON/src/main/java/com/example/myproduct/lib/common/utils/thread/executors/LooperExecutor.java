package com.example.myproduct.lib.common.utils.thread.executors;

import android.os.Handler;
import android.os.Looper;

import com.example.myproduct.lib.common.utils.thread.ThreadUtils;

import java.util.concurrent.Executor;

/**
 * @author lihanguang
 * @date 2017/10/13 16:45
 */

public class LooperExecutor implements Executor {
    private final Handler mHandler;

    public LooperExecutor(Looper looper) {
        mHandler = new Handler(looper);
    }

    /**
     * 与 {@link #post(Runnable)} 的功能差不多.
     * 如果当前线程就是想要切换的目的线程, 则直接运行.
     *
     * @param r
     */
    @Override
    public void execute(Runnable r) {
        Runnable wR = wrapBeforeExecute(r);
        if (Looper.myLooper() == mHandler.getLooper()) {
            wR.run();
        } else {
            mHandler.post(wR);
        }
    }

    public boolean post(Runnable r) {
        Runnable wR = wrapBeforeExecute(r);
        return mHandler.post(wR);
    }

    /**
     * 优先执行
     *
     * @param r
     */
    public void postAtFrontOfQueue(Runnable r) {
        Runnable wR = wrapBeforeExecute(r);
        mHandler.postAtFrontOfQueue(wR);
    }

    /**
     * 延迟执行
     *
     * @param r
     */
    public void postDelayed(Runnable r, long delayMillis) {
        Runnable wR = wrapBeforeExecute(r);
        mHandler.postDelayed(wR, delayMillis);
    }

    private Runnable wrapBeforeExecute(final Runnable r) {
        return new Runnable() {
            @Override
            public void run() {
                beforeExecute(Thread.currentThread(), r);
                ThreadUtils.wrap(r).run();
            }
        };
    }

    protected void beforeExecute(Thread t, Runnable r) {
    }
}
