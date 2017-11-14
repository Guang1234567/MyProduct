package com.example.myproduct.uisdk;

import android.content.Context;

import com.example.myproduct.lib.common.config.BuglyConfigAction;
import com.example.myproduct.lib.common.config.IConfigAction;
import com.example.myproduct.lib.common.config.XLogConfigAction;
import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.sdk.SdkManager;
import com.example.myproduct.sdk.model.core.AppCoreMgrSrv;
import com.google.auto.value.AutoValue;

import java.util.Map;

/**
 * SDK一切操作都是由通讯管理器 UiSdkManager 开始，SDK操作第一步需要获取 UiSdkManager 单例.
 *
 * @author lihanguang
 * @date 2017/4/8 11:45:46
 */
@AutoValue
public abstract class UiSdkManager extends BaseStartStopable implements IConfigAction {

    public abstract boolean isDisableXlog();

    public abstract boolean isDisableBuglyCrashReport();

    @Override
    protected void onStart(Context context) {

    }

    @Override
    protected void onStop(Context context) {
    }

    public static UiSdkManager.Builder builder() {
        return new UiSdkManager.Builder();
    }

    public static class Builder {

        private boolean mIsEnableXlog;
        private boolean mIsEnableBuglyCrashReport;

        private Map<String, String> mHeadsMap;

        private Builder() {
            mIsEnableXlog = true;
            mIsEnableBuglyCrashReport = true;
        }

        public Builder disableXlog() {
            mIsEnableXlog = false;
            return this;
        }

        public Builder disableBuglyCrashReport() {
            mIsEnableBuglyCrashReport = false;
            return this;
        }

        public Builder httpHeads(Map<String, String> headsMap){
            mHeadsMap = headsMap;
            return this;
        }

        public UiSdkManager build(Context context) {
            AppCoreMgrSrv appCoreMgrSrv = AppCoreMgrSrv.self();

            if (mIsEnableXlog) {
                appCoreMgrSrv.add(XLogConfigAction.create(
                        BuildConfig.EXTERNAL_DATA_DIR,
                        BuildConfig.IS_DEV_MODE.booleanValue()));
            }
            if (mIsEnableBuglyCrashReport) {
                appCoreMgrSrv.add(BuglyConfigAction.create(
                        BuildConfig.BUGLY_APP_ID,
                        BuildConfig.IS_DEV_MODE.booleanValue()));
            }

            appCoreMgrSrv
                    .add(SdkManager.builder()
                            .disableXlog()
                            .disableBuglyCrashReport()
                            .httpHeads(mHeadsMap)
                            .build(context));

            return new AutoValue_UiSdkManager(mIsEnableXlog, mIsEnableBuglyCrashReport);
        }
    }
}
