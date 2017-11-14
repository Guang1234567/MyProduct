package com.example.myproduct.lib.common.utils.rx.lifecycle;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author lihanguang
 * @date 2017/7/13 10:46
 */

public class RxApplicationLifecycleProviderImpl implements LifecycleProvider<ApplicationEvent> {
    private final BehaviorSubject<ApplicationEvent> mLifecycleSubject;

    public RxApplicationLifecycleProviderImpl() {
        mLifecycleSubject = BehaviorSubject.create();
    }

    public void onNext(@Nonnull ApplicationEvent event) {
        mLifecycleSubject.onNext(event);
    }

    @Nonnull
    @Override
    public Observable<ApplicationEvent> lifecycle() {
        return mLifecycleSubject.hide();
    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ApplicationEvent event) {
        return RxLifecycle.bindUntilEvent(mLifecycleSubject, event);
    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.bind(mLifecycleSubject, ApplicationEvent.VIEWHOLDER_LIFECYCLE);
    }
}
