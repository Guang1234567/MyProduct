package com.example.myproduct.sdk;

import android.content.Context;

import com.example.myproduct.lib.common.config.BuglyConfigAction;
import com.example.myproduct.lib.common.config.IConfigAction;
import com.example.myproduct.lib.common.config.OkHttpConfigAction;
import com.example.myproduct.lib.common.config.XLogConfigAction;
import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.sdk.model.core.AppCoreMgrSrv;
import com.example.myproduct.sdk.model.login.LoginMgrSrv;
import com.example.myproduct.sdk.proguard.config.TIMConfigAction;
import com.example.myproduct.sdk.proguard.config.TLSConfigAction;
import com.google.auto.value.AutoValue;

import java.util.Map;

/**
 * SDK一切操作都是由通讯管理器 SdkManager 开始，SDK操作第一步需要获取 SdkManager 单例.
 *
 * @author lihanguang
 * @date 2017/4/8 11:45:46
 */
@AutoValue
public abstract class SdkManager extends BaseStartStopable implements IConfigAction {

    public abstract boolean isDisableXlog();

    public abstract boolean isDisableBuglyCrashReport();

    @Override
    protected void onStart(Context context) {

    }

    @Override
    protected void onStop(Context context) {
    }

    public static Builder builder() {
        return new Builder();
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

        public SdkManager build(Context context) {
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
                    .add(OkHttpConfigAction.create(mHeadsMap))
                    .add(TIMConfigAction.create())
                    .add(TLSConfigAction.create())
                    .add(LoginMgrSrv.create());

            return new AutoValue_SdkManager(mIsEnableXlog, mIsEnableBuglyCrashReport);
        }
    }
}