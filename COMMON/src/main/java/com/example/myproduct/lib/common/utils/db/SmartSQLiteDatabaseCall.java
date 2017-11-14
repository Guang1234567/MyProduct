package com.example.myproduct.lib.common.utils.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.myproduct.lib.common.utils.log.Log;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * 一个拦截所有数据库异常, 并把异常转化为错误码返回的数据库操作封装类.
 * (非加密版本)
 *
 * @author lihanguang
 * @date 2017/9/12 16:48
 */

public class SmartSQLiteDatabaseCall implements SQLiteDatabaseCall {
    private static final String TAG = "SmartSQLiteDatabaseCall";

    private static final List<SmartSQLiteDatabaseCall> sCache = new LinkedList<>();

    private SQLiteDatabaseManager mSQLiteDatabaseManager;
    private SQLiteDatabaseCall mSrc;
    private boolean mIsClosed;

    private Throwable mInitWhere;

    private SmartSQLiteDatabaseCall() {
        mIsClosed = true;
    }

    private static boolean returnBack(SmartSQLiteDatabaseCall oldCall) {
        if (!sCache.contains(oldCall)) {
            return sCache.add(oldCall);
        } else {
            return false;
        }
    }

    private static SmartSQLiteDatabaseCall borrow() {
        SmartSQLiteDatabaseCall result = null;
        if (sCache.isEmpty()) {
            result = new SmartSQLiteDatabaseCall();
        } else {
            result = sCache.remove(0);
        }
        return result;
    }

    public static synchronized void clearCachePool() {
        sCache.clear();
    }

    static synchronized SmartSQLiteDatabaseCall obtain(SQLiteDatabaseManager mgr) {
        SmartSQLiteDatabaseCall call = borrow();
        call.mSQLiteDatabaseManager = mgr;
        call.mSrc = mgr.openDatabase();
        call.mIsClosed = false;
        call.mInitWhere = new IllegalStateException();
        return call;
    }

    public synchronized boolean isClosed() {
        return mIsClosed;
    }

    private void onClose() {
        mSQLiteDatabaseManager.closeDatabase();
        mSQLiteDatabaseManager = null;
        mSrc = null;
        mIsClosed = true;
    }

    /**
     * 主动式 close, 并将当前对象缓存以达到提升性能的目的.
     * <p>
     * 注意:
     * 由于主动 close 会将当前对象缓存到内存, 故不会触发调用 {@link SmartSQLiteDatabaseCall#finalize()} (互斥关系)
     */
    @Override
    public synchronized void close() {
        if (!isClosed()) {
            mInitWhere = null;
            onClose();
            returnBack(this); //将被回收的对象缓存起来, 达到复用目的
        }
    }

