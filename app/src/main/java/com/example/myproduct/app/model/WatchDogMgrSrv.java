package com.example.myproduct.app.model;

import android.content.Context;

import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.lib.common.utils.os.ProcessUtils;
import com.example.myproduct.sdk.model.core.IAppMgrSrv;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author lihanguang
 * @date 2017/3/15 10:58:51
 */

public class WatchDogMgrSrv extends BaseStartStopable implements IAppMgrSrv {

    private Thread.UncaughtExceptionHandler mOldDefaultHandler;

    private List<MultiProcessUncaughtExceptionHandler> mExcpHandlers;

    private WatchDogMgrSrv() {
        mExcpHandlers = new LinkedList<>();
    }

    public static WatchDogMgrSrv create() {
        return new WatchDogMgrSrv();
    }

    public WatchDogMgrSrv add(MultiProcessUncaughtExceptionHandler excpHandler) {
        if (excpHandler != null) {
            mExcpHandlers.add(excpHandler);
        }
        return this;
    }

    public WatchDogMgrSrv remove(MultiProcessUncaughtExceptionHandler excpHandler) {
        if (excpHandler != null) {
            mExcpHandlers.remove(excpHandler);
        }
        return this;
    }

    @Override
    protected void onStart(Context context) {
        mOldDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                if (mExcpHandlers.isEmpty()) {
                    if (mOldDefaultHandler != null) {
                        mOldDefaultHandler.uncaughtException(t, e);
                    }
                } else {
                    ListIterator<MultiProcessUncaughtExceptionHandler> it = mExcpHandlers.listIterator(mExcpHandlers.size());
                    while (it.hasPrevious()) {
                        MultiProcessUncaughtExceptionHandler handler = it.previous();
                        if (handler != null) {
                            handler.uncaughtException(
                                    ProcessUtils.getCurrentProcessId(),
                                    ProcessUtils.getCurrentProcessName(),
                                    t,
                                    e);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStop(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(mOldDefaultHandler);
        mOldDefaultHandler = null;
    }

    public interface MultiProcessUncaughtExceptionHandler {
        void uncaughtException(int processId, String processName, Thread t, Throwable e);
    }
}
