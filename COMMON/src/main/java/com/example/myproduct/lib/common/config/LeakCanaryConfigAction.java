package com.example.myproduct.lib.common.config;

import android.app.Application;
import android.content.Context;

import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.squareup.leakcanary.LeakCanary;

/**
 * @author lihanguang
 * @date 2017/4/5 13:45:03
 */

public class LeakCanaryConfigAction extends BaseStartStopable implements IConfigAction {

    private LeakCanaryConfigAction() {

    }

    public static LeakCanaryConfigAction create() {
        return new LeakCanaryConfigAction();
    }

    @Override
    protected void onStart(Context context) {
        if (LeakCanary.isInAnalyzerProcess(context)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not install your app in this process.
            return;
        }
        LeakCanary.install((Application) context.getApplicationContext());
    }

    @Override
    protected void onStop(Context context) {
    }
}
