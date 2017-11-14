package com.example.myproduct.lib.common.core;

import android.content.Context;

/**
 * @author lihanguang
 * @date 2017/3/20 18:56:06
 */

public interface IStartStopable {

    void start(Context context);

    void stop(Context context);

    String getLogTag();
}
