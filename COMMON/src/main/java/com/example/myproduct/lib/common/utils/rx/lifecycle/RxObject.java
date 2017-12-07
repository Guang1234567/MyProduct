package com.example.myproduct.lib.common.utils.rx.lifecycle;

import io.reactivex.Flowable;
import io.reactivex.processors.BehaviorProcessor;

/**
 * @author lihanguang
 * @date 2017/12/7 17:28
 */

public class RxObject<Value> {

    private Value mValue;

    private BehaviorProcessor<Value> mProcessor;

    public RxObject(Value value) {
        mValue = value;
        mProcessor = BehaviorProcessor.createDefault(value);
    }

    public Value getValue() {
        return mValue;
    }

    public void setValue(Value value) {
        if (value != mValue) {
            mValue = value;
            mProcessor.onNext(value);
        }
    }

    public Flowable<Value> toFlowable() {
        return mProcessor.hide();
    }
}
