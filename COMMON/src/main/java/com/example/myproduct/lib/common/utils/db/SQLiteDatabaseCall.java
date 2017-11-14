package com.example.myproduct.lib.common.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import java.io.Closeable;

/**
 * 一个拦截所有数据库异常, 并把异常转化为错误码返回的数据库操作封装类.
 *
 * @author lihanguang
 * @date 2017/9/12 16:48
 */

public interface SQLiteDatabaseCall extends Closeable {
    String TAG = "SQLiteDatabaseCall";

    boolean isClosed();

    void close();

    void beginTransaction();

    void setTransactionSuccessful();

    void endTransaction();

    Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy);

    Cursor rawQuery(String sql, String... selectionArgs);

    long insert(String table, String nullColumnHack, ContentValues values);

    long insertWithOnConflict(String table, String nullColumnHack,
                              ContentValues initialValues, int conflictAlgorithm);

    long replace(String table, String nullColumnHack, ContentValues initialValues);

    int update(String table, ContentValues values, String whereClause, String[] whereArgs);

    int updateWithOnConflict(String table, ContentValues values,
                             String whereClause, String[] whereArgs, int conflictAlgorithm);

    int delete(String table, String whereClause, String[] whereArgs);

    void execSQL(String sql) throws SQLException;

    boolean isReadOnly();
}
