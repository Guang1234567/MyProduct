package com.example.myproduct.sdk.model.validator;

/**
 * @author lihanguang
 * @date 2017/3/17 11:08:02
 */

public class ValidationResult<T> {

    private boolean mIsValid;
    private String mReason;

    private ValidationResult() {
        mIsValid = false;
    }

    public static <R> ValidationResult<R> createYes() {
        ValidationResult<R> result = new ValidationResult<>();
        result.mIsValid = true;
        return result;
    }

    public static <R> ValidationResult<R> createNo(String reason) {
        ValidationResult<R> result = new ValidationResult<>();
        result.mIsValid = false;
        result.mReason = reason;
        return result;
    }

    public boolean isValid() {
        return mIsValid;
    }

    public String getReason() {
        return mReason;
    }
}
