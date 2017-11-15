package com.example.myproduct.app.demo.db.model;

import com.example.myproduct.app.demo.db.model.dao.TodoListItemDao;
import com.example.myproduct.app.demo.db.model.pojo.TodoListItem;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseCall;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseManager;
import com.example.myproduct.lib.common.utils.db.annotation.DatabaseThread;
import com.example.myproduct.lib.common.utils.log.Log;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * @author lihanguang
 * @date 2017/10/23 11:25
 */

public class TodoModel {
    private static final String TAG = "TodoModel";

    private final FlowableProcessor mNotifier;

    public TodoModel() {
        mNotifier = PublishProcessor.create().toSerialized();
    }

    public void notifyInsert(SQLiteDatabaseManager dbMgr, final long insertedRowId) {
        if (mNotifier.hasSubscribers()
                && !mNotifier.hasComplete()) {
            TodoListItem r = null;
            try {
                r = findById(dbMgr, insertedRowId);
            } catch (Throwable tr) {
                Log.e(TAG, "", tr);
            }
            if (r != null) {
                mNotifier.onNext(r);
            }
        }
    }

    @DatabaseThread
    public List<TodoListItem> findAllWithItemCountWithoutItems(SQLiteDatabaseManager dbMgr) {
        List<TodoListItem> todoListItems;
        SQLiteDatabaseCall dbCall = dbMgr.obtainDbCall();
        dbCall.beginTransaction();
        try {
            todoListItems = TodoListItemDao.findAllWithItemCountWithoutItems(dbCall);
            dbCall.setTransactionSuccessful();
        } finally {
            dbCall.endTransaction();
            dbCall.close();
        }
        return todoListItems == null ? new LinkedList<TodoListItem>() : todoListItems;
    }

    @DatabaseThread
    public TodoListItem findById(SQLiteDatabaseManager dbMgr, long id) {
        TodoListItem todoListItem;
        SQLiteDatabaseCall dbCall = dbMgr.obtainDbCall();
        dbCall.beginTransaction();
        try {
            todoListItem = TodoListItemDao.findById(dbCall, id);
            dbCall.setTransactionSuccessful();
        } finally {
            dbCall.endTransaction();
            dbCall.close();
        }
        return todoListItem;
    }

    @DatabaseThread
    public long insert(final SQLiteDatabaseManager dbMgr, TodoListItem todoListItem) {
        long insertedRowId = -1L;
        final SQLiteDatabaseCall dbCall = dbMgr.obtainDbCall();
        dbCall.beginTransaction();
        try {
            insertedRowId = TodoListItemDao.insert(dbCall, todoListItem);
            dbCall.setTransactionSuccessful();
        } catch (Throwable throwable) {
        } finally {
            dbCall.endTransaction();
            dbCall.close();
        }

        // 发通知
        if (insertedRowId != -1) {
            // 通知方式一:
            // Only send a table trigger if the insert was successful.
            dbMgr.sendTableTrigger(TodoListItem.class); // 在Model层发变化的通知, 别在 Dao 里面发, Dao主要封装数据库操作.

            // 通知方式二:;
            notifyInsert(dbMgr, insertedRowId);
        }

        return insertedRowId;
    }

    @DatabaseThread
    public int deleteById(SQLiteDatabaseManager dbMgr, long id) {
        int affectedRows = 0;
        final SQLiteDatabaseCall dbCall = dbMgr.obtainDbCall();
        dbCall.beginTransaction();
        try {
            affectedRows = TodoListItemDao.deleteById(dbCall, id);
            dbCall.setTransactionSuccessful();
        } catch (Throwable throwable) {
        } finally {
            dbCall.endTransaction();
            dbCall.close();
        }

        // 发通知
        if (affectedRows > 0) {
            // Only send a table trigger if the insert was successful.
            dbMgr.sendTableTrigger(TodoListItem.class); // 在Model层发变化的通知, 别在 Dao 里面发, Dao主要封装数据库操作.
        }
        return affectedRows;
    }

    @DatabaseThread
    public int deleteAll(SQLiteDatabaseManager dbMgr) {
        int affectedRows = -1;
        final SQLiteDatabaseCall dbCall = dbMgr.obtainDbCall();
        dbCall.beginTransaction();
        try {
            affectedRows = TodoListItemDao.deleteAll(dbCall);
            dbCall.setTransactionSuccessful();
        } catch (Throwable throwable) {
        } finally {
            dbCall.endTransaction();
            dbCall.close();
        }

        // 发通知
        if (affectedRows > 0) {
            // Only send a table trigger if the insert was successful.
            dbMgr.sendTableTrigger(TodoListItem.class); // 在Model层发变化的通知, 别在 Dao 里面发, Dao主要封装数据库操作.
        }
        return affectedRows;
    }
}
