package com.example.myproduct.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.example.myproduct.app.model.WatchDogMgrSrv;
import com.example.myproduct.lib.common.config.BlockCanaryConfigAction;
import com.example.myproduct.lib.common.config.BuglyConfigAction;
import com.example.myproduct.lib.common.config.LeakCanaryConfigAction;
import com.example.myproduct.lib.common.config.ThreadConfigAction;
import com.example.myproduct.lib.common.config.XLogConfigAction;
import com.example.myproduct.lib.common.core.StartStopGroup;
import com.example.myproduct.lib.common.utils.log.Log;
import com.example.myproduct.lib.common.utils.os.ProcessUtils;
import com.example.myproduct.lib.common.utils.rx.lifecycle.RxBaseApplication;
import com.example.myproduct.lib.common.utils.rx.os.RxShutdownHook;
import com.example.myproduct.sdk.model.core.AppCoreMgrSrv;
import com.example.myproduct.uisdk.UiSdkManager;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @author lihanguang
 * @date 2017/3/10 10:36:39
 */

public class MyProductApplication extends RxBaseApplication {

    public static final String TAG = "MyProductApplication";

    private static volatile MyProductApplication sInst;

    private StartStopGroup mPlatformConfigs;
    private AppCoreMgrSrv mAppCoreMgrSrv;

    final public static MyProductApplication getInstance() {
        return sInst;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInst = this;

        // 平台基础配置
        mPlatformConfigs = StartStopGroup.create(this);
        mPlatformConfigs
                // 日志系统
                .add(XLogConfigAction.create(BuildConfig.EXTERNAL_DATA_DIR, BuildConfig.IS_DEV_MODE))
                // app 核心工作线程
                .add(ThreadConfigAction.create())
                .start(getApplicationContext());


        // 业务核心服务(运行在主进程)
        mAppCoreMgrSrv = createAppCoreMgrSrv();
    }

    protected final void terminateAppSafe() {
        Log.w(TAG, "# terminateAppSafe");
        super.terminateAppSafe();
        try {
            stopBusiness(getApplicationContext());
        } finally {
            mPlatformConfigs.stop(getApplicationContext());
        }
    }

    private AppCoreMgrSrv createAppCoreMgrSrv() {
        // 第二步: 开启AppCoreMgrSrv

        RxShutdownHook.onHook()
                .doOnNext(new Consumer<Thread>() {
                    @Override
                    public void accept(Thread thread) throws Exception {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                /*ProgressDialog dialog = new ProgressDialog(MyProductApplication.this);
                                dialog.setIndeterminate(true);
                                dialog.setTitle("关闭程序");
                                dialog.setMessage("关闭中...");
                                dialog.show();*/
                            }
                        });
                    }
                })
                .doOnNext(new Consumer<Thread>() {
                    @Override
                    public void accept(Thread thread) throws Exception {
                        terminateAppSafe();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .subscribe();

        final WatchDogMgrSrv watchDogMgrSrv = WatchDogMgrSrv.create()
                .add(new WatchDogMgrSrv.MultiProcessUncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(final int processId, final String processName, final Thread t, final Throwable e) {
                        Log.e(TAG, "--------------------------------------------------------------------------------------------");
                        Log.w(TAG, new StringBuilder("进程意外关闭 : ").append(processName).append('(').append(processId).append(')').toString());
                        Log.w(TAG, new StringBuilder("发生崩溃异常的线程 : ").append(t).toString());
                        Log.w(TAG, new StringBuilder("崩溃异常的堆栈信息 : ").toString(), e);
                        Log.e(TAG, "--------------------------------------------------------------------------------------------");

                        // 重启到你想要的界面
                        /*Intent nextIntent = new Intent(getApplicationContext(), MainActivity.class);
                        nextIntent.putExtra("is_phoenix", true);
                        ProcessPhoenix.triggerRebirth(getApplicationContext(), nextIntent);*/

                        System.exit(0xABC);// 0表示jvm正常退出，非0数可以做错误码 (0xABC == 2748 == 101010111100(binary))
                    }
                });

        Map<String, String> headsMap = new HashMap<>();
        headsMap.put("paltform", "android");
        headsMap.put("version", BuildConfig.APP_BUILD_INFO);

        return AppCoreMgrSrv.self()
                .add(BuglyConfigAction.create(BuildConfig.BUGLY_APP_ID, BuildConfig.IS_DEV_MODE))
                .add(BlockCanaryConfigAction.create(BuildConfig.EXTERNAL_DATA_DIR))
                .add(LeakCanaryConfigAction.create())
                .add(watchDogMgrSrv)
                .add(
                        UiSdkManager.builder()
                                .disableXlog()
                                .disableBuglyCrashReport()
                                .httpHeads(headsMap)
                                .build(this)
                );
    }

    final public void startBusiness(Context context) {
        // 排除某些进程
        if (ProcessPhoenix.isPhoenixProcess(context)) {
            return;
        }

        if (ProcessUtils.isMainProcess(getApplicationContext())) { // 判断是否主进程
            //启动业务核心服务
            mAppCoreMgrSrv.start(context);
        }
    }

    final public void stopBusiness(Context context) {
        mAppCoreMgrSrv.stop(context);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        terminateAppSafe();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
