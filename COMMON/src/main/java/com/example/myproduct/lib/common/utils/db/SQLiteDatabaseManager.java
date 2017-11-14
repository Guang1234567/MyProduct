package com.example.myproduct.lib.common.utils.db;

import android.text.TextUtils;

import com.example.myproduct.lib.common.utils.log.Log;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * 多线程环境下, SQLiteDatabase 会遇到各种问题.
 * 问题详情:
 * <p>
 * https://github.com/dmytrodanylyk/dmytrodanylyk/blob/gh-pages/articles/Concurrent%20Database%20Access.md
 * (中文)http://blog.csdn.net/rockcode_li/article/details/39024497
 * <p>
 * 此类主要解决上面的问题
 *
 * @author lihanguang
 * @date 2017/9/12 16:45
 */
public class SQLiteDatabaseManager {

    private static final String TAG = "SQLiteDatabaseManager";

    private AtomicInteger mOpenCounter;
    private SQLiteOpenHelper mDatabaseHelper;
    private String mName;

    private SQLiteDatabaseCall mDatabase;

    private boolean mIsEncrypted;
    private String mPassword;

    private final FlowableProcessor<Set<Object>> mTriggers;

    private SQLiteDatabaseManager(SQLiteOpenHelper helper) {
        this(helper, null);
    }

    private SQLiteDatabaseManager(SQLiteOpenHelper helper, String password) {
        mOpenCounter = new AtomicInteger();
        mDatabaseHelper = helper;
        mName = helper.getDatabaseName();

        mIsEncrypted = !TextUtils.isEmpty(password);
        mPassword = password;

        mTriggers = PublishProcessor.<Set<Object>>create().toSerialized();
    }

    public static SQLiteDatabaseManager createStandard(SQLiteOpenHelper helper) {
        return new SQLiteDatabaseManager(helper);
    }

    public static SQLiteDatabaseManager createEncrypted(SQLiteOpenHelper helper, String password) {
        return new SQLiteDatabaseManager(helper, password);
    }

    final /*public*/ synchronized SQLiteDatabaseCall openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            if (mIsEncrypted) {
                mDatabase = mDatabaseHelper.getEncryptedWritableDb(mPassword);
            } else {
                mDatabase = mDatabaseHelper.getWritableDb();
            }
        } else {
            Log.w(TAG, "\nRecommend read or write database in single thread!\nCurrent reference count : " + mOpenCounter.get() + "\nCurrent Thread : " + String.valueOf(Thread.currentThread()));
        }
        return mDatabase;
    }

    final /*public*/ synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();
            mDatabase = null;
        }
    }

    public SQLiteDatabaseCall obtainDbCall() {
        return SmartSQLiteDatabaseCall.obtain(this);
    }

    public Flowable<Set<Object>> createTableMonitor(final Object... tables) {
        return mTriggers
                .filter(new Predicate<Set<Object>>() {
                    @Override
                    public boolean test(Set<Object> triggers) throws Exception {
                        for (Object table : tables) {
                            if (triggers.contains(table)) {
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .onBackpressureLatest();

    }

    public void sendTableTrigger(Object... tables) {
        mTriggers.onNext(new LinkedHashSet<>(Arrays.asList(tables)));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SQLiteDatabaseManager{");
        sb.append("mOpenCounter=").append(mOpenCounter);
        sb.append(", mDatabaseHelper=").append(mDatabaseHelper);
        sb.append(", mName='").append(mName).append('\'');
        sb.append(", mDatabase=").append(mDatabase);
        sb.append(", mIsEncrypted=").append(mIsEncrypted);
        sb.append(", mTriggers=").append(mTriggers);
        sb.append('}');
        return sb.toString();
    }
}
