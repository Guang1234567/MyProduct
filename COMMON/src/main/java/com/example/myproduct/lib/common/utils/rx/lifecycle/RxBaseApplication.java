package com.example.myproduct.lib.common.utils.rx.lifecycle;

import android.app.Application;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.annotation.Nonnull;

import io.reactivex.Observable;

/**
 * @author lihanguang
 * @date 2017/7/13 10:45
 */

public abstract class RxBaseApplication extends Application implements LifecycleProvider<ApplicationEvent> {

    private final RxApplicationLifecycleProviderImpl mLifecycleProvider;

    public RxBaseApplication() {
        super();
        mLifecycleProvider = new RxApplicationLifecycleProviderImpl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLifecycleProvider.onNext(ApplicationEvent.CREATE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    protected void terminateAppSafe() {
        mLifecycleProvider.onNext(ApplicationEvent.TERMINATE);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mLifecycleProvider.onNext(ApplicationEvent.LOW_MEMORY);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        mLifecycleProvider.onNext(ApplicationEvent.TRIM_MEMORY);
    }

    @Nonnull
    @Override
    public final Observable<ApplicationEvent> lifecycle() {
        return mLifecycleProvider.lifecycle();
    }

    @Nonnull
    @Override
    public final <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ApplicationEvent event) {
        return mLifecycleProvider.bindUntilEvent(event);
    }

    @Nonnull
    @Override
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return mLifecycleProvider.bindToLifecycle();
    }
}
