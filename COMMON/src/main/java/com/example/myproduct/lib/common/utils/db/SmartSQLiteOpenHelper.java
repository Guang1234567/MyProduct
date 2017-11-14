package com.example.myproduct.lib.common.utils.db;

import android.content.Context;

/**
 * @author lihanguang
 * @date 2017/9/28 16:51
 */

public abstract class SmartSQLiteOpenHelper extends SQLiteOpenHelper {
    private SQLiteUpgrader mSQLiteUpgrader;

    public SmartSQLiteOpenHelper(Context context, String name, int version,
                                 SQLiteUpgrader sqLiteUpgrader) {
        super(context, name, version);
        init(sqLiteUpgrader);
    }

    private void init(SQLiteUpgrader sqLiteUpgrader) {
        mSQLiteUpgrader = sqLiteUpgrader;
    }

    @Override
    final protected void onCreate(SQLiteDatabaseCall db) {
        if (mSQLiteUpgrader != null) {
            mSQLiteUpgrader.doOnCreate(db);
        }
    }

    @Override
    final protected void onUpgrade(SQLiteDatabaseCall db, int oldVersion, int newVersion) {
        if (mSQLiteUpgrader != null) {
            mSQLiteUpgrader.doOnUpgrade(db, oldVersion, newVersion);
        }
    }
}
