package com.example.myproduct.lib.common.utils;

import android.database.Cursor;
import android.os.Looper;

import com.example.myproduct.lib.common.utils.db.DbUtils;

import java.io.Closeable;
import java.net.ServerSocket;
import java.net.Socket;

import okhttp3.internal.Util;

/**
 *  java 工具方法集合(太常用了)
 *
 * @author hg-li
 * @date 16-6-30 上午11:07
 */
public class Utils {

    /** Returns true if two possibly-null objects are equal. */
    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    /**
     * Returns {@code true} if called on the main thread, {@code false} otherwise.
     */
    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * Returns {@code true} if called on the main thread, {@code false} otherwise.
     */
    public static boolean isOnBackgroundThread() {
        return !isOnMainThread();
    }

    /**
     * Closes {@code closeable}, ignoring any checked exceptions. Does nothing if {@code closeable} is
     * null.
     */
    public static void closeQuietly(Closeable closeable) {
        Util.closeQuietly(closeable);
    }

    public static void closeQuietly(Socket socket) {
        Util.closeQuietly(socket);
    }

    public static void closeQuietly(ServerSocket serverSocket) {
        Util.closeQuietly(serverSocket);
    }

    public static void closeQuietly(Cursor cursor) {
        DbUtils.closeQuietly(cursor);
    }
}
