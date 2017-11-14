package com.example.myproduct.lib.common.utils.net.okhttp.progress.listener;

import okhttp3.Request;
import okhttp3.Response;

public interface IProgressListener {
    void update(long bytesReadOrWrite,
                long contentLength,
                boolean done);
}
