package com.example.myproduct.app.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myproduct.app.R;
import com.example.myproduct.app.demo.rx.TestDemoListActivity;
import com.example.myproduct.app.ui.AppBaseActivity;
import com.example.myproduct.lib.common.config.BuglyConfigAction;
import com.example.myproduct.lib.common.utils.log.Log;
import com.example.myproduct.lib.common.utils.net.okhttp.OkHttpClientGenerator;
import com.example.myproduct.lib.common.utils.net.okhttp.progress.listener.IProgressListener;
import com.example.myproduct.lib.common.utils.net.retrofit.ApiFactory;
import com.example.myproduct.lib.common.utils.net.retrofit.ApiResult;
import com.example.myproduct.lib.common.utils.net.retrofit.ApiResultWrapper;
import com.example.myproduct.lib.common.utils.net.retrofit.ApiResultWrapperTransformer;
import com.example.myproduct.lib.common.utils.rx.RxSchedulers;
import com.example.myproduct.lib.common.utils.rx.eventbus.RxEventBus;
import com.example.myproduct.lib.common.utils.rx.lifecycle.ApplicationEvent;
import com.example.myproduct.lib.common.utils.thread.ThreadUtils;
import com.example.myproduct.lib.common.utils.thread.executors.SQLiteDatabaseThreadExecutor;
import com.example.myproduct.lib.common_ui.utils.glide.config.GlideOkHttpUrlProgressLoader;
import com.example.myproduct.lib.common_ui.utils.view.ViewUtils;
import com.example.myproduct.sdk.model.core.AppCoreMgrSrv;
import com.example.myproduct.sdk.model.login.AccountRegisterException;
import com.example.myproduct.sdk.model.login.AccountRegisterResult;
import com.example.myproduct.sdk.model.login.AccountRegisterWay;
import com.example.myproduct.sdk.model.login.LoginMgrSrv;
import com.jakewharton.rxbinding2.view.RxView;
import com.tencent.bugly.crashreport.CrashReport;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import tencent.tls.platform.TLSUserInfo;

import static com.example.myproduct.sdk.model.login.AccountRegisterWay.STR;

public class MainDemoActivity extends AppBaseActivity {
    private static final String TAG = "MainDemoActivity";

    private TextView mTvPhoenix;
    private Button mBtnShutdownApp;
    private CompositeDisposable mScopeDisposes;
    private ImageView mIvTestGlide;


    public static void startActivity(Context from) {
        Intent i = new Intent(from, MainDemoActivity.class);
        from.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_main);

