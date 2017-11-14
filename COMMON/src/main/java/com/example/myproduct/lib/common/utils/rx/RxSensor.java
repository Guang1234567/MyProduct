package com.example.myproduct.lib.common.utils.rx;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;

/**
 * @author lihanguang
 * @date 2017/5/15 00:39
 */

public class RxSensor {

    private RxSensor() {
        throw new AssertionError("No instances.");
    }

    public static Flowable<SensorEvent> onSensorChanged(final Context context) {
        SensorManager sensormanager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return onSensorChanged(sensormanager);
    }

    public static Flowable<SensorEvent> onSensorChanged(final SensorManager sensorManager) {
        return Flowable.create(new FlowableOnSubscribe<SensorEvent>() {
            @Override
            public void subscribe(FlowableEmitter<SensorEvent> emitter) throws Exception {
                SensorChangedListener listener = new SensorChangedListener(sensorManager, emitter);
                emitter.setDisposable(listener);
            }
        }, BackpressureStrategy.LATEST);
    }

    public static final class SensorChangedListener implements Disposable, SensorEventListener {

        private AtomicBoolean mIsDispose;

        private final SensorManager mSensorManager;
        private final FlowableEmitter<SensorEvent> mEmitter;

        SensorChangedListener(SensorManager sensormanager, FlowableEmitter<SensorEvent> emitter) {
            mIsDispose = new AtomicBoolean(false);
            mSensorManager = sensormanager;
            mEmitter = emitter;

            init(sensormanager);
        }

        private void init(SensorManager sensormanager) {
            sensormanager.registerListener(this,
                    sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);
        }

        @Override
        public void dispose() {
            if (mIsDispose.compareAndSet(false, true)) {
                mSensorManager.unregisterListener(this);
            }
        }

        @Override
        public boolean isDisposed() {
            return mIsDispose.get();
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (!isDisposed()) {
                mEmitter.onNext(event);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
