package com.example.myproduct.app.demo.db.model;

import com.example.myproduct.app.demo.db.model.dao.TodoListItemDao;
import com.example.myproduct.app.demo.db.model.pojo.TodoListItem;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseCall;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseManager;
import com.example.myproduct.lib.common.utils.db.annotation.DatabaseThread;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * @author lihanguang
 * @date 2017/10/23 11:25
 */

public class TodoModel {

    private final FlowableProcessor mNotifier;

    public TodoModel() {
        mNotifier = PublishProcessor.create().toSerialized();
    }

    public void notifyInsert(Callable<TodoListItem> t) throws Throwable {
        if (mNotifier.hasSubscribers()
                && !mNotifier.hasComplete()) {
            TodoListItem r = t.call();
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
    public long insert(SQLiteDatabaseManager dbMgr, TodoListItem todoListItem) {
        long insertedRowId = -1L;
        final SQLiteDatabaseCall dbCall = dbMgr.obtainDbCall();
        dbCall.beginTransaction();
        try {
            insertedRowId = TodoListItemDao.insert(dbCall, todoListItem);

            dbCall.setTransactionSuccessful();

            // 发通知
            if (insertedRowId != -1) {
                // 通知方式一:
                // Only send a table trigger if the insert was successful.
                dbMgr.sendTableTrigger(TodoListItem.class); // 在Model层发变化的通知, 别在 Dao 里面发, Dao主要封装数据库操作.

                // 通知方式二:
                final long finalInsertedRowId = insertedRowId;
                notifyInsert(new Callable<TodoListItem>() {
                    @Override
                    public TodoListItem call() throws Exception {
                        return TodoListItemDao.findById(dbCall, finalInsertedRowId);
                    }
                });
            }
        } catch (Throwable throwable) {
        } finally {
            dbCall.endTransaction();
            dbCall.close();
        }
        return insertedRowId;
    }
}
