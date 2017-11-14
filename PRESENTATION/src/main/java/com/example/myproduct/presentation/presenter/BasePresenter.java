package com.example.myproduct.presentation.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

/**
 * MVP 模式中 Presenter 的基类.
 * 提供一些常用的方法.
 *
 * @author lihanguang
 * @date 2016/3/18 9:08:22
 */
public abstract class BasePresenter<VIEW> implements IPresenter<VIEW> {
    private static final String TAG = "BasePresenter";

    /**
     * 关联的视图对象
     */
    private VIEW mView;

    private boolean mIsAttached;

    protected BasePresenter(final String LogTag) {
        mIsAttached = false;
    }

    final public boolean isAttached() {
        return mIsAttached;
    }

    @UiThread
    @Override
    final public void attachView(@NonNull VIEW view) {
        if (!mIsAttached) { // 防止重复 attach
            mView = view;
            onAttachView(view);
            mIsAttached = true;
        }
    }

    @UiThread
    abstract protected void onAttachView(@NonNull VIEW view);

    @UiThread
    @Override
    final public void detachView(@NonNull VIEW view) {
        if (mIsAttached) { // 防止重复 dettach
            mView = null;
            onDetachView(view);
            mIsAttached = false;
        }
    }

    @UiThread
    abstract protected void onDetachView(@NonNull VIEW view);

    @Nullable
    final public VIEW getView() {
        return mView;
    }
}
