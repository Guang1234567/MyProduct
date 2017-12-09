package com.example.myproduct.lib.common.utils.rx.lifecycle;

import com.example.myproduct.lib.common.utils.log.Log;

import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.flowable.FlowableInternalHelper;
import io.reactivex.internal.subscribers.LambdaSubscriber;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.subscribers.SerializedSubscriber;

/**
 * @author lihanguang
 * @date 2017/12/7 17:28
 */

public class RxObject<V> {

    private V mValue;

    private final FlowableProcessor<V> mValueChanged;

    private final Subscriber<V> mSetter;

    public RxObject(V value) {
        mValue = value;
        mValueChanged = BehaviorProcessor.createDefault(value).toSerialized();

        mSetter = new SerializedSubscriber<>(new LambdaSubscriber<>(
                new Consumer<V>() {
                    @Override
                    public void accept(V value) throws Exception {
                        onSet(value);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mValueChanged.onError(throwable);
                    }
                },
                new Action() {
                    @Override
                    public void run() throws Exception {
                        mValueChanged.onComplete();
                    }
                },
                FlowableInternalHelper.RequestMax.INSTANCE
        ));
    }

    public Flowable<V> getter() {
        synchronized (mValue) {
            return Flowable.just(mValue);
        }
    }

    public Subscriber<V> setter() {
        return mSetter;
    }

    private void onSet(V value) {
        synchronized (mValue) {
            mValue = value;
        }
        mValueChanged.onNext(value);
    }

    public Flowable<V> valueChanged() {
        return mValueChanged.hide();
    }
}

class Demo {
    public Demo() {
        RxObject<Long> rxObj = new RxObject<>(-1L);

        // 输入
        Flowable.interval(0, 1, TimeUnit.SECONDS)
                .take(10)
                .subscribe(rxObj.setter());

        // 输出
        rxObj.valueChanged().blockingForEach(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                Log.d("RxObject_Demo", String.valueOf(aLong));
            }
        });
    }
}
