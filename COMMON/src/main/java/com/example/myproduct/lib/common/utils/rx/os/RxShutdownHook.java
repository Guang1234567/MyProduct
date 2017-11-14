package com.example.myproduct.lib.common.utils.rx.os;

import com.example.myproduct.lib.common.utils.rx.SafeDisposable;

import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * @author lihanguang
 * @date 2017/7/14 17:18
 */

public class RxShutdownHook {
    public static final String TAG = "RxShutdownHook";

    private RxShutdownHook() {
        throw new AssertionError("No instances.");
    }

    public static Flowable<Thread> onHook() {
        return Flowable.create(new FlowableOnSubscribe<Thread>() {
            @Override
            public void subscribe(final FlowableEmitter<Thread> emitter) throws Exception {
                final Thread hook = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        emitter.onNext(Thread.currentThread());
                        emitter.onComplete();
                    }
                }, TAG + " # newThread");
                Runtime.getRuntime().addShutdownHook(hook);
                emitter.setDisposable(new SafeDisposable() {
                    @Override
                    protected void onDispose() {
                        //Runtime.getRuntime().removeShutdownHook(hook);
                    }
                });
            }
        }, BackpressureStrategy.MISSING);
    }
}
