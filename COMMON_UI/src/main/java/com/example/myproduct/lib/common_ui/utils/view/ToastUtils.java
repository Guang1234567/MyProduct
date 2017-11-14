package com.example.myproduct.lib.common_ui.utils.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.example.myproduct.lib.common.utils.Utils;

/**
 * Toast的工具类
 *
 * @author lihanguang
 * @date 2016/02/23下午1:47
 */
public class ToastUtils {

    /**
     * 判断当前线程，合理显示Toast
     *
     * @param context  上下文
     * @param resStrId 字符串的id
     * @param duration How long to display the message.  Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void show(final Context context, @StringRes final int resStrId, final int duration) {
        String content = context.getResources().getString(resStrId);
        show(context, content, duration);
    }

    /**
     * 判断当前线程，合理显示Toast
     *
     * @param context  上下文
     * @param content  显示的内容
     * @param duration How long to display the message.  Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void show(final Context context, @NonNull final CharSequence content, final int duration) {
        // 判断是否为主线程
        if (Utils.isOnMainThread()) {
            Toast.makeText(context, content, duration).show();
        } else {
            // 如果不是，就用该方法使其在ui线程中运行
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, content, duration).show();
                }
            });
        }
    }
}
