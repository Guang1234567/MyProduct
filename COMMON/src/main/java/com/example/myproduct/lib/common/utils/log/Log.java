package com.example.myproduct.lib.common.utils.log;

import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;

import com.example.myproduct.lib.common.utils.os.ProcessUtils;
import com.tencent.mars.xlog.Xlog;

/**
 * @author lihanguang
 * @date 2017/3/21 20:23:59
 */

public class Log {

    private static com.tencent.mars.xlog.Log.LogImp sLogImp;

    public static void open(String logDir, Boolean debugMode) {

        String cacheDir = logDir + "/cache";

        final String currentProcessName = ProcessUtils.getCurrentProcessName();
        final int currentProcessId = ProcessUtils.getCurrentProcessId();
        final String logFileNamePrefix = new StringBuilder(currentProcessName).append("_pid(").append(currentProcessId).append(')').toString();

        //init xlog
        if (debugMode) {
            Xlog.open(true, Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cacheDir, logDir, logFileNamePrefix);
            Xlog.setConsoleLogOpen(true);
        } else {
            Xlog.open(true, Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cacheDir, logDir, logFileNamePrefix);
            Xlog.setConsoleLogOpen(false);
        }

        sLogImp = new Xlog();
    }

    public static void close() {
        if (sLogImp != null) {
            sLogImp.appenderClose();
        }
    }

    public static void flush(boolean isSync) {
        if (sLogImp != null) {
            sLogImp.appenderFlush(isSync);
        }
    }

    public static void v(String tag, String content) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logV(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    content);
        } else {
            android.util.Log.v(tag, content);
        }
    }

    public static void v(String tag, String content, Throwable tr) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logV(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    (TextUtils.isEmpty(content) ? "" : content) + '\n' + android.util.Log.getStackTraceString(tr));
        } else {
            android.util.Log.v(tag, content, tr);
        }
    }

    public static void d(String tag, String content) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logD(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    content);
        } else {
            android.util.Log.d(tag, content);
        }
    }

    public static void d(String tag, String content, Throwable tr) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logD(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    (TextUtils.isEmpty(content) ? "" : content) + '\n' + android.util.Log.getStackTraceString(tr));
        } else {
            android.util.Log.d(tag, content, tr);
        }
    }

    public static void i(String tag, String content) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logI(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    content);
        } else {
            android.util.Log.i(tag, content);
        }
    }

    public static void i(String tag, String content, Throwable tr) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logI(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    (TextUtils.isEmpty(content) ? "" : content) + '\n' + android.util.Log.getStackTraceString(tr));
        } else {
            android.util.Log.i(tag, content, tr);
        }
    }


    public static void w(String tag, String content) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logW(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    content);
        } else {
            android.util.Log.w(tag, content);
        }
    }

    public static void w(String tag, String content, Throwable tr) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logW(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    (TextUtils.isEmpty(content) ? "" : content) + '\n' + android.util.Log.getStackTraceString(tr));
        } else {
            android.util.Log.w(tag, content, tr);
        }
    }

    public static void e(String tag, String content) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logE(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    content);
        } else {
            android.util.Log.e(tag, content);
        }
    }

    public static void e(String tag, String content, Throwable tr) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        if (sLogImp != null) {
            sLogImp.logE(tag,
                    fileName, methodName, lineNumber,
                    Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(),
                    (TextUtils.isEmpty(content) ? "" : content) + '\n' + android.util.Log.getStackTraceString(tr));
        } else {
            android.util.Log.e(tag, content, tr);
        }
    }

    /*final static Object[] getExtraInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 5;
        String fileName = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        return new Object[] {fileName, methodName, lineNumber};
    }*/
}
