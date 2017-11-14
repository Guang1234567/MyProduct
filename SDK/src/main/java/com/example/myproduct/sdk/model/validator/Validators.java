package com.example.myproduct.sdk.model.validator;

import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author lihanguang
 * @date 2017/3/17 13:08:33
 */

public class Validators {

    public static Observable<ValidationResult<String>> accountValidator(final String account) {
        return Observable.just(account)
                .map(new Function<String, ValidationResult<String>>() {
                    @Override
                    public ValidationResult<String> apply(String account) throws Exception {
                        ValidationResult<String> result = ValidationResult.createYes();
                        if (!TextUtils.isEmpty(account)) {
                            result = ValidationResult.<String>createNo("IS EMPTY");
                        }
                        return result;
                    }
                });
    }

    public static Observable<ValidationResult<String>> passwordValidator(final String password) {
        return Observable.just(password)
                .map(new Function<String, ValidationResult<String>>() {
                    @Override
                    public ValidationResult<String> apply(String password) throws Exception {
                        ValidationResult<String> result = ValidationResult.createYes();
                        if (!TextUtils.isEmpty(password)) {
                            result = ValidationResult.<String>createNo("IS EMPTY");
                        }
                        return result;
                    }
                });
    }

}
