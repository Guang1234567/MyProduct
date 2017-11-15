package com.example.myproduct.app.demo.db.model.dao;

import android.database.Cursor;

import com.example.myproduct.app.demo.db.model.tables.TodoList;
import com.example.myproduct.lib.common.utils.db.DbUtils;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseCall;
import com.example.myproduct.lib.common.utils.db.annotation.DatabaseThread;
import com.example.myproduct.lib.common.utils.log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lihanguang
 * @date 2017/10/19 13:28
 */

@DatabaseThread
public class TodoListDao {
    public static final String TAG = "TodoListDao";

    public TodoListDao() {
    }

    public static long insert(SQLiteDatabaseCall dbcall, TodoList todoList) {
        long insertedRowId = dbcall.insert(TodoList.TABLE, null, todoList.toContentValues());
        return insertedRowId;
    }

    public static TodoList findById(SQLiteDatabaseCall dbcall, long id) {
        Cursor c = dbcall.query(TodoList.TABLE, null, TodoList.ROW_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c.moveToNext()) {
            try {
                return TodoList.mapper().apply(c);
            } catch (Throwable e) {
                Log.e(TAG, "#findById", e);
            }
        }
        DbUtils.closeQuietly(c);
        return null;
    }

    public static List<TodoList> findAll(SQLiteDatabaseCall dbcall) {
        Cursor c = dbcall.query(TodoList.TABLE, null, null, null, null, null, null);
        int size = c.getCount();
        List<TodoList> todoLists = new ArrayList<>(size);
        if (size > 0) {
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                TodoList todoList = null;
                try {
                    todoList = TodoList.mapper().apply(c);
                } catch (Exception e) {
                    Log.e(TAG, "#findAll", e);
                }
                if (todoList != null) {
                    todoLists.add(todoList);
                }
            }
        }
        DbUtils.closeQuietly(c);
        return todoLists;
    }

    public static int deleteById(SQLiteDatabaseCall dbcall, long id) {
        return dbcall.delete(TodoList.TABLE, TodoList.ROW_ID + "=?", new String[]{String.valueOf(id)});
    }

    public static int deleteAll(SQLiteDatabaseCall dbcall) {
        return dbcall.delete(TodoList.TABLE, null, null);
    }
}
