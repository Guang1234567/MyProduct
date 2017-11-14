package com.example.myproduct.presentation.presenter;

import android.support.annotation.Nullable;

/**
 * MVP 模式中 Presenter 的接口.
 *
 * 泛型 <VIEW> 表示视图, 通常是一个被Activity Fragment View 所实现的接口.
 *
 * @author lihanguang
 * @date 2016/3/17 13:49:49
 */
public interface IPresenter<VIEW> {
    void attachView(@Nullable VIEW view);

    void detachView(@Nullable VIEW view);
}
