package com.example.myproduct.app.demo.db.model;

import android.content.Context;

import com.example.myproduct.app.demo.db.model.tables.TodoItem;
import com.example.myproduct.app.demo.db.model.tables.TodoList;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseCall;
import com.example.myproduct.lib.common.utils.db.SQLiteUpgrader;
import com.example.myproduct.lib.common.utils.db.SmartSQLiteOpenHelper;

/**
 * @author lihanguang
 * @date 2017/11/1 15:58
 */

public class TodoSQLiteOpenHelper2 extends SmartSQLiteOpenHelper {
    public static final int DATABASE_FIRST_VERSION = 1; // 最初版本号是 1
    public static final int DATABASE_LAST_VERSION = 3; // 目前是 3

    private TodoSQLiteOpenHelper2(Context context, String name, int version,
                                  EncryptedDBType encryptedDBType, String password,
                                  SQLiteUpgrader upgrader) {
        super(context, name, version, encryptedDBType, password, upgrader);
    }

    public static TodoSQLiteOpenHelper2 create(Context context, String name,
                                               EncryptedDBType encryptedDBType, String password) {
        SQLiteUpgrader upgrader = new SQLiteUpgraderImpl(DATABASE_FIRST_VERSION, DATABASE_LAST_VERSION);
        return new TodoSQLiteOpenHelper2(context, name, DATABASE_LAST_VERSION, encryptedDBType, password, upgrader);
    }

    @Override
    protected void onOpen(SQLiteDatabaseCall db) {

    }

    private static class SQLiteUpgraderImpl extends SQLiteUpgrader {

        public SQLiteUpgraderImpl(int firstVersionCode, int lastVersionCode) {
            super(firstVersionCode, lastVersionCode);
            init();
        }

        private void init() {
            atVersion(DATABASE_FIRST_VERSION).todo(new DatabaseModifiedTask() {
                @Override
                public void onMotify(SQLiteDatabaseCall db, Version executedAt) {
                    doInVer01(db);
                }
            });

            atVersion(2).todo(new DatabaseModifiedTask() {
                @Override
                public void onMotify(SQLiteDatabaseCall db, Version executedAt) {
                    doInVer02(db);
                }
            });

            atVersion(3).todo(new DatabaseModifiedTask() {
                @Override
                public void onMotify(SQLiteDatabaseCall db, Version executedAt) {
                    doInVer03(db);
                }
            });
        }

        private void doInVer01(SQLiteDatabaseCall db) {
            db.execSQL(TodoList.CREATE_LIST);
            db.execSQL(TodoItem.CREATE_ITEM);
            db.execSQL(TodoItem.CREATE_ITEM_LIST_ID_INDEX);

            long groceryListId = db.insert(TodoList.TABLE, null,
                    TodoList.builder()
                            .name("Grocery List")
                            .archived(false)
                            .build()
                            .toContentValues());
            db.insert(TodoItem.TABLE, null, TodoItem.builder()
                    .listId(groceryListId)
                    .description("Beer")
                    .complete(false)
                    .build()
                    .toContentValues());
            db.insert(TodoItem.TABLE, null, TodoItem.builder()
                    .listId(groceryListId)
                    .description("Point Break on DVD")
                    .complete(false)
                    .build()
                    .toContentValues());
            db.insert(TodoItem.TABLE, null, TodoItem.builder()
                    .listId(groceryListId)
                    .description("Bad Boys 2 on DVD")
                    .complete(false)
                    .build()
                    .toContentValues());

            long holidayPresentsListId = db.insert(TodoList.TABLE, null,
                    TodoList.builder()
                            .name("Holiday Presents")
                            .archived(false)
                            .build()
                            .toContentValues());
            db.insert(TodoItem.TABLE, null,
                    TodoItem.builder()
                            .listId(holidayPresentsListId)
                            .description("Pogo Stick for Jake W.")
                            .complete(false)
                            .build()
                            .toContentValues());
            db.insert(TodoItem.TABLE, null,
                    TodoItem.builder()
                            .listId(holidayPresentsListId)
                            .description("Jack-in-the-box for Alec S.")
                            .complete(false)
                            .build()
                            .toContentValues());
            db.insert(TodoItem.TABLE, null,
                    TodoItem.builder()
                            .listId(holidayPresentsListId)
                            .description("Pogs for Matt P.")
                            .complete(false)
                            .build()
                            .toContentValues());
            db.insert(TodoItem.TABLE, null,
                    TodoItem.builder()
                            .listId(holidayPresentsListId)
                            .description("Cola for Jesse W.")
                            .complete(false)
                            .build()
                            .toContentValues());

            long workListId = db.insert(TodoList.TABLE, null,
                    TodoList.builder()
                            .name("Work Items")
                            .archived(false)
                            .build()
                            .toContentValues());
            db.insert(TodoItem.TABLE, null,
                    TodoItem.builder()
                            .listId(workListId)
                            .description("Finish SqlBrite library")
                            .complete(false)
                            .build()
                            .toContentValues());
            db.insert(TodoItem.TABLE, null,
                    TodoItem.builder()
                            .listId(workListId)
                            .description("Finish SqlBrite sample app")
                            .complete(false)
                            .build()
                            .toContentValues());
            db.insert(TodoItem.TABLE, null,
                    TodoItem.builder()
                            .listId(workListId)
                            .description("Publish SqlBrite to GitHub")
                            .complete(false)
                            .build()
                            .toContentValues());

            long birthdayPresentsListId = db.insert(TodoList.TABLE, null,
                    TodoList.builder()
                            .name("Birthday Presents")
                            .archived(true)
                            .build()
                            .toContentValues());
            db.insert(TodoItem.TABLE, null,
                    TodoItem.builder()
                            .listId(birthdayPresentsListId)
                            .description("New Car")
                            .complete(false)
                            .build()
                            .toContentValues());
        }

        private void doInVer02(SQLiteDatabaseCall db) {

        }

        private void doInVer03(SQLiteDatabaseCall db) {

        }
    }
}
