package com.example.myproduct.lib.common.utils.net.okhttp.interceptor;

import com.example.myproduct.lib.common.utils.net.okhttp.progress.body.ProgressRequestBody;
import com.example.myproduct.lib.common.utils.net.okhttp.progress.listener.IProgressListener;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class ProgressRequestInterceptor extends ProgressInterceptor {

    public ProgressRequestInterceptor(IProgressListener progressListener) {
        super(progressListener);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null) {
            return chain.proceed(originalRequest);
        }

        ProgressRequestBody progressRequestBody = ProgressRequestBody.create(originalRequest.body(), getProgressListener());
        Request progressRequest = originalRequest.newBuilder()
                .put(progressRequestBody)
                .build();

        progressRequestBody.setOriginalRequest(originalRequest);
        progressRequestBody.setWrappedRequest(progressRequest);

        Response response = chain.proceed(progressRequest);
        return response;
    }
}
