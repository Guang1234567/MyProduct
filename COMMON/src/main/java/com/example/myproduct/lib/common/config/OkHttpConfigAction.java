package com.example.myproduct.lib.common.config;

import android.content.Context;

import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.lib.common.utils.net.okhttp.OkHttpClientGenerator;

import java.util.Map;

/**
 * @author lihanguang
 * @date 2017/3/17 18:52:13
 */

public class OkHttpConfigAction extends BaseStartStopable implements IConfigAction {

    private Map<String, String> mHeadsMap;

    private OkHttpConfigAction(Map<String, String> headsMap) {
        super();
        mHeadsMap = headsMap;
    }

    public static OkHttpConfigAction create(Map<String, String> headsMap) {
        return new OkHttpConfigAction(headsMap);
    }

    @Override
    protected void onStart(Context context) {
        OkHttpClientGenerator.install(context, mHeadsMap);
    }

    @Override
    protected void onStop(Context context) {
        OkHttpClientGenerator.uninstall(context);
    }
}
