package com.example.myproduct.lib.common.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * @author lihanguang
 * @date 2017/9/28 16:51
 */

public abstract class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    private final Context mContext;
    private final int mVersion;
    private EncryptedHelper mEncryptedHelper;

    public SQLiteOpenHelper(Context context, String name, int version) {
        super(context.getApplicationContext(), name, null, version, null);
        mContext = context.getApplicationContext();
        mVersion = version;
    }

    @Override
    final public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        this.onOpen(this.wrap(db));
    }

    @Override
    final public void onCreate(SQLiteDatabase db) {
        this.onCreate(this.wrap(db));
    }

    @Override
    final public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onUpgrade(this.wrap(db), oldVersion, newVersion);
    }

    @Override
    final public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onDowngrade(this.wrap(db), oldVersion, newVersion);
    }

    protected abstract void onOpen(SQLiteDatabaseCall db);

    protected abstract void onCreate(SQLiteDatabaseCall db);

    protected abstract void onUpgrade(SQLiteDatabaseCall db, int oldVersion, int newVersion);

    protected void onDowngrade(SQLiteDatabaseCall db, int oldVersion, int newVersion) {
        throw new SQLiteException("Can't downgrade database from version " +
                oldVersion + " to " + newVersion);
    }

    public SQLiteDatabaseCall getWritableDb() {
        return this.wrap(this.getWritableDatabase());
    }

    public SQLiteDatabaseCall getReadableDb() {
        return this.wrap(this.getReadableDatabase());
    }

    private SQLiteDatabaseCall wrap(final SQLiteDatabase sqLiteDatabase) {
        if (sqLiteDatabase == null) {
            return null;
        }
        return new SQLiteDatabaseCall() {
            @Override
            public boolean isClosed() {
                return !sqLiteDatabase.isOpen();
            }

            @Override
            public void close() {
                sqLiteDatabase.close();
            }

            @Override
            public void beginTransaction() {
                sqLiteDatabase.beginTransaction();
            }

            @Override
            public void setTransactionSuccessful() {
                sqLiteDatabase.setTransactionSuccessful();
            }

            @Override
            public void endTransaction() {
                sqLiteDatabase.endTransaction();
            }

            @Override
            public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
                return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            }

            @Override
            public Cursor rawQuery(String sql, String... selectionArgs) {
                return sqLiteDatabase.rawQuery(sql, selectionArgs);
            }

            @Override
            public long insert(String table, String nullColumnHack, ContentValues values) {
                return sqLiteDatabase.insert(table, nullColumnHack, values);
            }

            @Override
            public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
                return sqLiteDatabase.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
            }

            @Override
            public long replace(String table, String nullColumnHack, ContentValues initialValues) {
                return sqLiteDatabase.replace(table, nullColumnHack, initialValues);
            }

            @Override
            public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
                return sqLiteDatabase.update(table, values, whereClause, whereArgs);
            }

            @Override
            public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) {
                return sqLiteDatabase.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
            }

            @Override
            public int delete(String table, String whereClause, String[] whereArgs) {
                return sqLiteDatabase.delete(table, whereClause, whereArgs);
            }

            @Override
            public void execSQL(String sql) throws SQLException {
                sqLiteDatabase.execSQL(sql);
            }

            @Override
            public boolean isReadOnly() {
                return sqLiteDatabase.isReadOnly();
            }

            @Override
            public String toString() {
                return String.valueOf(sqLiteDatabase);
            }
        };
    }


    private EncryptedHelper checkEncryptedHelper() {
        if (mEncryptedHelper == null) {
            mEncryptedHelper = new EncryptedHelper(mContext, this.getDatabaseName(), mVersion, true);
        }

        return this.mEncryptedHelper;
    }

    public SQLiteDatabaseCall getEncryptedWritableDb(String password) {
        EncryptedHelper encryptedHelper = this.checkEncryptedHelper();
        return encryptedHelper.wrap(encryptedHelper.getWritableDatabase(password));
    }

    public SQLiteDatabaseCall getEncryptedWritableDb(char[] password) {
        EncryptedHelper encryptedHelper = this.checkEncryptedHelper();
        return encryptedHelper.wrap(encryptedHelper.getWritableDatabase(password));
    }

    public SQLiteDatabaseCall getEncryptedReadableDb(String password) {
        EncryptedHelper encryptedHelper = this.checkEncryptedHelper();
        return encryptedHelper.wrap(encryptedHelper.getReadableDatabase(password));
    }

    public SQLiteDatabaseCall getEncryptedReadableDb(char[] password) {
        EncryptedHelper encryptedHelper = this.checkEncryptedHelper();
        return encryptedHelper.wrap(encryptedHelper.getReadableDatabase(password));
    }

    private class EncryptedHelper extends net.sqlcipher.database.SQLiteOpenHelper {

        public EncryptedHelper(Context context, String name, int version, boolean loadLibs) {
            super(context, name, null, version);
            if (loadLibs) {
                net.sqlcipher.database.SQLiteDatabase.loadLibs(context);
            }
        }

        public void onCreate(net.sqlcipher.database.SQLiteDatabase db) {
            SQLiteOpenHelper.this.onCreate(this.wrap(db));
        }

        public void onUpgrade(net.sqlcipher.database.SQLiteDatabase db, int oldVersion, int newVersion) {
            SQLiteOpenHelper.this.onUpgrade(this.wrap(db), oldVersion, newVersion);
        }

        public void onOpen(net.sqlcipher.database.SQLiteDatabase db) {
            SQLiteOpenHelper.this.onOpen(this.wrap(db));
        }

        private SQLiteDatabaseCall wrap(final net.sqlcipher.database.SQLiteDatabase sqLiteDatabase) {
            if (sqLiteDatabase == null) {
                return null;
            }
            return new SQLiteDatabaseCall() {
                @Override
                public boolean isClosed() {
                    return !sqLiteDatabase.isOpen();
                }

                @Override
                public void close() {
                    sqLiteDatabase.close();
                }

                @Override
                public void beginTransaction() {
                    sqLiteDatabase.beginTransaction();
                }

                @Override
                public void setTransactionSuccessful() {
                    sqLiteDatabase.setTransactionSuccessful();
                }

                @Override
                public void endTransaction() {
                    sqLiteDatabase.endTransaction();
                }

                @Override
                public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
                    return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
                }

                @Override
                public Cursor rawQuery(String sql, String... selectionArgs) {
                    return sqLiteDatabase.rawQuery(sql, selectionArgs);
                }

                @Override
                public long insert(String table, String nullColumnHack, ContentValues values) {
                    return sqLiteDatabase.insert(table, nullColumnHack, values);
                }

                @Override
                public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
                    return sqLiteDatabase.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
                }

                @Override
                public long replace(String table, String nullColumnHack, ContentValues initialValues) {
                    return sqLiteDatabase.replace(table, nullColumnHack, initialValues);
                }

                @Override
                public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
                    return sqLiteDatabase.update(table, values, whereClause, whereArgs);
                }

                @Override
                public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) {
                    return sqLiteDatabase.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
                }

                @Override
                public int delete(String table, String whereClause, String[] whereArgs) {
                    return sqLiteDatabase.delete(table, whereClause, whereArgs);
                }

                @Override
                public void execSQL(String sql) throws SQLException {
                    sqLiteDatabase.execSQL(sql);
                }

                @Override
                public boolean isReadOnly() {
                    return sqLiteDatabase.isReadOnly();
                }

                @Override
                public String toString() {
                    return String.valueOf(sqLiteDatabase);
                }
            };
        }
    }
}
