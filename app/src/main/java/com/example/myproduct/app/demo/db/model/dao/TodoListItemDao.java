package com.example.myproduct.app.demo.db.model.dao;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.myproduct.app.demo.db.model.pojo.TodoListItem;
import com.example.myproduct.app.demo.db.model.tables.TodoItem;
import com.example.myproduct.app.demo.db.model.tables.TodoList;
import com.example.myproduct.lib.common.utils.db.DbUtils;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseCall;
import com.example.myproduct.lib.common.utils.db.annotation.DatabaseThread;
import com.example.myproduct.lib.common.utils.log.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lihanguang
 * @date 2017/10/19 16:02
 */

@DatabaseThread
public class TodoListItemDao {
    public static final String TAG = "TodoListItemDao";

    public TodoListItemDao() {
    }

    public static long insert(SQLiteDatabaseCall dbCall, TodoListItem todoListItem) {
        long insertedRowId = TodoListDao.insert(dbCall, todoListItem.todoList());
        if (insertedRowId != -1L) {
            TodoItemDao.bulkInsert(dbCall, insertedRowId, todoListItem.todoItems());
        }
        return insertedRowId;
    }

    /**
     * {@link android.content.ContentProvider#bulkInsert(android.net.Uri, android.content.ContentValues[])}
     *
     * @param dbCall
     * @param todoListItems
     * @return The number of values that were inserted.
     */
    public static long bulkInsert(SQLiteDatabaseCall dbCall, List<TodoListItem> todoListItems) {
        long numInserted = todoListItems.size();
        if (numInserted > 0) {
            Iterator<TodoListItem> it = todoListItems.iterator();
            while (it.hasNext()) {
                TodoListItem todoListItem = it.next();
                long insertedRowId = insert(dbCall, todoListItem);
                if (insertedRowId == -1L) {
                    numInserted--;
                }
            }
        }
        return numInserted;
    }

    @Nullable
    private static TodoListItem toTodoListItem(SQLiteDatabaseCall dbCall, TodoList todoList) {
        if (todoList != null) {
            List<TodoItem> todoItems = TodoItemDao.findSomeByListId(dbCall, todoList.rowId());
            return TodoListItem.builder()
                    .todoList(todoList)
                    .todoItems(todoItems)
                    .build();
        }
        return null;
    }

    @Nullable
    public static TodoListItem findById(SQLiteDatabaseCall dbCall, long listItemId) {
        TodoList todoList = TodoListDao.findById(dbCall, listItemId);
        TodoListItem todoListItem = toTodoListItem(dbCall, todoList);
        return todoListItem;
    }

    @NonNull
    public static List<TodoListItem> findAll(SQLiteDatabaseCall dbCall) {
        List<TodoList> todoLists = TodoListDao.findAll(dbCall);
        List<TodoListItem> result = new ArrayList<>(todoLists.size());
        Iterator<TodoList> it = todoLists.iterator();
        while (it.hasNext()) {
            TodoList todoList = it.next();
            TodoListItem todoListItem = toTodoListItem(dbCall, todoList);
            if (todoListItem != null) {
                result.add(todoListItem);
            }
        }
        return result;
    }

    @DatabaseThread
    public static int deleteById(SQLiteDatabaseCall dbCall, long id) {
        TodoItemDao.deleteSomeByListId(dbCall, id);
        return TodoListDao.deleteById(dbCall, id);
    }

    @DatabaseThread
    public static int deleteAll(SQLiteDatabaseCall dbCall) {
        TodoItemDao.deleteAll(dbCall);
        return TodoListDao.deleteAll(dbCall);
    }

    /**
     * 查询所有 TodoListItem.
     * <p>
     * 由于此方法最初设定用于ListView, 出于性能考虑 TodoListItem 只包含一些概述信息.
     * 具体包含哪些概述信息和映射规则,请
     * {@link TodoListItem#QUERY_ALL_WITH_ITEM_COUNT_WITHOUT_ITEMS}
     * {@link TodoListItem#MAPPER_OF_QUERY_ALL_WITH_ITEM_COUNT_WITHOUT_ITEMS}
     *
     * @param dbCall
     * @return
     */
    @NonNull
    public static List<TodoListItem> findAllWithItemCountWithoutItems(SQLiteDatabaseCall dbCall) {
        Cursor c = dbCall.rawQuery(TodoListItem.QUERY_ALL_WITH_ITEM_COUNT_WITHOUT_ITEMS);
        int size = c.getCount();
        List<TodoListItem> todoListItems = new ArrayList<>(size);
        if (size > 0) {
            c.moveToPosition(-1);
            while (c.moveToNext()) {
                TodoListItem todoListItem = null;
                try {
                    todoListItem = TodoListItem.MAPPER_OF_QUERY_ALL_WITH_ITEM_COUNT_WITHOUT_ITEMS.apply(c);
                } catch (Exception e) {
                    Log.e(TAG, "#findAllWithItemCountWithoutItems", e);
                }
                if (todoListItem != null) {
                    todoListItems.add(todoListItem);
                }
            }
        }
        DbUtils.closeQuietly(c);
        return todoListItems;
    }
}
