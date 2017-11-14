package com.example.myproduct.lib.common.config;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.github.moduth.blockcanary.internal.BlockInfo;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lihanguang
 * @date 2017/3/30 13:21:30
 */

public class BlockCanaryConfigAction extends BaseStartStopable implements IConfigAction {

    private String mAppDataFolderName;

    private BlockCanaryConfigAction() {
        super();
    }

    public static BlockCanaryConfigAction create(String appDataFolderName) {
        BlockCanaryConfigAction action = new BlockCanaryConfigAction();
        action.mAppDataFolderName = appDataFolderName;
        return action;
    }

    @Override
    protected void onStart(Context context) {
        // 检测界面卡顿(UIThread 超16ms)的性能优化工具,
        // 可通过 {com.jiahe.gzb.AppBlockCanaryContext.provideBlockThreshold() 设置阈值(目前是200ms, 就是只要某个操作在 UIThread 上运行超过 200ms, 就被检测到).
        BlockCanary.install(context, new AppBlockCanaryContext(mAppDataFolderName)).start();
    }

    @Override
    protected void onStop(Context context) {

    }
}

class AppBlockCanaryContext extends BlockCanaryContext {
    private final static String TAG = "AppBlockCanaryContext";

    private String mAppDataFolderName;

    public AppBlockCanaryContext(String appDataFolderName) {
        mAppDataFolderName = appDataFolderName;
    }

    /**
     * Implement in your project.
     *
     * @return Qualifier which can specify this installation, like version + flavor.
     */
    public String provideQualifier() {
        return "unknown";
    }

    /**
     * Implement in your project.
     *
     * @return user id
     */
    public String provideUid() {
        return "uid";
    }

    /**
     * Network type
     *
     * @return {@link String} like 2G, 3G, 4G, wifi, etc.
     */
    public String provideNetworkType() {
        return "unknown";
    }

    /**
     * Config monitor duration, after this time BlockCanary will stop, use
     * with {@code BlockCanary}'s isMonitorDurationEnd
     *
     * @return monitor last duration (in hour)
     */
    public int provideMonitorDuration() {
        return -1;
    }

    /**
     * Config block threshold (in millis), dispatch over this duration is regarded as a BLOCK. You may set it
     * from performance of device.
     *
     * @return threshold in mills
     */
    public int provideBlockThreshold() {
        return 100;
    }

    /**
     * Thread stack dump interval, use when block happens, BlockCanary will dump on main thread
     * stack according to current sample cycle.
     * <p>
     * Because the implementation mechanism of Looper, real dump interval would be longer than
     * the period specified here (especially when cpu is busier).
     * </p>
     *
     * @return dump interval (in millis)
     */
    public int provideDumpInterval() {
        return provideBlockThreshold();
    }

    /**
     * Path to save log, like "/blockcanary/", will save to sdcard if can.
     *
     * @return path of log files
     */
    public String providePath() {
        StringBuilder sb = new StringBuilder();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sb.append(Environment.getExternalStorageDirectory());
        }*/

        if (!TextUtils.isEmpty(mAppDataFolderName)) {
            sb.append('/').append(mAppDataFolderName);
        }
        return sb.append("/blockcanary/").toString();
    }

    /**
     * If need notification to notice block.
     *
     * @return true if need, else if not need.
     */
    public boolean displayNotification() {
        return Log.isLoggable(TAG, Log.DEBUG); // adb shell setprop log.tag.AppBlockCanaryContext DEBUG 把性能检测工具在 Debug 版本上打开.
        //return true;
    }

    /**
     * Implement in your project, bundle files into a zip file.
     *
     * @param src  files before compress
     * @param dest files compressed
     * @return true if compression is successful
     */
    public boolean zip(File[] src, File dest) {
        return false;
    }

    /**
     * Implement in your project, bundled log files.
     *
     * @param zippedFile zipped file
     */
    public void upload(File zippedFile) {
        throw new UnsupportedOperationException();
    }


    /**
     * Packages that developer concern, by default it uses process name,
     * put high priority one in pre-order.
     *
     * @return null if simply concern only package with process name.
     */
    public List<String> concernPackages() {
        return null;
    }

    /**
     * Filter stack without any in concern package, used with @{code concernPackages}.
     *
     * @return true if filter, false it not.
     */
    public boolean filterNonConcernStack() {
        return false;
    }

    /**
     * Provide white list, entry in white list will not be shown in ui list.
     *
     * @return return null if you don't need white-list filter.
     */
    public List<String> provideWhiteList() {
        LinkedList<String> whiteList = new LinkedList<>();
        whiteList.add("org.chromium");
        return whiteList;
    }

    /**
     * Whether to delete files whose stack is in white list, used with white-list.
     *
     * @return true if delete, false it not.
     */
    public boolean deleteFilesInWhiteList() {
        return true;
    }

    /**
     * Block interceptor, developer may provide their own actions.
     */
    public void onBlock(Context context, BlockInfo blockInfo) {

    }
}
