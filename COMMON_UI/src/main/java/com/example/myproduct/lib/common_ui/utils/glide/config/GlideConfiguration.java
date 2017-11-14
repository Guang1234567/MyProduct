package com.example.myproduct.lib.common_ui.utils.glide.config;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.myproduct.lib.common_ui.R;
import com.example.myproduct.lib.common.utils.log.Log;

import java.io.File;
import java.io.InputStream;

public class GlideConfiguration implements GlideModule {

    private static final String TAG = "GlideConfiguration";

    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);

        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                return createDiskCache(context);
            }
        });

        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        builder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize));
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        builder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize));

        ViewTarget.setTagId(R.id.common_ui_tag_id_glide);
    }


    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
        glide.register(GlideUrl.class, InputStream.class, new GlideOkHttpUrlLoader.Factory());
    }

    private DiskCache createDiskCache(Context context) {
        // Careful: the external cache directory doesn't enforce permissions
        File cacheLocation = new File(context.getExternalCacheDir(), "glide_cache");
        if (!cacheLocation.exists()) {
            cacheLocation.mkdirs();
            Log.d(TAG, "applyOptions mkdirs " + cacheLocation.getPath());
        }
        return DiskLruCacheWrapper.get(cacheLocation, 500 * 1024 * 1024);
    }
}