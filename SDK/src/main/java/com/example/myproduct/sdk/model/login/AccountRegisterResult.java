package com.example.myproduct.sdk.model.login;

import com.google.auto.value.AutoValue;

/**
 * @author lihanguang
 * @date 2017/5/7 16:57
 */
@AutoValue
public abstract class AccountRegisterResult<T> {

    public abstract AccountRegisterWay registerWay();

    public abstract T body();

    public static <R> Builder<R> builder() {
        return new AutoValue_AccountRegisterResult.Builder<>();
    }


    @AutoValue.Builder
    public abstract static class Builder<T> {
        public abstract Builder<T> registerWay(AccountRegisterWay registerWay);

        public abstract Builder<T> body(T body);

        public abstract AccountRegisterResult<T> build();
    }
}
