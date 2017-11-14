package com.example.myproduct.lib.common_ui.utils.glide.config;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.example.myproduct.lib.common.utils.net.okhttp.OkHttpClientGenerator;

import java.io.InputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class GlideOkHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    /**
     * The default factory for {@link GlideOkHttpUrlLoader}s.
     */
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private OkHttpClient client;
        private OkHttpClient httpsClient;

        /**
         * Constructor for a new Factory that runs requests using a static singleton client.
         */
        public Factory() {
            this(OkHttpClientGenerator.getCacheOkHttpClient(),
                    OkHttpClientGenerator.getCacheOkHttpsClient());
        }

        /**
         * Constructor for a new Factory that runs requests using given client.
         */
        public Factory(OkHttpClient client, OkHttpClient httpsClient) {
            this.client = client;
            this.httpsClient = httpsClient;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new GlideOkHttpUrlLoader(client, httpsClient);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }

    private final OkHttpClient mClient;
    private final OkHttpClient mHttpsClient;

    public GlideOkHttpUrlLoader(OkHttpClient client, OkHttpClient httpsClient) {
        mClient = client;
        mHttpsClient = httpsClient;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(GlideUrl model, int width, int height) {
        OkHttpClient client;
        HttpUrl parsed = HttpUrl.parse(model.toStringUrl());
        if (parsed != null
                && parsed.isHttps()) {
            client = mHttpsClient;
        } else {
            client = mClient;
        }
        return new GlideOkHttpStreamFetcher(client, model);
    }
}
