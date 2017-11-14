package com.example.myproduct.lib.common.utils.rx;

import android.os.Looper;

import com.example.myproduct.lib.common.utils.thread.ThreadUtils;

import java.util.concurrent.Executor;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lihanguang
 * @date 2017/7/14 13:53
 */

public class RxSchedulers {

    private static Scheduler workerThread;
    private static Scheduler databaseThread;

    public static Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    public static synchronized Scheduler workerThread() {
        if (workerThread == null) {
            workerThread = from(ThreadUtils.getWorkerLooper());
        }
        return workerThread;
    }

    public static synchronized Scheduler database() {
        if (databaseThread == null) {
            databaseThread = from(ThreadUtils.getDatabaseLooper());
        }
        return databaseThread;
    }

    public static Scheduler io() {
        return Schedulers.io();
    }

    public static Scheduler single() {
        return Schedulers.single();
    }

    public static Scheduler newThread() {
        return Schedulers.newThread();
    }

    public static Scheduler computation() {
        return Schedulers.computation();
    }

    public static Scheduler trampoline() {
        return Schedulers.trampoline();
    }

    public static Scheduler from(Looper looper) {
        return AndroidSchedulers.from(looper);
    }

    public static Scheduler from(Executor executor) {
        return Schedulers.from(executor);
    }
}
