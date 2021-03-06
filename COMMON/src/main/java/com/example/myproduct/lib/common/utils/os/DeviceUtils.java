package com.example.myproduct.lib.common.utils.os;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;

/**
 * 获取手机信息工具类<br>
 *
 * @author lihanguang
 * @date 16-6-30 上午11:07
 */
public class DeviceUtils {
    private static final String TAG = DeviceUtils.class.getSimpleName();

    private DeviceUtils() {

    }

    /**
     * 获取设备的系统版本号
     */
    public static int getSDKVersion() {
        int sdk = Build.VERSION.SDK_INT;
        return sdk;
    }

    /**
     * Gets os version.
     *
     * @return the os version
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Gets build id.
     *
     * @return the build id
     */
    public static String getBuildID() {
        return Build.ID;
    }

    /**
     * 获取设备的型号
     */
    public static String getModel() {
        String model = Build.MODEL;
        return model;
    }

    /**
     * 获取设备的型号
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * Gets serial.
     *
     * @return the serial
     */
    public static String getSerial() {
        String serial = Build.SERIAL;
        return serial;
    }

    /**
     * Gets device.
     *
     * @return the device
     */
    public static String getDevice() {
        String device = Build.DEVICE;
        return device;
    }

    /**
     * Gets getManufacturer.
     *
     * @return the Manufacturer
     */
    public static String getManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        return manufacturer;
    }

    /**
     * Gets string supported abis.
     *
     * @return the string supported abis
     */
    public static String getStringSupportedABIS() {
        String result = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] supportedABIS = Build.SUPPORTED_ABIS;
            StringBuilder supportedABIString = new StringBuilder();
            if (supportedABIS.length > 0) {
                for (String abis : supportedABIS) {
                    supportedABIString.append(abis).append(" ");
                }
                supportedABIString.deleteCharAt(supportedABIString.lastIndexOf(" "));
            } else {
                supportedABIString.append("");
            }
            result = supportedABIString.toString();
        }
        return TextUtils.isEmpty(result) ? "null" : result;
    }

    public static String getDeviceInfo(Context appContext) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("APILevel: ").append(getSDKVersion()).append("\n");
        stringBuilder.append("BuildID: ").append(getBuildID()).append("\n");
        stringBuilder.append("OSVersion: ").append(getOSVersion()).append("\n");
        stringBuilder.append("Model: ").append(getModel()).append("\n");
        stringBuilder.append("Product: ").append(getProduct()).append("\n");
        stringBuilder.append("Device: ").append(getDevice()).append("\n");
        stringBuilder.append("Serial: ").append(getSerial()).append("\n");
        stringBuilder.append("SupportedABIS: ").append(getStringSupportedABIS()).append("\n");
        stringBuilder.append("Connected: ").append(NetWorkUtils.isConnectedByState(appContext)).append("\n");
        stringBuilder.append("NetworkType: ").append(NetWorkUtils.getNetworkType(appContext)).append("");

        return stringBuilder.toString();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isCharging(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent == null) {
            // should not happen
            return false;
        }

        // 0 is on battery
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);
    }

    @SuppressWarnings("deprecation")
    public static boolean isIdle(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*
             * isDeviceIdleMode() is a very strong requirement and could cause a job
             * to be never run. isDeviceIdleMode() returns true in doze mode, but jobs
             * are delayed until the device leaves doze mode
             */
            return powerManager.isDeviceIdleMode() || !powerManager.isInteractive();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return !powerManager.isInteractive();
        } else {
            return !powerManager.isScreenOn();
        }
    }

    public static boolean checkIfPowerSaverModeEnabled(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                return pm.isPowerSaveMode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}