        Button btnGoTestDemoList = ViewUtils.findViewById(this, R.id.btn_go_test_demo_list);
        btnGoTestDemoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestDemoListActivity.startActivity(v.getContext());
            }
        });

        mTvPhoenix = (TextView) findViewById(android.R.id.text1);
        mBtnShutdownApp = ViewUtils.findViewById(this, R.id.btn_shutdown_app);


        mScopeDisposes = new CompositeDisposable();

        /*try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //testBuglyAndPhoenix();
        //testGlide();
        //testRx();
        testRxView();
        //testRxEventBus();
        //testOkHttp();
        //testShutdownHook();
        //testRetrofit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            mScopeDisposes.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "#onDestroy");
    }

    private void testShutdownHook() {
        mBtnShutdownApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.globalDatabaseExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {

                        }
                    }
                });

                ThreadUtils.globalWorkerExecutor().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "消息循环 10十秒");
                    }
                }, 10000);

                System.exit(0);
            }
        });

        /*ShutdownHookConfigAction shutdownHookConfigAction = AppCoreMgrSrv.self().getAppConfigAction(ShutdownHookConfigAction.class);
        shutdownHookConfigAction.add(new ShutdownHookConfigAction.IShutDownHook() {
            @Override
            public void beforeShutdown() {
                int i = 7 / 0;
            }
        })
                .add(new ShutdownHookConfigAction.IShutDownHook() {
                    @Override
                    public void beforeShutdown() {
                        Log.e(TAG, "我在关机之前还是执行到了");
                    }
                });*/

        /* RxShutdownHook.onHook().doOnNext(new Consumer<Thread>() {
            @Override
            public void accept(Thread thread) throws Exception {
                Log.e(TAG, "我在关机之前还是执行到了1");
            }
        }).subscribe();

        RxShutdownHook.onHook().doOnNext(new Consumer<Thread>() {
            @Override
            public void accept(Thread thread) throws Exception {
                Log.e(TAG, "我在关机之前还是执行到了2");
            }
        }).subscribe();

        RxShutdownHook.onHook().doOnNext(new Consumer<Thread>() {
            @Override
            public void accept(Thread thread) throws Exception {
                Log.e(TAG, "我在关机之前还是执行到了3");
            }
        }).subscribe();*/
    }

    private void testGlide() {
        mIvTestGlide = ViewUtils.findViewById(this, R.id.iv_test_glide);

        Glide.with(this)
                .using(GlideOkHttpUrlProgressLoader
                        .create(
                                new IProgressListener() {
                                    @Override
                                    public void update(long bytesReadOrWrite, long contentLength, boolean done) {
                                        // 要把 glide 和 okhttp 的缓存都清理掉才能看到效果
                                        if (done) {
                                            Log.i(TAG, "#testGlide : 图片下载完成");
                                        } else {
                                            Log.i(TAG, "#testGlide : 图片下载进度(" + ((double) bytesReadOrWrite / contentLength) + ")");
                                        }
                                    }
                                })
                        .asStreamStringLoader(this)
                )
                // 服务器应该参照 google 的做法: "2x"(xhdpi), "3X"(xxhdpi)
                // 支持 Https
                //.load("https://www.google.com.hk/images/branding/googlelogo/2x/googlelogo_color_120x44dp.png")
                // 支持 Http
                //.load("http://www.baidu.com/img/bd_logo1.png")
                .load("http://mpic.tiankong.com/365/99f/36599f05cdb059e3bdfc0c56fb9c5423/640.jpg")
                .fitCenter()
                .into(mIvTestGlide);
    }

    private void testOkHttp() {
        OkHttpClientGenerator.getCacheOkHttpClient()
                .newCall(
                        new Request.Builder().url("http://www.baidu.com").build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG, "#testOkHttp : " + response.body().string());
                    }
                });
    }

    private void testBuglyAndPhoenix() {
        BuglyConfigAction buglyConfig = AppCoreMgrSrv.self().getAppConfigAction(BuglyConfigAction.class);

        Log.i(TAG, "getBuglyAppId() = " + buglyConfig.getBuglyAppId());
        Log.i(TAG, "isBuglyDebugMode() = " + buglyConfig.isBuglyDebugMode());

        boolean isPhoenix = getIntent().getBooleanExtra("is_phoenix", false);
        mTvPhoenix.setText("is_phoenix = " + isPhoenix);
        Log.i(TAG, "is_phoenix = " + isPhoenix);

        if (!isPhoenix) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    CrashReport.testJavaCrash();
                    //CrashReport.testNativeCrash();
                    //CrashReport.testANRCrash();
                }
            }, 5000);
        }
    }

    private void testRx() {
        mBtnShutdownApp.setText("test_Rx");

        final Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) throws Exception {
                StringBuilder sb = new StringBuilder();
                try {
                    throw t;
                } catch (AccountRegisterException e) {
                    AccountRegisterWay registerWay = e.registerWay();
                    sb.append("(1) 注册方式 : ")
                            .append(registerWay.getDescription()).append('\n')
                            .append("(2) 注册结果 : 失败").append('\n')
                            .append("(3) 注册失败的详细信息 : ").append('\n')
                            .append("\t\t" + e.body().Msg);
                } catch (Throwable e) {
                    sb.append("未知注册账号错误!");
                }
                Log.e(TAG, String.valueOf(sb), t);
            }
        };

        RxView.clicks(mBtnShutdownApp)
                .observeOn(RxSchedulers.io())
                .toFlowable(BackpressureStrategy.LATEST)
                .debounce(400, TimeUnit.MILLISECONDS, RxSchedulers.mainThread())
                .switchMap(new Function<Object, Publisher<AccountRegisterResult<TLSUserInfo>>>() {
                    @Override
                    public Publisher<AccountRegisterResult<TLSUserInfo>> apply(Object v) throws Exception {
                        final LoginMgrSrv loginMgrSrv = AppCoreMgrSrv.self().getAppMgrSrv(LoginMgrSrv.class);
                        return loginMgrSrv.registerAccount("iUser001236756", "Pwd123456", STR)
                                .doOnError(onError)
                                .onErrorResumeNext(new Function<Throwable, Publisher<? extends AccountRegisterResult<TLSUserInfo>>>() {
                                    @Override
                                    public Publisher<? extends AccountRegisterResult<TLSUserInfo>> apply(Throwable throwable) throws Exception {
                                        return Flowable.never();
                                    }
                                });
                    }
                })
                .observeOn(RxSchedulers.mainThread())
                .subscribe(
                        new Consumer<AccountRegisterResult<TLSUserInfo>>() {
                            @Override
                            public void accept(AccountRegisterResult<TLSUserInfo> o) throws Exception {
                                StringBuilder sb = new StringBuilder();
                                AccountRegisterWay registerWay = o.registerWay();
                                sb.append("(1) 注册方式 : ")
                                        .append(registerWay.getDescription()).append('\n')
                                        .append("(2) 注册结果 : 成功").append('\n')
                                        .append("(3) 注册成功的详细信息 :").append('\n')
                                        .append("\t\t" + o.body().identifier).append('\n')
                                        .append("\t\t" + o.body().accountType);
                                Log.w(TAG, String.valueOf(sb));
                            }
                        });
    }

    private void testRxView() {
        mBtnShutdownApp.setText("testRxView");
        RxView.clicks(mBtnShutdownApp)
                .compose(super.<Object>bindUntilEvent(ActivityEvent.DESTROY,
                        ApplicationEvent.TERMINATE))
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.w("main", "#testRxView - doFinally");
                    }
                })
                .subscribe(
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                Log.w("main", "#testRxView - onNext");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("main", "#testRxView - onError", throwable);
                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.w("main", "#testRxView - onComplete");
                            }
                        });
    }

    private void testRxEventBus() {
        mBtnShutdownApp.setText("post event");
        mBtnShutdownApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxEventBus.getDefault().post("做个勇敢中国人!");
            }
        });

        final Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.w("main", "111111111111111112222222222222", throwable);
            }
        };

        RxEventBus.getDefault().onEvent(String.class)
                .flatMap(new Function<String, Publisher<String>>() {
                    @Override
                    public Publisher<String> apply(String s) throws Exception {
                        return Flowable.just(s)
                                .map(new Function<String, String>() {
                                    @Override
                                    public String apply(String s) throws Exception {
                                        throw new Exception("在 变换过程中 里引起崩溃的异常!");
                                        //return s;
                                    }
                                })
                                .doOnError(onError)
                                .onErrorResumeNext(Flowable.<String>never());
                    }
                })
                /*.map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        throw new Exception("在 变换过程中 里引起崩溃的异常!");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("main", "doOnError", throwable);
                    }
                })
                .onErrorResumeNext(Flowable.<String>never())*/
                .subscribe(
                        new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Log.w("main", s);
                                throw new Exception("在 subscriber 里引起崩溃的异常!");
                            }
                        },
                        onError);

        RxEventBus.getDefault().onEvent(String.class)
                .subscribe(
                        new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Log.w("main222", s);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.w("main222", "333344444", throwable);
                            }
                        });
    }


    void testRetrofit() {
        /*Observable<ApiResult<UserApi.MessageList, UserApi.MessageListError>> o =
                ApiFactory.getUserApi()
                        .getMessageList(0, 10)
                        .subscribeOn(RxSchedulers.io());*/

        /*Observable<ApiResult<UserApi.MessageList, UserApi.MessageListError>> o =
                Observable.just(ApiResult.<UserApi.MessageList, UserApi.MessageListError>ok(700, "hello ok", new UserApi.MessageList()));*/

        Observable<ApiResult<ApiFactory.UserApi.MessageList, ApiFactory.UserApi.MessageListError>> o =
                Observable.just(ApiResult.<ApiFactory.UserApi.MessageList, ApiFactory.UserApi.MessageListError>fail(-700, "hello fail", new ApiFactory.UserApi.MessageListError()));

        /*Observable<ApiResult<UserApi.MessageList, UserApi.MessageListError>> o =
                Observable.error(new Exception("bilibilbi"));*/

        ApiResultWrapper<ApiFactory.UserApi.MessageList, ApiFactory.UserApi.MessageListError> apiResultWrapper = ApiResultWrapperTransformer.asWrapper(o);

        apiResultWrapper.onOk()
                .compose(super.<ApiFactory.UserApi.MessageList>bindUntilEvent(ActivityEvent.DESTROY,
                        ApplicationEvent.TERMINATE/*,
                        ApplicationEvent.TRIM_MEMORY,
                        ApplicationEvent.LOW_MEMORY*/))
                .observeOn(RxSchedulers.workerThread())
                .subscribe(new Consumer<ApiFactory.UserApi.MessageList>() {
                    @Override
                    public void accept(ApiFactory.UserApi.MessageList messageList) throws Exception {
                        Log.i("testRetrofit", "onOk : " + String.valueOf(messageList));
                    }
                }, ApiFactory.getApiThrowableHandler());

        apiResultWrapper.onError()
                .observeOn(RxSchedulers.workerThread())
                .subscribe(new Consumer<ApiFactory.UserApi.MessageListError>() {
                    @Override
                    public void accept(ApiFactory.UserApi.MessageListError messageListError) throws Exception {
                        Log.e("testRetrofit", "onFail : " + String.valueOf(messageListError));

                        throw new Exception("模拟在处理 onFail 的过程中,有产生新的异常");
                    }
                }, ApiFactory.getApiThrowableHandler());

        apiResultWrapper.onThrowable()
                .observeOn(RxSchedulers.workerThread())
                .subscribe(ApiFactory.getApiThrowableHandler());
    }
}
