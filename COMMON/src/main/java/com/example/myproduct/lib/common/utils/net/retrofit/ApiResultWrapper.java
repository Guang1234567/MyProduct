package com.example.myproduct.lib.common.utils.net.retrofit;

import io.reactivex.Observable;

public interface ApiResultWrapper<DATA, ERROR> {

    Observable<DATA> onOk();

    Observable<ERROR> onError();

    Observable<Throwable> onThrowable();
}
