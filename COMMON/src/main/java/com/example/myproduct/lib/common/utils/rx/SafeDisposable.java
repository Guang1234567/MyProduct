package com.example.myproduct.lib.common.utils.rx;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.disposables.Disposable;

/**
 * @author lihanguang
 * @date 2017/5/26 14:44:11
 */

public abstract class SafeDisposable implements Disposable {

    private AtomicBoolean mUnsubscribed;

    public SafeDisposable() {
        mUnsubscribed = new AtomicBoolean(false);
    }

    @Override
    public final void dispose() {
        if (mUnsubscribed.compareAndSet(false, true)) {
            onDispose();
        }
    }

    @Override
    public final boolean isDisposed() {
        return mUnsubscribed.get();
    }

    protected abstract void onDispose();
}
