package com.example.myproduct.lib.common.utils.db;

import android.util.SparseArray;

import com.example.myproduct.lib.common.utils.log.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lihanguang
 * @date 2017/9/28 16:52
 */

public class SQLiteUpgrader {
    public static final String TAG = "SQLiteUpgrader";
    private final int mFirstVersionCode;
    private final int mLastVersionCode;
    private SparseArray<Version> mVersions;

    public SQLiteUpgrader(int firstVersionCode, int lastVersionCode) {
        mFirstVersionCode = firstVersionCode;
        mLastVersionCode = lastVersionCode;
        mVersions = new SparseArray<>();
    }

    void doOnCreate(SQLiteDatabaseCall db) {
        Log.i(TAG, "┏ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┓");
        Log.i(TAG, "┃            Start Create Database");
        Log.i(TAG, "┗ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┛");
        Log.i(TAG, "db = " + db);
        doEachVer(db, mFirstVersionCode, mLastVersionCode);
        Log.i(TAG, "┏ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┓");
        Log.i(TAG, "┃              End Create Database");
        Log.i(TAG, "┗ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┛");
    }

    void doOnUpgrade(SQLiteDatabaseCall db, int oldVersion, int newVersion) {
        Log.i(TAG, "┏ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┓");
        Log.i(TAG, "┃            Start Upgrade Database");
        Log.i(TAG, "┗ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┛");
        Log.i(TAG, "db = " + db + "   (@" + db.hashCode() + ")");
        Log.i(TAG, "oldVersion = " + oldVersion);
        Log.i(TAG, "newVersion = " + newVersion);
        doEachVer(db, oldVersion + 1, newVersion);
        Log.i(TAG, "┏ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┓");
        Log.i(TAG, "┃              End Upgrade Database");
        Log.i(TAG, "┗ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ━ ┛");
    }


    private void doEachVer(SQLiteDatabaseCall db, int fromVersion, int toVersion) {
        for (int toVer = fromVersion; toVer <= toVersion; toVer++) {
            Version ver = mVersions.get(toVer);
            if (ver != null) {
                Log.i(TAG, "Version " + toVer + " is being todo...");
                ver.onUpgrade(db);
                Log.i(TAG, "Version " + toVer + " is done!");
            } else {
                Log.w(TAG, "Nothing todo at version " + toVer + ". Is forget to configure?");
            }
        }
    }

    public Version atVersion(int newVersion) {
        Version ver = mVersions.get(newVersion);
        if (ver == null) {
            ver = new Version(newVersion);
            mVersions.put(newVersion, ver);
        }
        return ver;
    }

    public static class Version {
        private int mVersionCode;
        private List<DatabaseModifiedTask> mDatabaseModifiedTasks;

        private Version(int versionCode) {
            mVersionCode = versionCode;
            mDatabaseModifiedTasks = new LinkedList<>();
        }

        private void onUpgrade(SQLiteDatabaseCall db) {
            Iterator<DatabaseModifiedTask> it = mDatabaseModifiedTasks.iterator();
            while (it.hasNext()) {
                DatabaseModifiedTask task = it.next();
                if (task != null) {
                    task.onMotify(db, this);
                }
            }
        }

        public int getVersionCode() {
            return mVersionCode;
        }

        public Version todo(DatabaseModifiedTask task) {
            mDatabaseModifiedTasks.add(task);
            return this;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Version{");
            sb.append("mVersionCode=").append(mVersionCode);
            sb.append('}');
            return sb.toString();
        }
    }

    public interface DatabaseModifiedTask {
        void onMotify(SQLiteDatabaseCall db, Version executedAt);
    }
}
