package com.example.myproduct.lib.common.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.tencent.wcdb.DatabaseErrorHandler;
import com.tencent.wcdb.database.SQLiteCipherSpec;

/**
 * @author lihanguang
 * @date 2017/9/28 16:51
 */

public abstract class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    public enum EncryptedDBType {
        NO_CIPHER, // 不加密
        SQLCIPHER, // 加密
        WCDB_NO_CIPHER,// 不加密
        WCDB_CIPHER, // 加密
    }

    private final Context mContext;
    private final int mVersion;
    private EncryptedHelper mEncryptedHelper;
    private EncryptedHelperWeChat mEncryptedHelperWeChat;
    private EncryptedDBType mEncryptedDBType;
    private String mPassword;

    public SQLiteOpenHelper(Context context, String name, int version) {
        this(context, name, version, EncryptedDBType.NO_CIPHER, null);
    }

    public SQLiteOpenHelper(Context context, String name, int version, EncryptedDBType encryptedDBType, String password) {
        super(context.getApplicationContext(), name, null, version, null);
        mContext = context.getApplicationContext();
        mVersion = version;
        mEncryptedDBType = encryptedDBType;
        mPassword = password;
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
        SQLiteDatabaseCall result;
        switch (mEncryptedDBType) {
            case SQLCIPHER:
                EncryptedHelper encryptedHelper = this.checkEncryptedHelper();
                result = encryptedHelper.wrap(encryptedHelper.getWritableDatabase(mPassword));
                break;
            case WCDB_CIPHER:
            case WCDB_NO_CIPHER:
                EncryptedHelperWeChat encryptedHelperWC = this.checkEncryptedHelperWeChat(mEncryptedDBType);
                result = encryptedHelperWC.wrap(encryptedHelperWC.getWritableDatabase());
                break;
            default:
                result = this.wrap(this.getWritableDatabase());
                break;
        }
        return result;
    }

    public SQLiteDatabaseCall getReadableDb() {
        SQLiteDatabaseCall result;
        switch (mEncryptedDBType) {
            case SQLCIPHER:
                EncryptedHelper encryptedHelper = this.checkEncryptedHelper();
                result = encryptedHelper.wrap(encryptedHelper.getReadableDatabase(mPassword));
                break;
            case WCDB_CIPHER:
            case WCDB_NO_CIPHER:
                EncryptedHelperWeChat encryptedHelperWC = this.checkEncryptedHelperWeChat(mEncryptedDBType);
                result = encryptedHelperWC.wrap(encryptedHelperWC.getReadableDatabase());
                break;
            default:
                result = this.wrap(this.getReadableDatabase());
                break;
        }
        return result;
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

        return mEncryptedHelper;
    }

    private EncryptedHelperWeChat checkEncryptedHelperWeChat(EncryptedDBType encryptedDBType) {
        if (mEncryptedHelperWeChat == null) {
            if (EncryptedDBType.WCDB_CIPHER.equals(encryptedDBType)) {
                // 如以前使用过其他PRAGMA，可添加其他选项
                SQLiteCipherSpec cipher = new SQLiteCipherSpec()  // 加密描述对象
                        .setPageSize(1024)        // SQLCipher 默认 Page size 为 1024
                        .setSQLCipherVersion(3);  // 1,2,3 分别对应 1.x, 2.x, 3.x 创建的 SQLCipher 数据库

                mEncryptedHelperWeChat = new EncryptedHelperWeChat(
                        mContext,
                        this.getDatabaseName(), // DB 路径
                        mPassword.getBytes(),   // WCDB 密码参数类型为 byte[]
                        cipher,                 // 上面创建的加密描述对象
                        null,                   // CursorFactory
                        mVersion,
                        null                    // DatabaseErrorHandler
                        // SQLiteDatabaseHook 参数去掉了，在cipher里指定参数可达到同样目的
                );
            } else {
                mEncryptedHelperWeChat = new EncryptedHelperWeChat(
                        mContext,
                        this.getDatabaseName(), // DB 路径
                        null,                   // CursorFactory
                        mVersion,
                        null                    // DatabaseErrorHandler
                );
            }
        }

        return mEncryptedHelperWeChat;
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

    private class EncryptedHelperWeChat extends com.tencent.wcdb.database.SQLiteOpenHelper {

        /**
         * wcdb 同时支持不加密的版本
         */
        public EncryptedHelperWeChat(Context context, String name, com.tencent.wcdb.database.SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        /**
         * wcdb 同时支持不加密的版本
         */
        public EncryptedHelperWeChat(Context context, String name, com.tencent.wcdb.database.SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        /**
         * wcdb 同时支持加密的版本
         */
        public EncryptedHelperWeChat(Context context, String name, byte[] password, SQLiteCipherSpec cipher, com.tencent.wcdb.database.SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, password, cipher, factory, version, errorHandler);
        }

        /**
         * wcdb 同时支持加密的版本
         */
        public EncryptedHelperWeChat(Context context, String name, byte[] password, com.tencent.wcdb.database.SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, password, factory, version, errorHandler);
        }

        public void onCreate(com.tencent.wcdb.database.SQLiteDatabase db) {
            SQLiteOpenHelper.this.onCreate(this.wrap(db));
        }

        public void onUpgrade(com.tencent.wcdb.database.SQLiteDatabase db, int oldVersion, int newVersion) {
            SQLiteOpenHelper.this.onUpgrade(this.wrap(db), oldVersion, newVersion);
        }

        public void onOpen(com.tencent.wcdb.database.SQLiteDatabase db) {
            SQLiteOpenHelper.this.onOpen(this.wrap(db));
        }

        private SQLiteDatabaseCall wrap(final com.tencent.wcdb.database.SQLiteDatabase sqLiteDatabase) {
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
