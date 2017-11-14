package com.example.myproduct.lib.common.utils.thread.executors;

import android.os.Looper;

/**
 * @author lihanguang
 * @date 2017/10/13 13:14
 */

public class WorkerThreadExecutor extends LooperExecutor {
    public static final String TAG = "WorkerThreadExecutor";

    private WorkerThreadExecutor(Looper workerLoop) {
        super(workerLoop);
    }

    public static WorkerThreadExecutor create(Looper workerLoop) {
        if (Looper.getMainLooper() == workerLoop) {
            throw new IllegalArgumentException("Cannot be a MainLooper!");
        }
        return new WorkerThreadExecutor(workerLoop);
    }
}
