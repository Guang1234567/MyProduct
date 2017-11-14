package com.example.myproduct.app.ui.boot;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myproduct.app.MyProductApplication;
import com.example.myproduct.app.R;
import com.example.myproduct.app.databinding.ActivitySplashBinding;
import com.example.myproduct.app.ui.AppBaseActivity;
import com.example.myproduct.app.ui.login.LoginActivity;
import com.example.myproduct.app.ui.main.MainActivity;
import com.example.myproduct.lib.common.utils.log.Log;
import com.example.myproduct.lib.common.utils.rx.RxSchedulers;
import com.example.myproduct.lib.common.utils.rx.lifecycle.ApplicationEvent;
import com.example.myproduct.lib.common_ui.utils.view.ToastUtils;
import com.example.myproduct.sdk.model.core.AppCoreMgrSrv;
import com.example.myproduct.sdk.model.login.LoginMgrSrv;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class SplashActivity extends AppBaseActivity {
    private static final String TAG = "SplashActivity";

    public final ObservableField<String> mInfo = new ObservableField<>();

    private ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        mBinding.setActivity(this);

        Observable.just(MyProductApplication.getInstance())
                .compose(super.<MyProductApplication>bindUntilEvent(ActivityEvent.DESTROY, ApplicationEvent.TERMINATE))
                .observeOn(RxSchedulers.mainThread())
                .doOnNext(new Consumer<MyProductApplication>() {
                    @Override
                    public void accept(MyProductApplication app) throws Exception {
                        mInfo.set("1. 启动服务");
                    }
                })
                .delay(16, TimeUnit.MILLISECONDS, RxSchedulers.workerThread())
                .doOnNext(new Consumer<MyProductApplication>() {
                    @Override
                    public void accept(MyProductApplication app) throws Exception {
                        app.startBusiness(app.getApplicationContext()); // 程序启动总入口, 开启相关服务
                    }
                })
                .observeOn(RxSchedulers.mainThread())
                .doOnNext(new Consumer<MyProductApplication>() {
                    @Override
                    public void accept(MyProductApplication app) throws Exception {
                        mInfo.set("2. 完成启动");
                    }
                })
                .observeOn(RxSchedulers.workerThread())
                .map(new Function<MyProductApplication, Boolean>() {
                    @Override
                    public Boolean apply(MyProductApplication myProductApplication) throws Exception {
                        LoginMgrSrv loginMgrSrv = AppCoreMgrSrv.self().getAppMgrSrv(LoginMgrSrv.class);
                        return loginMgrSrv != null && loginMgrSrv.isUserLogin();
                    }
                })
                .observeOn(RxSchedulers.mainThread())
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isShortCutLogin) throws Exception {
                        if (isShortCutLogin) {
                            Log.i(TAG, "快捷登录!");
                            ToastUtils.show(SplashActivity.this, "快捷登录!", Toast.LENGTH_SHORT);
                            SplashActivity.this.naviToMain();
                        } else {
                            SplashActivity.this.naviToLogin();
                        }

                        SplashActivity.this.finish();

                        Log.flush(false); // 把整个登陆流程的 log flush 一下.
                    }
                })
                .subscribe();
    }

    private void naviToMain() {
        MainActivity.startActivity(this);
    }

    private void naviToLogin() {
        LoginActivity.startActivity(this);
    }
}
