package com.example.myproduct.app.demo.db.presenter;

import android.content.Context;

import com.example.myproduct.app.demo.db.model.TodoModel;
import com.example.myproduct.app.demo.db.model.TodoSQLiteOpenHelper2;
import com.example.myproduct.app.demo.db.model.pojo.TodoListItem;
import com.example.myproduct.app.demo.db.model.tables.TodoItem;
import com.example.myproduct.app.demo.db.model.tables.TodoList;
import com.example.myproduct.lib.common.utils.Utils;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseCall;
import com.example.myproduct.lib.common.utils.db.SQLiteDatabaseManager;
import com.example.myproduct.lib.common.utils.db.SQLiteOpenHelper;
import com.example.myproduct.lib.common.utils.rx.RxSchedulers;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

/**
 * @author lihanguang
 * @date 2017/10/23 14:28
 */

public class TodoPresenter {
    private static final String TAG = "TodoPresenter";

    private Context mContext;

    private TodoModel mTodoModel;

    private final SQLiteDatabaseManager mDatabaseManager;

    private final SQLiteDatabaseCall mDatabaseHolder;

    private final Scheduler mScopeScheduler;

    public TodoPresenter(Context context) {
        mContext = context;
        mTodoModel = new TodoModel();

        SQLiteOpenHelper dbHelper = TodoSQLiteOpenHelper2.create(context.getApplicationContext(),
                "user001.db");

        // 用户001 的数据库
        mDatabaseManager = SQLiteDatabaseManager.createStandard(dbHelper); // 非加密版本
        //mDatabaseManager = SQLiteDatabaseManager.createEncrypted(dbHelper, "password_1234567"); // 加密版本
        mDatabaseHolder = mDatabaseManager.obtainDbCall();


        //为了保证在某个界面内 insert, update, delete, query 四者任意组合能按次序执行, 专门为这个界面新建个专属的局部(scope)的 SingleScheduler
        //mScopeScheduler = new SingleScheduler();
        //or
        mScopeScheduler = RxSchedulers.workerThread();
    }

    @Override
    protected void finalize() throws Throwable {
    }

    public void close() {
        Utils.closeQuietly(mDatabaseHolder);
    }

    public Flowable<List<TodoListItem>> findAllWithItemCountWithoutItems(Context context) {
        return Flowable.fromCallable(
                new Callable<List<TodoListItem>>() {
                    @Override
                    public List<TodoListItem> call() throws Exception {
                        return mTodoModel.findAllWithItemCountWithoutItems(mDatabaseManager);
                    }
                })
                .subscribeOn(RxSchedulers.database())
                .observeOn(mScopeScheduler);
    }

    public Flowable<Set<Object>> createTodoListItemMonitor() {
        return mDatabaseManager
                .createTableMonitor(TodoListItem.class)
                .subscribeOn(RxSchedulers.database())
                .observeOn(mScopeScheduler);
    }

    public Completable insertSomeRandomly() {
        return Flowable.fromCallable(
                new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        TodoList todoList = TodoList.builder().name("Test_" + new Random().nextInt(100)).archived(false).build();

                        int max = 5;
                        int min = 1;
                        Random random = new Random();
                        int s = random.nextInt(max) % (max - min + 1) + min;
                        List<TodoItem> todoItems = new LinkedList<>();
                        for (int i = min; i <= s; i++) {
                            todoItems.add(TodoItem.builder().listId(todoList.rowId()).description("child_" + i).complete(false).build());
                        }

                        TodoListItem todoListItem = TodoListItem.builder()
                                .todoList(todoList)
                                .todoItems(todoItems)
                                .itemCount(todoItems.size())
                                .build();

                        return mTodoModel.insert(mDatabaseManager, todoListItem);
                    }
                })
                .subscribeOn(RxSchedulers.database())
                .observeOn(mScopeScheduler)
                .flatMapCompletable(new Function<Long, Completable>() {
                    @Override
                    public Completable apply(Long insertedRowId) throws Exception {
                        if (insertedRowId != -1L) {
                            return Completable.complete();
                        } else {
                            return Completable.error(new IllegalStateException("insert fail!"));
                        }
                    }
                });
    }

    public Completable clearAll() {
        return Flowable.fromCallable(
                new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return mTodoModel.insert(mDatabaseManager, todoListItem);
                    }
                })
                .subscribeOn(RxSchedulers.database())
                .observeOn(mScopeScheduler)
                .flatMapCompletable(new Function<Long, Completable>() {
                    @Override
                    public Completable apply(Long insertedRowId) throws Exception {
                        if (insertedRowId != -1L) {
                            return Completable.complete();
                        } else {
                            return Completable.error(new IllegalStateException("insert fail!"));
                        }
                    }
                });
    }

    public Scheduler getScopeScheduler() {
        return mScopeScheduler;
    }
}
