package com.example.myproduct.lib.common.utils.net.retrofit;

/**
 * @author lihanguang
 * @date 2017/7/6 17:37
 */

public class ApiException extends RuntimeException {
    private ApiErrorCode mErrorCode;

    public ApiException(ApiErrorCode code, String msg) {
        super(msg);
        this.mErrorCode = code;
    }

    public ApiException(ApiErrorCode code, String msg, Throwable cause) {
        super(msg, cause);
        mErrorCode = code;
    }

    public ApiErrorCode getErrorCode() {
        return mErrorCode;
    }

}
