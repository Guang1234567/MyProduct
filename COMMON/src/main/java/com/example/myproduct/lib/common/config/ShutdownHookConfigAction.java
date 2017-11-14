package com.example.myproduct.lib.common.config;

import android.content.Context;

import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.lib.common.utils.log.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author lihanguang
 * @date 2017/3/31 17:58:28
 */

public class ShutdownHookConfigAction extends BaseStartStopable implements IConfigAction {
    private Thread mHook;
    private List<IShutDownHook> mChildren;

    private ShutdownHookConfigAction() {
        mChildren = new LinkedList<>();
        mHook = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mChildren != null && !mChildren.isEmpty()) {
                    ListIterator<IShutDownHook> it = mChildren.listIterator(mChildren.size());
                    while (it.hasPrevious()) {
                        IShutDownHook hook = it.previous();
                        if (hook != null) {
                            try {
                                hook.beforeShutdown();
                            } catch (Throwable ignore) {
                                Log.e(getLogTag(), "ShutdownHookConfigAction # beforeShutdown : ", ignore);
                            }
                        }
                    }
                }
            }
        }, "Thread-ShutdownHookConfigAction");
    }

    public static ShutdownHookConfigAction create() {
        return new ShutdownHookConfigAction();
    }

    @Override
    protected void onStart(final Context context) {
        Runtime.getRuntime().addShutdownHook(mHook);
    }

    @Override
    protected void onStop(Context context) {
        mChildren.clear();
    }

    public ShutdownHookConfigAction add(IShutDownHook child) {
        if (child != null) {
            if (mChildren != null) {
                mChildren.add(child);
            }
        }
        return this;
    }

    public ShutdownHookConfigAction remove(IShutDownHook child) {
        if (child != null) {
            if (mChildren != null && !mChildren.isEmpty()) {
                mChildren.remove(child);
            }
        }
        return this;
    }

    public interface IShutDownHook {
        void beforeShutdown();
    }
}
