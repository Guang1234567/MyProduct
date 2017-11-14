package com.example.myproduct.lib.common.utils.net.retrofit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author lihanguang
 * @date 2017/7/7 17:42
 */

public class ApiOkResultTransformer<DATA, ERROR> implements ObservableTransformer<ApiResult<DATA, ERROR>, DATA> {
    @Override
    public ObservableSource<DATA> apply(Observable<ApiResult<DATA, ERROR>> upstream) {
        return upstream
                .filter(new Predicate<ApiResult<DATA, ERROR>>() {
                    @Override
                    public boolean test(ApiResult<DATA, ERROR> apiResult) throws Exception {
                        return apiResult.isOk();
                    }
                })
                .map(new Function<ApiResult<DATA, ERROR>, DATA>() {

                    @Override
                    public DATA apply(ApiResult<DATA, ERROR> r) throws Exception {
                        return r.getData();
                    }
                });
    }
}
