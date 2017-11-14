/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myproduct.lib.common.utils.db;

import android.content.Context;
import android.database.Cursor;

import com.example.myproduct.lib.common.utils.log.Log;

import java.util.Date;

import io.reactivex.functions.Consumer;

/**
 * {@link android.database.DatabaseUtils} 的补充.
 */
public final class DbUtils {
    public static final String TAG = "DbUtils";

    public static final int BOOLEAN_FALSE = 0;
    public static final int BOOLEAN_TRUE = 1;

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    public static boolean getBoolean(Cursor cursor, String columnName) {
        return getInt(cursor, columnName) == BOOLEAN_TRUE;
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    public static Date getDate(Cursor cursor, String columnName) {
        long timestamp = getLong(cursor, columnName);
        return new Date(timestamp);
    }

    /**
     * Closes {@code cursor}, ignoring any checked exceptions. Does nothing if {@code cursor} is
     * null.
     */
    public static final void closeQuietly(Cursor cursor) {
        if (cursor != null
                && !cursor.isClosed()) {
            try {
                cursor.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
                Log.e(TAG, "closeQuietly : ", ignored);
            }
        }
    }

    public boolean deleteDatabase(Context context, String dbName) {
        return context.getApplicationContext().deleteDatabase(dbName);
    }

    /**
     * 遍历 Cursor
     * <p>
     * {@link android.database.DatabaseUtils#dumpCursor(android.database.Cursor, java.lang.StringBuilder)}
     *
     * @param cursor
     * @param consumer
     */
    public static final void forEachCursorRow(Cursor cursor, Consumer<Cursor> consumer) throws Exception {
        if (consumer != null
                && cursor != null
                && !cursor.isClosed()
                && cursor.getCount() > 0) {
            int startPos = cursor.getPosition(); // 记录当前的位置

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                consumer.accept(cursor);
            }

            cursor.moveToPosition(startPos); // 恢复到之前的位置
        }
    }
}