    /**
     * 被动式 close, 应付"忘记主动 close"这一问题.
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        if (!isClosed()) {
            if (mInitWhere != null) {
                Log.w(TAG, "Forget close the SmartSQLiteDatabaseCall(" + String.valueOf(this) + ") which was initialized at :\n", mInitWhere);
                mInitWhere = null;
            }
            onClose();
            //returnBack(this); // 注意不要将被回收的对象缓存.
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SmartSQLiteDatabaseCall{");
        sb.append("mSQLiteDatabaseManager=").append(mSQLiteDatabaseManager);
        sb.append(", mSrc=").append(mSrc);
        sb.append(", mIsClosed=").append(mIsClosed);
        sb.append('}');
        return sb.toString();
    }

    public synchronized void beginTransaction() {
        if (!isClosed()
                && mSrc != null) {
            try {
                mSrc.beginTransaction();
            } catch (Throwable e) {
                Log.e(TAG, "#beginTransaction", e);
            }
        }
    }

    public synchronized void setTransactionSuccessful() {
        if (!isClosed()
                && mSrc != null) {
            try {
                mSrc.setTransactionSuccessful();
            } catch (Throwable e) {
                Log.e(TAG, "#setTransactionSuccessful", e);
            }
        }
    }

    public synchronized void endTransaction() {
        if (!isClosed()
                && mSrc != null) {
            try {
                mSrc.endTransaction();
            } catch (Throwable e) {
                Log.e(TAG, "#endTransaction", e);
            }
        }
    }

    @Nonnull
    public synchronized Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor c = null;
        if (!isClosed()
                && mSrc != null) {
            try {
                c = mSrc.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            } catch (Throwable e) {
                Log.e(TAG, "#query", e);
            }
        }
        return FinalizableCursorWrapper.create(c == null ? new NullCursor() : c);
    }

    public synchronized Cursor rawQuery(String sql, String... selectionArgs) {
        Cursor c = null;
        if (!isClosed()
                && mSrc != null) {
            try {
                c = mSrc.rawQuery(sql, selectionArgs);
            } catch (Throwable e) {
                Log.e(TAG, "#rawQuery", e);
            }
        }
        return FinalizableCursorWrapper.create(c == null ? new NullCursor() : c);
    }

    public synchronized long insert(String table, String nullColumnHack, ContentValues values) {
        long rowID = -1L;
        if (!isClosed()
                && mSrc != null
                && !mSrc.isReadOnly()) {
            try {
                rowID = mSrc.insert(table, nullColumnHack, values);
            } catch (Throwable e) {
                Log.e(TAG, "#insert", e);
            }
        }
        return rowID;
    }

    public synchronized long insertWithOnConflict(String table, String nullColumnHack,
                                                  ContentValues initialValues, int conflictAlgorithm) {
        long result = -1L;
        if (!isClosed()
                && mSrc != null
                && !mSrc.isReadOnly()) {
            try {
                result = mSrc.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
            } catch (Throwable e) {
                Log.e(TAG, "#insertWithOnConflict", e);
            }
        }
        return result;
    }

    public synchronized long replace(String table, String nullColumnHack, ContentValues initialValues) {
        long result = -1L;
        if (!isClosed()
                && mSrc != null
                && !mSrc.isReadOnly()) {
            try {
                result = mSrc.replace(table, nullColumnHack, initialValues);
            } catch (Throwable e) {
                Log.e(TAG, "#replace", e);
            }
        }
        return result;
    }

    public synchronized int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        int result = 0;
        if (!isClosed()
                && mSrc != null
                && !mSrc.isReadOnly()) {
            try {
                result = mSrc.update(table, values, whereClause, whereArgs);
            } catch (Throwable e) {
                Log.e(TAG, "#update", e);
                result = -1; // 出异常时, 返回 -1
            }
        }
        return result;
    }

    public synchronized int updateWithOnConflict(String table, ContentValues values,
                                                 String whereClause, String[] whereArgs, int conflictAlgorithm) {
        int result = 0;
        if (!isClosed()
                && mSrc != null
                && !mSrc.isReadOnly()) {
            try {
                result = mSrc.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
            } catch (Throwable e) {
                Log.e(TAG, "#updateWithOnConflict", e);
                result = -1; // 出异常时, 返回 -1
            }
        }
        return result;
    }

    public synchronized int delete(String table, String whereClause, String[] whereArgs) {
        int result = 0;
        if (!isClosed()
                && mSrc != null
                && !mSrc.isReadOnly()) {
            try {
                result = mSrc.delete(table, whereClause, whereArgs);
            } catch (Throwable e) {
                Log.e(TAG, "#delete", e);
                result = -1; // 出异常时, 返回 -1
            }
        }
        return result;
    }

    @Override
    public void execSQL(String sql) {
        if (!isClosed()
                && mSrc != null
                && !mSrc.isReadOnly()) {
            try {
                mSrc.execSQL(sql);
            } catch (Throwable e) {
                Log.e(TAG, "#execSQL", e);
            }
        }
    }

    @Override
    public boolean isReadOnly() {
        return mSrc.isReadOnly();
    }
}
