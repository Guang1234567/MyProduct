package com.example.myproduct.lib.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.myproduct.lib.common.utils.log.Log;

/**
 * @author lihanguang
 * @date 2017/4/14 16:26:58
 */

public class AppUtils {

    private final static String TAG = "AppUtils";

    /**
     * 获取单个App图标
     **/
    public static Drawable getAppIcon(Context context) {
        PackageManager pm = context.getPackageManager();
        Drawable icon = pm.getApplicationIcon(context.getApplicationInfo());
        return icon;
    }

    /**
     * 获取单个App名称
     **/
    public static CharSequence getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        CharSequence appName = pm.getApplicationLabel(context.getApplicationInfo());
        return appName;
    }

    /**
     * 获取单个App版本号
     **/
    public static String getAppVersion(Context context, String fallBack) {
        String result = fallBack;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            result = packageInfo.versionName;
        } catch (Throwable e) {
            Log.e(TAG, "#getAppVersion : ", e);
        }
        return result;
    }

    /**
     * 获取单个App的所有权限
     **/
    public static String[] getAppPermission(Context context, String[] fallBack) {
        String[] result = fallBack;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            result = packageInfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "#getAppPermission : ", e);
        }
        return result;
    }

    /**
     * 获取单个App的签名
     **/
    public static String getAppSignature(Context context, String fallBack) {
        String result = fallBack;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            result = packageInfo.signatures[0].toCharsString();
        } catch (Throwable e) {
            Log.e(TAG, "#getAppSignature : ", e);
        }
        return result;
    }
}
