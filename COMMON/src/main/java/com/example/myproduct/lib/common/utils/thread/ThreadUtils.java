package com.example.myproduct.lib.common.utils.thread;

import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;

import com.example.myproduct.lib.common.BuildConfig;
import com.example.myproduct.lib.common.utils.log.Log;
import com.example.myproduct.lib.common.utils.thread.executors.LooperExecutor;
import com.example.myproduct.lib.common.utils.thread.executors.SQLiteDatabaseThreadExecutor;
import com.example.myproduct.lib.common.utils.thread.executors.WorkerThreadExecutor;

/**
 * @author lihanguang
 * @date 2017/5/27 13:17:16
 */

public class ThreadUtils {

    public final static String TAG = "ThreadUtils";

    private ThreadUtils() {
        throw new AssertionError("No instances.");
    }

    private static final HandlerThread sWHT;
    private static final HandlerThread sDBHT;

    private static final LooperExecutor sWorkerExecutor;
    private static final SQLiteDatabaseThreadExecutor sDatabaseExecutor;

    static {
        sDBHT = new HandlerThread("ThreadUtils # DatabaseHandlerThread"); // Model (数据)层专用
        sWHT = new HandlerThread("ThreadUtils # WorkerHandlerThread");    // Presenter (业务)层专用

        sDBHT.start();
        sWHT.start();

        sDatabaseExecutor = SQLiteDatabaseThreadExecutor.create(sDBHT.getLooper());
        sWorkerExecutor = WorkerThreadExecutor.create(sWHT.getLooper());
    }

    public static Looper getMainLooper() {
        return Looper.getMainLooper();
    }

    public static Looper getWorkerLooper() {
        return sWHT.getLooper();
    }

    public static Looper getDatabaseLooper() {
        return sDBHT.getLooper();
    }

    public static LooperExecutor globalWorkerExecutor() {
        return sWorkerExecutor;
    }

    public static LooperExecutor globalDatabaseExecutor() {
        return sDatabaseExecutor;
    }

    public static Runnable wrap(final Runnable r) {
        if (!BuildConfig.DEBUG && !BuildConfig.IS_DEV_MODE) {
            return r;
        }

        final String srcThreadInfo = String.valueOf(Thread.currentThread());
        final Exception stack = new Exception();
        return new Runnable() {
            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    Log.w(TAG, "┏ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┓");
                    Log.w(TAG, new StringBuilder("┃ ")
                            .append(this)
                            .append(" was executed / posted at ")
                            .append(srcThreadInfo)
                            .toString(), stack);
                    Log.w(TAG, "┗ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┛");

                    throw t; // rethrow
                }
            }
        };
    }

    public static void start() {
    }

    public static void stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sWHT.quitSafely();
            sDBHT.quitSafely();
        } else {
            sWHT.quit();
            sDBHT.quit();
        }
    }
}
