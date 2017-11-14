package com.example.myproduct.lib.common_ui.utils.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.example.myproduct.lib.common.utils.Utils;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;

/**
 * @author lihanguang
 * @date 2017/5/27 10:03:57
 */

public class RxToast {

    private RxToast() {
        throw new AssertionError("No instances.");
    }

    public static Flowable<Toast> makeText(final Context context, @StringRes final int resStrId, final int duration) {
        String content = context.getResources().getString(resStrId);
        return makeText(context, content, duration);
    }

    public static Flowable<Toast> makeText(final Context context, @NonNull final CharSequence content, final int duration) {
        return Flowable.create(new FlowableOnSubscribe<Toast>() {
            @Override
            public void subscribe(final FlowableEmitter<Toast> emitter) throws Exception {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        final Toast t = Toast.makeText(context, content, duration);
                        emitter.setDisposable(new MainThreadDisposable() {
                            @Override
                            protected void onDispose() {
                                if (t != null) {
                                    t.cancel();
                                }
                            }
                        });

                        emitter.onNext(t);
                    }
                };

                if (Utils.isOnMainThread()) {
                    r.run();
                } else {
                    new Handler(Looper.getMainLooper()).post(r);
                }
            }
        }, BackpressureStrategy.LATEST);
    }

    public static Flowable<Toast> create(final Context context) {
        return Flowable.create(new FlowableOnSubscribe<Toast>() {
            @Override
            public void subscribe(FlowableEmitter<Toast> emitter) throws Exception {
                final Toast t = new Toast(context);
                emitter.setDisposable(new MainThreadDisposable() {
                    @Override
                    protected void onDispose() {
                        if (t != null) {
                            t.cancel();
                        }
                    }
                });

                emitter.onNext(t);
            }
        }, BackpressureStrategy.LATEST);
    }
}
