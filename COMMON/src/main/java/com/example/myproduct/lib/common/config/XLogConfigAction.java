package com.example.myproduct.lib.common.config;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.lib.common.utils.log.Log;
import com.example.myproduct.lib.common.utils.os.ProcessUtils;

/**
 * @author lihanguang
 * @date 2017/3/19 16:45
 */

public class XLogConfigAction extends BaseStartStopable implements IConfigAction {

    private static final String TAG = "XLogConfigBootAction";

    private String mAppDataFolderName;
    private boolean mDebugMode;

    private String mLogPath;

    private XLogConfigAction() {
        super();
    }

    public static XLogConfigAction create(String appDataFolderName, boolean debugMode) {
        XLogConfigAction action = new XLogConfigAction();
        action.mAppDataFolderName = appDataFolderName;
        action.mDebugMode = debugMode;
        return action;
    }

    @Override
    protected void onStart(Context context) {
        final String appName = mAppDataFolderName;
        final String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        StringBuilder sb = new StringBuilder(sdcard);
        if (!TextUtils.isEmpty(appName)) {
            sb.append('/').append(appName);
        }
        sb.append("/mars_xlog");

        final String logPath = mLogPath = sb.toString();

        Log.open(logPath, mDebugMode);

        Log.e(TAG, "---------------------------------------------------------------------------------------------------------------------------------------------");
        Log.w(TAG, new StringBuilder("进程的 XLog 日志系统启动 : ")
                .append(ProcessUtils.getCurrentProcessName())
                .append('(').append(ProcessUtils.getCurrentProcessId()).append(')')
                .toString());
        Log.w(TAG, "日志存储本地路径 : " + logPath);
        Log.e(TAG, "---------------------------------------------------------------------------------------------------------------------------------------------");
    }

    @Override
    protected void onStop(Context context) {
        Log.e(TAG, "---------------------------------------------------------------------------------------------------------------------------------------------");
        Log.w(TAG, new StringBuilder("进程的 XLog 日志系统关闭 : ")
                .append(ProcessUtils.getCurrentProcessName())
                .append('(').append(ProcessUtils.getCurrentProcessId()).append(')')
                .toString());
        Log.w(TAG, "日志存储本地路径 : " + String.valueOf(mLogPath));
        Log.e(TAG, "---------------------------------------------------------------------------------------------------------------------------------------------");

        Log.close();
        mLogPath = null;
    }
}
