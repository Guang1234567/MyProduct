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

        // 非加密版本1
        SQLiteOpenHelper dbHelper1 = TodoSQLiteOpenHelper2.create(context.getApplicationContext(),
                "user001.db", SQLiteOpenHelper.EncryptedDBType.NO_CIPHER, null);
        // 加密版本2
        SQLiteOpenHelper dbHelper2 = TodoSQLiteOpenHelper2.create(context.getApplicationContext(),
                "user002.db", SQLiteOpenHelper.EncryptedDBType.SQLCIPHER, "Password_1234567");
        // 加密版本3
        SQLiteOpenHelper dbHelper3 = TodoSQLiteOpenHelper2.create(context.getApplicationContext(),
                "user003.db", SQLiteOpenHelper.EncryptedDBType.WCDB_CIPHER, "Password_7654321");
        // 非加密版本4
        SQLiteOpenHelper dbHelper4 = TodoSQLiteOpenHelper2.create(context.getApplicationContext(),
                "user004.db", SQLiteOpenHelper.EncryptedDBType.WCDB_NO_CIPHER, null);

        // 用户001 的数据库
        mDatabaseManager = SQLiteDatabaseManager.create(/*dbHelper1*/ /*dbHelper2*/ dbHelper3);
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

    public Completable deleteAll() {
        return Flowable.fromCallable(
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return mTodoModel.deleteAll(mDatabaseManager);
                    }
                })
                .subscribeOn(RxSchedulers.database())
                .observeOn(mScopeScheduler)
                .flatMapCompletable(new Function<Integer, Completable>() {
                    @Override
                    public Completable apply(Integer affectedRows) throws Exception {
                        if (affectedRows > 0) {
                            return Completable.complete();
                        } else {
                            return Completable.error(new IllegalStateException("deleteAll fail! affectedRows = " + affectedRows));
                        }
                    }
                });
    }

    public Completable deleteById(final long id) {
        return Flowable.fromCallable(
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return mTodoModel.deleteById(mDatabaseManager, id);
                    }
                })
                .subscribeOn(RxSchedulers.database())
                .observeOn(mScopeScheduler)
                .flatMapCompletable(new Function<Integer, Completable>() {
                    @Override
                    public Completable apply(Integer affectedRows) throws Exception {
                        if (affectedRows > 0) {
                            return Completable.complete();
                        } else {
                            return Completable.error(new IllegalStateException("deleteById fail! affectedRows = " + affectedRows));
                        }
                    }
                });
    }

    public Scheduler getScopeScheduler() {
        return mScopeScheduler;
    }
}
