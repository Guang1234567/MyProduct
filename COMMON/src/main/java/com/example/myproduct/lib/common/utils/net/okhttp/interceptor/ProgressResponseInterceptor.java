package com.example.myproduct.lib.common.utils.net.okhttp.interceptor;

import com.example.myproduct.lib.common.utils.net.okhttp.progress.body.ProgressResponseBody;
import com.example.myproduct.lib.common.utils.net.okhttp.progress.listener.IProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ProgressResponseInterceptor extends ProgressInterceptor {

    public ProgressResponseInterceptor(IProgressListener progressListener) {
        super(progressListener);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);

        ProgressResponseBody progressResponseBody = ProgressResponseBody.create(originalResponse.body(), getProgressListener());
        Response progressResponse = originalResponse.newBuilder()
                .body(progressResponseBody)
                .build();

        progressResponseBody.setOriginalResponse(originalResponse);
        progressResponseBody.setWrappedResponse(progressResponse);

        return progressResponse;
    }
}
