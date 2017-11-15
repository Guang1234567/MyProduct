package com.example.myproduct.app.demo.db;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.myproduct.app.R;
import com.example.myproduct.app.databinding.ActivityTestDatabaseUpgradeBinding;
import com.example.myproduct.app.demo.db.model.pojo.TodoListItem;
import com.example.myproduct.app.demo.db.presenter.TodoPresenter;
import com.example.myproduct.app.demo.rx.TestDemoListActivity;
import com.example.myproduct.app.ui.AppBaseActivity;
import com.example.myproduct.lib.common.utils.log.Log;
import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class TestDatabaseUpgradeDemoActivity extends AppBaseActivity {
    public static final String TAG = "TestDatabaseUpgradeActivity";

    TodoPresenter mPresenter;

    public final ObservableField<String> mToolbarTitle = new ObservableField<>();

    private ActivityTestDatabaseUpgradeBinding mBinding;

    private Completable mLoadTodoListItemsForListViewTask;

    public final static void startActivity(Context from) {
        from.startActivity(new Intent(from, TestDemoListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test_database_upgrade);
        mBinding.setActivity(this);

        mPresenter = new TodoPresenter(this);

        setUpToolBar(mBinding.toolBar);
        setUpList(mBinding.list);
    }

    private void setUpToolBar(Toolbar toolBar) {
        mToolbarTitle.set("测试例子");

        // 不要这样写
        /*RxView.clicks(toolBar)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .flatMapCompletable(new Function<Object, Completable>() {
                    @Override
                    public Completable apply(Object o) throws Exception {
                        return mPresenter.insertSomeRandomly();
                    }
                })
                .subscribe(
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.i(TAG, "insertSomeRandomly # onSuccess");
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable t) throws Exception {
                                Log.i(TAG, "insertSomeRandomly # onFail", t);
                            }
                        });*/

        RxView.clicks(toolBar)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //为啥 RxView.clicks(toolBar) 不用 flatMap 操作符跟 insertSomeRandomly2 连接起来, 这是因为 RxView.clicks 不调用 onComplete().
                        /*mPresenter.insertSomeRandomly2()
                                .compose(TestDatabaseUpgradeDemoActivity.super.<Set<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(
                                        new Action() {
                                            @Override
                                            public void run() throws Exception {
                                                Log.i(TAG, "insertSomeRandomly # onSuccess");
                                            }
                                        },
                                        new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable t) throws Exception {
                                                Log.i(TAG, "insertSomeRandomly # onFail", t);
                                            }
                                        });*/

                        mPresenter.insertSomeRandomly()
                                .compose(TestDatabaseUpgradeDemoActivity.super.<Set<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(
                                        new Action() {
                                            @Override
                                            public void run() throws Exception {
                                                Log.i(TAG, "insertSomeRandomly # onSuccess");
                                            }
                                        },
                                        new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable t) throws Exception {
                                                Log.i(TAG, "insertSomeRandomly # onFail", t);
                                            }
                                        });
                    }
                });

        RxView.longClicks(toolBar)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mPresenter.deleteAll()
                                .compose(TestDatabaseUpgradeDemoActivity.super.<Set<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(
                                        new Action() {
                                            @Override
                                            public void run() throws Exception {
                                                Log.i(TAG, "deleteAll # onSuccess");
                                            }
                                        },
                                        new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable t) throws Exception {
                                                Log.i(TAG, "deleteAll # onFail", t);
                                            }
                                        });
                    }
                });
    }

    private void setUpList(RecyclerView list) {
        list.setLayoutManager(new LinearLayoutManager(list.getContext()));
        list.setHasFixedSize(false);
        final TestDatabaseListAdapter adapter = new TestDatabaseListAdapter();
        list.setAdapter(adapter);


        mLoadTodoListItemsForListViewTask = mPresenter.findAllWithItemCountWithoutItems(this)
                .flatMapCompletable(
                        new Function<List<TodoListItem>, Completable>() {
                            @Override
                            public Completable apply(List<TodoListItem> todoListItems) throws Exception {
                                return adapter.changeAll(todoListItems);
                            }
                        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 第一次加载数据
        mLoadTodoListItemsForListViewTask
                .compose(super.<Set<Object>>bindUntilEvent(ActivityEvent.PAUSE)) // onPause() 被回调时, 自动释放相关资源
                .subscribe(
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.i(TAG, "mLoadTodoListItemsForListViewTask # onSuccess : adapter.changeAll");
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable t) throws Exception {
                                Log.i(TAG, "mLoadTodoListItemsForListViewTask # onFail : adapter.changeAll", t);
                            }
                        });

        // 监听TodoListItem表变化
        mPresenter.createTodoListItemMonitor()
                .compose(super.<Set<Object>>bindUntilEvent(ActivityEvent.PAUSE)) // onPause() 被回调时, 自动释放相关资源
                .debounce(1, TimeUnit.SECONDS) // 防止界面狂刷新
                .flatMapCompletable(new Function<Set<Object>, Completable>() {
                    @Override
                    public Completable apply(Set<Object> sentTables) throws Exception {
                        return mLoadTodoListItemsForListViewTask;
                    }
                })
                .subscribe(
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.i(TAG, "createTodoListItemMonitor # onSuccess : adapter.changeAll");
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable t) throws Exception {
                                Log.i(TAG, "createTodoListItemMonitor # onFail : adapter.changeAll", t);
                            }
                        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.close();
        mBinding.list.setAdapter(null);
    }
}
