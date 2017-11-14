package com.example.myproduct.sdk.model.core;

import android.content.Context;

import com.example.myproduct.lib.common.config.IConfigAction;
import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.lib.common.core.StartStopGroup;
import com.example.myproduct.lib.common.utils.log.Log;
import com.example.myproduct.lib.common.utils.os.ProcessUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lihanguang
 * @date 2017/3/10 15:39:37
 */

public class AppCoreMgrSrv extends BaseStartStopable implements IAppMgrSrv {
    public static final String TAG = "AppCoreMgrSrv";

    private static volatile AppCoreMgrSrv sInst;

    public static AppCoreMgrSrv self() {
        if (sInst == null) {
            synchronized (AppCoreMgrSrv.class) {
                if (sInst == null) {
                    sInst = new AppCoreMgrSrv();
                }
            }
        }
        return sInst;
    }

    private StartStopGroup<AppCoreMgrSrv> mStartStopGroup;
    private Map<Class<? extends IConfigAction>, IConfigAction> mAppConfigActionMap;
    private Map<Class<? extends IAppMgrSrv>, IAppMgrSrv> mAppMgrSrvMap;

    private AppCoreMgrSrv() {
        mStartStopGroup = StartStopGroup.create(this);
        mAppConfigActionMap = new HashMap<>();
        mAppMgrSrvMap = new HashMap<>();
    }

    final public AppCoreMgrSrv add(IConfigAction action) {
        if (action != null
                && mAppConfigActionMap.get(action.getClass()) == null) {
            mAppConfigActionMap.put(action.getClass(), action);
            mStartStopGroup.add(action);
        }
        return this;
    }

    final public AppCoreMgrSrv add(IAppMgrSrv srv) {
        if (srv != null
                && mAppMgrSrvMap.get(srv.getClass()) == null) {
            mAppMgrSrvMap.put(srv.getClass(), srv);
            mStartStopGroup.add(srv);
        }
        return this;
    }

    @Override
    public void onStart(Context context) {
        Log.e(TAG, "--------------------------------------------------------------------------------------------");
        Log.w(TAG, new StringBuilder("进程启动 : ").append(ProcessUtils.getProcessName(android.os.Process.myPid())).append('(').append(android.os.Process.myPid()).append(')').toString());
        Log.w(TAG, "是否主进程 : " + ProcessUtils.isMainProcess(context));
        Log.e(TAG, "--------------------------------------------------------------------------------------------");

        mStartStopGroup.start(context);

        Log.flush(false);
    }

    @Override
    public void onStop(Context context) {
        mStartStopGroup.stop(context);

        Log.e(TAG, "--------------------------------------------------------------------------------------------");
        Log.w(TAG, new StringBuilder("进程销毁 : ").append(ProcessUtils.getProcessName(android.os.Process.myPid())).append('(').append(android.os.Process.myPid()).append(')').toString());
        Log.w(TAG, "是否主进程 : " + ProcessUtils.isMainProcess(context));
        Log.e(TAG, "--------------------------------------------------------------------------------------------");
    }

    public <T extends IAppMgrSrv> T getAppMgrSrv(Class<T> srvKey) {
        return (T) mAppMgrSrvMap.get(srvKey);
    }

    public <T extends IConfigAction> T getAppConfigAction(Class<T> actionKey) {
        return (T) mAppConfigActionMap.get(actionKey);
    }
}
