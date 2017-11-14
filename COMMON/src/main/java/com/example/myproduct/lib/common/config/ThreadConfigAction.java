package com.example.myproduct.lib.common.config;

import android.content.Context;

import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.lib.common.utils.thread.ThreadUtils;

/**
 * @author lihanguang
 * @date 2017/5/27 13:40:54
 */

public class ThreadConfigAction extends BaseStartStopable implements IConfigAction {

    private ThreadConfigAction() {

    }

    public static ThreadConfigAction create() {
        return new ThreadConfigAction();
    }

    @Override
    protected void onStart(Context context) {
        ThreadUtils.start();
    }

    @Override
    protected void onStop(Context context) {
        ThreadUtils.stop();
    }
}
