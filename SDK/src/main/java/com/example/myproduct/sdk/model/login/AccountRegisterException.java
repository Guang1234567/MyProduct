package com.example.myproduct.sdk.model.login;

import com.google.auto.value.AutoValue;

import tencent.tls.platform.TLSErrInfo;

/**
 * @author lihanguang
 * @date 2017/5/7 17:08
 */

@AutoValue
public abstract class AccountRegisterException extends Exception {

    public abstract AccountRegisterWay registerWay();

    public abstract TLSErrInfo body();

    public static Builder builder() {
        return new AutoValue_AccountRegisterException.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder registerWay(AccountRegisterWay registerWay);

        public abstract Builder body(TLSErrInfo body);

        public abstract AccountRegisterException build();
    }
}
