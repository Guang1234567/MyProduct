package com.example.myproduct.app.demo.db.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.myproduct.app.demo.db.model.tables.TodoItem;
import com.example.myproduct.lib.common.utils.db.DbUtils;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseCall;
import com.example.myproduct.lib.common.utils.db.annotation.DatabaseThread;
import com.example.myproduct.lib.common.utils.log.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lihanguang
 * @date 2017/10/19 13:28
 */

@DatabaseThread
public class TodoItemDao {
    public static final String TAG = "TodoItemDao";

    public TodoItemDao() {
    }

    public static long insert(SQLiteDatabaseCall dbcall, @NonNull TodoItem todoItem) {
        long insertedRowId = dbcall.insert(TodoItem.TABLE, null, todoItem.toContentValues());
        return insertedRowId;
    }

    /**
     * {@link android.content.ContentProvider#bulkInsert(android.net.Uri, android.content.ContentValues[])}
     *
     * @param dbcall
     * @param todoItems
     * @return The number of values that were inserted.
     */
    public static long bulkInsert(SQLiteDatabaseCall dbcall, long listId, @NonNull List<TodoItem> todoItems) {
        long numInserted = todoItems.size();
        Iterator<TodoItem> it = todoItems.iterator();
        while (it.hasNext()) {
            TodoItem todoItem = it.next();
            ContentValues cvs = todoItem.toContentValues();
            cvs.put(TodoItem.LIST_ID, listId);
            long insertedRowId = dbcall.insert(TodoItem.TABLE, null, cvs);
            if (insertedRowId == -1L) {
                numInserted--;
            }
        }
        return numInserted;
    }

    @Nullable
    public static TodoItem findById(SQLiteDatabaseCall dbcall, long id) {
        Cursor c = dbcall.query(TodoItem.TABLE, null, TodoItem.ROW_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c.moveToNext()) {
            try {
                return TodoItem.mapper().apply(c);
            } catch (Throwable e) {
                Log.e(TAG, "#findById", e);
            }
        }
        DbUtils.closeQuietly(c);
        return null;
    }

    public static List<TodoItem> findSomeByListId(SQLiteDatabaseCall dbcall, long id) {
        Cursor c = dbcall.query(TodoItem.TABLE, null, TodoItem.LIST_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        int size = c.getCount();
        List<TodoItem> todoItems = new ArrayList<>(size);
        if (size > 0) {
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                TodoItem todoItem = null;
                try {
                    todoItem = TodoItem.mapper().apply(c);
                } catch (Exception e) {
                    Log.e(TAG, "#findSomeByListId", e);
                }
                if (todoItem != null) {
                    todoItems.add(todoItem);
                }
            }
        }
        DbUtils.closeQuietly(c);
        return todoItems;
    }

    public static int deleteSomeByListId(SQLiteDatabaseCall dbcall, long id) {
        return dbcall.delete(TodoItem.TABLE, TodoItem.LIST_ID + "=?", new String[]{String.valueOf(id)});
    }

    public static int deleteAll(SQLiteDatabaseCall dbcall) {
        return dbcall.delete(TodoItem.TABLE, null, null);
    }
}
