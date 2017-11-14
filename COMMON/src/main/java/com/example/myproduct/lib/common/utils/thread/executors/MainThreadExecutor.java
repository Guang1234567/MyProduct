package com.example.myproduct.lib.common.utils.thread.executors;

import android.os.Looper;

/**
 * @author lihanguang
 * @date 2017/10/13 10:02
 */

public class MainThreadExecutor extends LooperExecutor {
    private MainThreadExecutor() {
        super(Looper.getMainLooper());
    }

    public static MainThreadExecutor create() {
        return new MainThreadExecutor();
    }
}
