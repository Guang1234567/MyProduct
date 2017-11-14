package com.example.myproduct.lib.common_ui.utils.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.example.myproduct.lib.common_ui.R;

public class ResourceUtils {
    private static final String ANDROID_RESOURCES = "http://schemas.android.com/apk/res/android";

    /**
     * 获取"当前主题"中 dimension类型的attr
     */
    @TargetApi(14)
    public static int resolveDimensionPixelSize(Context context, @AttrRes int attr) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(attr, tv, true);
            result = context.getResources().getDimensionPixelSize(tv.resourceId);
        }
        return result;
    }

    /**
     * 获取"当前主题"中 color类型的attr
     */
    public static int resolveColor(Context context, @AttrRes int attr) {
        return resolveColor(context, attr, 0);
    }

    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    /**
     * 获取"当前主题"中 drawable类型的attr
     */
    public static Drawable resolveDrawable(Context context, @AttrRes int attr) {
        return resolveDrawable(context, attr, null);
    }

    public static Drawable resolveDrawable(Context context, @AttrRes int attr, Drawable fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            Drawable d = a.getDrawable(0);
            if (d == null && fallback != null)
                d = fallback;
            return d;
        } finally {
            a.recycle();
        }
    }

    /**
     * 获取 AttributeSet 里指定属性对应的 Drawable 资源.
     * 常用于获取 View(包括自定义View) 在 xml 里配置的相应属性对应的 Drawable 资源.
     *
     * @param context
     * @param set
     * @param attr         - 如一些返回 Drawable 的系统属性:
     *                     {@link android.R.attr#src   android:src},
     *                     {@link android.R.attr#background   android:background}
     *                     ...
     *                     <p/>
     *                     还有自定义View时自定义的 attr : R.styleable.CustomView_customAttr
     * @param defStyleAttr
     * @param defStyleRes
     * @return
     */
    public static Drawable resolveDrawable(Context context,
                                           AttributeSet set,
                                           @AttrRes int attr,
                                           int defStyleAttr,
                                           int defStyleRes) {
        return resolveDrawable(context, set, attr, defStyleAttr, defStyleRes, null);
    }

    public static Drawable resolveDrawable(Context context,
                                           AttributeSet set,
                                           @AttrRes int attr,
                                           int defStyleAttr,
                                           int defStyleRes,
                                           Drawable fallback) {
        final TypedArray a = context.obtainStyledAttributes(set, new int[]{attr}, defStyleAttr, defStyleRes);
        try {
            Drawable d = a.getDrawable(0);
            if (d == null && fallback != null)
                d = fallback;
            return d;
        } finally {
            a.recycle();
        }
    }

    /**
     * 获取"当前主题"中 dimension类型的attr
     */
    public static int resolveDimension(Context context, @AttrRes int attr) {
        return resolveDimension(context, attr, 0);
    }

    public static int resolveDimension(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getDimensionPixelSize(0, fallback);
        } finally {
            a.recycle();
        }
    }

    /**
     * 获取"当前主题"中 boolean类型的attr
     */
    public static boolean resolveBoolean(Context context, @AttrRes int attr) {
        return resolveBoolean(context, attr, false);
    }

    public static boolean resolveBoolean(Context context, @AttrRes int attr, boolean fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getBoolean(0, fallback);
        } finally {
            a.recycle();
        }
    }

    /**
     * 获取"当前主题"中 String类型的attr
     */
    public static String resolveString(Context context, @AttrRes int attr) {
        return resolveString(context, attr, null);
    }

    public static String resolveString(Context context, @AttrRes int attr, String fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            String str = a.getString(0);
            if (str == null && !TextUtils.isEmpty(fallback))
                str = fallback;
            return str;
        } finally {
            a.recycle();
        }
    }

    /**
     * 获取系统内部的 dimension resource.
     * <p>
     * you can search android internal dimension key in below repository.
     * https://github.com/android/platform_frameworks_base
     * https://android.googlesource.com/platform/frameworks/base.git
     */
    public static int getInternalDimensionSize(Context context, String key) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 根据 Material 设计要求, 一些 "Widget(包括自定义View) 或 其他界面元素"的默认颜色要以当前主题的
     * <code>
     * <item name="colorAccent">@color/color_accent</item>
     * </code>
     * 保持一致.
     *
     * @param context
     * @return
     */
    @ColorInt
    public static int getWidgetColor(Context context, @ColorRes int fallBackColor) {
        int widgetColor = resolveColor(context, R.attr.colorAccent, ContextCompat.getColor(context, fallBackColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            widgetColor = resolveColor(context, android.R.attr.colorAccent, widgetColor);
        }
        return widgetColor;
    }
}
