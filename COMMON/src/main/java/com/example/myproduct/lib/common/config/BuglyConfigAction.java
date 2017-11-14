package com.example.myproduct.lib.common.config;

import android.content.Context;

import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.lib.common.utils.os.ProcessUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bugly 的配置.
 * <p>
 * 教程
 * https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20170307182353
 * https://bugly.qq.com/docs/user-guide/advance-features-android/
 *
 * @author lihanguang
 * @date 2017/3/10 11:14:23
 */

public class BuglyConfigAction extends BaseStartStopable implements IConfigAction {
    private String mBuglyAppId;
    private boolean mBuglyDebugMode;

    private String mCurUserId;

    private BuglyConfigAction(String buglyAppId, boolean buglyDebugMode) {
        super();

        mBuglyAppId = buglyAppId;
        mBuglyDebugMode = buglyDebugMode;

        mCurUserId = "";
    }

    public static BuglyConfigAction create(String buglyAppId, boolean buglyDebugMode) {
        return new BuglyConfigAction(buglyAppId, buglyDebugMode);
    }

    protected void onStart(Context context) {
        //在开发测试阶段，可以在初始化Bugly之前通过以下接口把调试设备设置成“开发设备”。
        CrashReport.setIsDevelopmentDevice(context, mBuglyDebugMode);

        // 获取当前包名
        String mainProcessName = context.getPackageName();
        // 获取当前进程名
        String processName = ProcessUtils.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(mainProcessName)); // 只在主进程上报


        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                          String errorMessage, String errorStack) {
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                map.put("Key", "Value");
                return map;
            }

            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType,
                                                           String errorMessage, String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }

        });


        CrashReport.initCrashReport(context,
                mBuglyAppId,
                mBuglyDebugMode,
                strategy);
    }

    @Override
    protected void onStop(Context context) {
        changeUserId("UserId_logout");
    }

    public String getBuglyAppId() {
        return mBuglyAppId;
    }

    public boolean isBuglyDebugMode() {
        return mBuglyDebugMode;
    }

    public String getCurUserId() {
        return mBuglyAppId;
    }

    public void changeUserId(String newUserId) {
        if (mCurUserId != newUserId) {
            mCurUserId = newUserId;
            CrashReport.setUserId(newUserId);
        }
    }
}
