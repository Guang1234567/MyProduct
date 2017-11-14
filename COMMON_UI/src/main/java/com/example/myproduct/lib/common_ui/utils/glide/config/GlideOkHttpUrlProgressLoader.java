package com.example.myproduct.lib.common_ui.utils.glide.config;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.model.stream.StreamStringLoader;
import com.bumptech.glide.load.model.stream.StreamUriLoader;
import com.example.myproduct.lib.common.utils.net.okhttp.OkHttpClientGenerator;
import com.example.myproduct.lib.common.utils.net.okhttp.progress.listener.IProgressListener;

import java.io.InputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * 监听 Glide加载网络图片的进度.
 * <p>
 * Usage:
 * Glide
 * .with(this)
 * .using(GlideOkHttpUrlProgressLoader.create(progressListener))
 * .load(urlToCheck)
 * .into(imageView);
 *
 * @author lihanguang
 * @date 2016/4/27 11:20:12
 */
public class GlideOkHttpUrlProgressLoader implements StreamModelLoader<GlideUrl> {
    private IProgressListener mProgressListener;

    private GlideOkHttpUrlProgressLoader(IProgressListener listener) {
        mProgressListener = listener;
    }

    public static GlideOkHttpUrlProgressLoader create(IProgressListener listener) {
        GlideOkHttpUrlProgressLoader l = new GlideOkHttpUrlProgressLoader(listener);
        return l;
    }

    public StreamStringLoader asStreamStringLoader(Context context) {
        return new StreamStringLoader(new StreamUriLoader(context, this));
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(GlideUrl model, int width, int height) {
        OkHttpClient client;
        HttpUrl parsed = HttpUrl.parse(model.toStringUrl());
        if (parsed != null
                && parsed.isHttps()) {
            client = OkHttpClientGenerator.createProgressResponseHttpsClient(mProgressListener);
        } else {
            client = OkHttpClientGenerator.createProgressResponseHttpClient(mProgressListener);
        }
        return new GlideOkHttpStreamFetcher(client, model);
    }
}
