package com.example.myproduct.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.myproduct.app.MyProductApplication;
import com.example.myproduct.lib.common.utils.Preconditions;
import com.example.myproduct.lib.common.utils.rx.lifecycle.ApplicationEvent;
import com.example.myproduct.lib.common.utils.rx.lifecycle.RxBaseApplication;
import com.example.myproduct.lib.common_ui.ui.RxBaseActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.reactivestreams.Publisher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * @author lihanguang
 * @date 2017/5/24 16:21:57
 */

public abstract class AppBaseActivity extends RxBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public final <T> RxUiLifeCycleTransformer<T> bindUntilEvent(@Nonnull ActivityEvent activityEvent, @Nonnull ApplicationEvent... applicationEvents) {
        return new RxUiLifeCycleTransformer<>(activityEvent,
                this,
                Arrays.asList(applicationEvents),
                MyProductApplication.getInstance());
    }

    private static class RxUiLifeCycleTransformer<T>
            implements ObservableTransformer<T, T>,
            FlowableTransformer<T, T>,
            SingleTransformer<T, T>,
            MaybeTransformer<T, T>,
            CompletableTransformer {

        private ActivityEvent mActivityEvent;
        private RxBaseActivity mRxBaseActivity;

        private List<ApplicationEvent> mApplicationEvents;
        private RxBaseApplication mRxBaseApplication;

        public RxUiLifeCycleTransformer(@Nonnull ActivityEvent activityEvent,
                                        @Nonnull RxBaseActivity rxBaseActivity,
                                        @Nonnull List<ApplicationEvent> applicationEvents,
                                        @Nonnull RxBaseApplication rxBaseApplication) {
            mActivityEvent = ObjectHelper.requireNonNull(activityEvent, "activityEvent == null");
            mRxBaseActivity = ObjectHelper.requireNonNull(rxBaseActivity, "activityLifecycle == null");

            mApplicationEvents = Preconditions.checkNotEmpty(
                    ObjectHelper.requireNonNull(applicationEvents, "applicationEvents == null"),
                    "applicationEvents is empty");
            mRxBaseApplication = ObjectHelper.requireNonNull(rxBaseApplication, "applicationLifecycle == null");
        }

        @Override
        public ObservableSource<T> apply(Observable<T> upstream) {
            Observable<T> downstream = upstream
                    .compose(mRxBaseActivity.<T>bindUntilEvent(mActivityEvent));

            Iterator<ApplicationEvent> iterator = mApplicationEvents.iterator();
            while (iterator.hasNext()) {
                ApplicationEvent e = iterator.next();
                if (e != null) {
                    downstream = downstream.compose(mRxBaseApplication.<T>bindUntilEvent(e));
                }
            }

            return downstream;
        }

        @Override
        public Publisher<T> apply(Flowable<T> upstream) {
            Flowable<T> downstream = upstream
                    .compose(mRxBaseActivity.<T>bindUntilEvent(mActivityEvent));

            Iterator<ApplicationEvent> iterator = mApplicationEvents.iterator();
            while (iterator.hasNext()) {
                ApplicationEvent e = iterator.next();
                if (e != null) {
                    downstream = downstream.compose(mRxBaseApplication.<T>bindUntilEvent(e));
                }
            }

            return downstream;
        }

        @Override
        public SingleSource<T> apply(Single<T> upstream) {
            Single<T> downstream = upstream
                    .compose(mRxBaseActivity.<T>bindUntilEvent(mActivityEvent));

            Iterator<ApplicationEvent> iterator = mApplicationEvents.iterator();
            while (iterator.hasNext()) {
                ApplicationEvent e = iterator.next();
                if (e != null) {
                    downstream = downstream.compose(mRxBaseApplication.<T>bindUntilEvent(e));
                }
            }

            return downstream;
        }

        @Override
        public MaybeSource<T> apply(Maybe<T> upstream) {
            Maybe<T> downstream = upstream
                    .compose(mRxBaseActivity.<T>bindUntilEvent(mActivityEvent));

            Iterator<ApplicationEvent> iterator = mApplicationEvents.iterator();
            while (iterator.hasNext()) {
                ApplicationEvent e = iterator.next();
                if (e != null) {
                    downstream = downstream.compose(mRxBaseApplication.<T>bindUntilEvent(e));
                }
            }

            return downstream;
        }

        @Override
        public CompletableSource apply(Completable upstream) {
            Completable downstream = upstream
                    .compose(mRxBaseActivity.<T>bindUntilEvent(mActivityEvent));

            Iterator<ApplicationEvent> iterator = mApplicationEvents.iterator();
            while (iterator.hasNext()) {
                ApplicationEvent e = iterator.next();
                if (e != null) {
                    downstream = downstream.compose(mRxBaseApplication.<T>bindUntilEvent(e));
                }
            }

            return downstream;
        }
    }
}
