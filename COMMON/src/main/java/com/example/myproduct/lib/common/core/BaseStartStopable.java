package com.example.myproduct.lib.common.core;

import android.content.Context;

/**
 * @author lihanguang
 * @date 2017/3/21 19:40:03
 */

public abstract class BaseStartStopable implements IStartStopable {
    private boolean mIsStarted;

    protected BaseStartStopable() {
        mIsStarted = false;
    }

    final public String getLogTag() {
        return this.getClass().getSimpleName();
    }

    final public boolean isStarted() {
        return mIsStarted;
    }

    @Override
    final public void start(Context context) {
        if (!mIsStarted) {
            onStart(context);
            mIsStarted = true;
        }
    }

    @Override
    final public void stop(Context context) {
        if (mIsStarted) {
            onStop(context);
            mIsStarted = false;
        }
    }

    abstract protected void onStart(Context context);

    abstract protected void onStop(Context context);
}
