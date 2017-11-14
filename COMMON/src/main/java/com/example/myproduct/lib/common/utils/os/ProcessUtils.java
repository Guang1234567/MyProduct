package com.example.myproduct.lib.common.utils.os;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.bugly.crashreport.BuglyLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author lihanguang
 * @date 2017/3/10 11:08:54
 */

public class ProcessUtils {
    private static final String TAG = "ProcessUtils";

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            BuglyLog.e(TAG, "#getProcessName", throwable);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                BuglyLog.e(TAG, "#getProcessName", exception);
            }
        }
        return null;
    }

    public static String getCurrentProcessName() {
        return getProcessName(getCurrentProcessId());
    }

    public static boolean isMainProcess(Context context){
        String mainProcessName = context.getPackageName();
        String processName = ProcessUtils.getProcessName(android.os.Process.myPid());
        return processName == null || processName.equals(mainProcessName);
    }

    public static int getCurrentProcessId() {
        return android.os.Process.myPid();
    }
}
