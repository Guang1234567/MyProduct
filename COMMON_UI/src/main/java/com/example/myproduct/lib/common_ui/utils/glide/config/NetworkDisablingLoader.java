package com.example.myproduct.lib.common_ui.utils.glide.config;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * "禁止从网络加载(只能从本地缓存加载)"的Loader, 与Glide配套使用.
 *
 * Usage:
 *
 *  String url3G = "http://placehold.it/640x480?text=3g";
 *  String urlWifi = "http://placehold.it/1920x1080?text=wifi";
 *
 *  DrawableRequestBuilder<String> load3G;
 *  DrawableRequestBuilder<String> loadWifi;
 *
 *  if (NetworkUtils.isOnSlowNetwork()) { // 低速网络环境?
 *      load3G = Glide.with(this).load(url3G);
 *      loadWifi = Glide.with(this).using(new NetworkDisablingLoader()).load(urlWifi);
 *  } else {
 *      load3G = Glide.with(this).using(new NetworkDisablingLoader()).load(url3G);
 *      loadWifi = Glide.with(this).load(urlWifi);
 *  }
 *
 *  // set up loads
 *  load3G
 *  .diskCacheStrategy(DiskCacheStrategy.ALL) // SOURCE is enough too
 *  .centerCrop() // just to show that you can add different parameters to loads
 *  ...;
 *
 *  loadWifi
 *  .diskCacheStrategy(DiskCacheStrategy.ALL) // SOURCE is enough too
 *  .transform(new CircleTransform(context)) // just to show that you can add different parameters to loads
 *  ...;
 *
 *  // do the actual load
 *  loadWifi.thumbnail(load3G).into(imageView);
 *
 * @author lihanguang
 * @date 2016/4/27 10:37:40
 */
public class NetworkDisablingLoader implements StreamModelLoader<String> {
    @Override
    public DataFetcher<InputStream> getResourceFetcher(final String model, int width, int height) {
        return new DataFetcher<InputStream>() {
            @Override
            public InputStream loadData(Priority priority) throws Exception {
                throw new IOException("Forced Glide network failure");
            }

            @Override
            public void cleanup() {
            }

            @Override
            public String getId() {
                return model;
            }

            @Override
            public void cancel() {
            }
        };
    }
}
