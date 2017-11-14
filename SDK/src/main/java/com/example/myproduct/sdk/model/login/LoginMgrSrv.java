package com.example.myproduct.sdk.model.login;

import android.content.Context;
import android.text.TextUtils;

import com.example.myproduct.lib.common.core.BaseStartStopable;
import com.example.myproduct.lib.common.utils.rx.RxSchedulers;
import com.example.myproduct.lib.common.utils.rx.SafeDisposable;
import com.example.myproduct.sdk.model.core.IAppMgrSrv;
import com.example.myproduct.sdk.proguard.model.login.TLSLoginBusiness;
import com.example.myproduct.sdk.proguard.model.login.TLSStrAccountRegisterBusiness;

import org.reactivestreams.Publisher;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.PublishProcessor;
import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * 登录管理服务 : 注册账号 + 登录账号
 *
 * @author lihanguang
 * @date 2017/3/11 10:51:35
 */

public class LoginMgrSrv extends BaseStartStopable implements IAppMgrSrv {

    private Map<AccountRegisterWay, Object> mRegisterBusinessMap;

    private TLSLoginBusiness mLoginBusiness;

    private LoginMgrSrv() {
    }

    public static LoginMgrSrv create() {
        LoginMgrSrv srv = new LoginMgrSrv();
        srv.mRegisterBusinessMap = new HashMap<>();
        srv.mLoginBusiness = TLSLoginBusiness.create();
        return srv;
    }

    @Override
    public void onStart(Context context) {
    }

    @Override
    public void onStop(Context context) {
    }

    public String getUserSig(String identify) {
        return mLoginBusiness.getUserSig(identify);
    }

    public String getLastUserIdentifier() {
        return mLoginBusiness.getLastUserIdentifier();
    }

    public boolean isUserLogin() {
        return mLoginBusiness.isUserLogin();
    }

    /**
     * 注册账号
     *
     * @param identity 账户名
     * @param password 密码
     */
    public Flowable<AccountRegisterResult<TLSUserInfo>> registerAccount(final String identity,
                                                                        final String password,
                                                                        final AccountRegisterWay regWay) {

        return Flowable.defer(new Callable<Publisher<AccountRegisterResult<TLSUserInfo>>>() {
            @Override
            public Publisher<AccountRegisterResult<TLSUserInfo>> call() throws Exception {
                return Flowable.just("RxStart : LoginMgrSrv # registerAccount")
                        .observeOn(RxSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                if (isStarted()) {
                                    return true;
                                } else {
                                    throw new IllegalStateException("LoginMgrSrv # registerAccount() must called after LoginMgrSrv started");
                                }
                            }
                        })
                        .observeOn(RxSchedulers.io())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                if (TextUtils.isEmpty(identity)) {
                                    throw new IllegalStateException("identity 参数为空!");
                                }

                                if (TextUtils.isEmpty(password)) {
                                    throw new IllegalStateException("password 参数为空!");
                                }

                                return true;
                            }
                        })
                        .flatMap(new Function<String, Publisher<AccountRegisterResult<TLSUserInfo>>>() {
                            @Override
                            public Publisher<AccountRegisterResult<TLSUserInfo>> apply(String s) throws Exception {
                                return _registerAccount(identity, password, regWay);
                            }
                        });
            }
        });
    }

    private Flowable<AccountRegisterResult<TLSUserInfo>> _registerAccount(
            final String identity, final String password, AccountRegisterWay regWay) {
        final AccountRegisterException.Builder expbuilder = AccountRegisterException.builder()
                .registerWay(regWay);

        if (AccountRegisterWay.STR.equals(regWay)) {
            TLSStrAccountRegisterBusiness registerBusiness = (TLSStrAccountRegisterBusiness) mRegisterBusinessMap.get(regWay);
            if (registerBusiness == null) {
                registerBusiness = TLSStrAccountRegisterBusiness.create();
                mRegisterBusinessMap.put(regWay, registerBusiness);
            }
            return registerBusiness.registerAccount(identity, password);
        } else {
            AccountRegisterException accountRegisterException = expbuilder
                    .body(new TLSErrInfo(TLSErrInfo.INPUT_INVALID, "警告", "还不支持注册方式 : " + String.valueOf(regWay)))
                    .build();
            return Flowable.error(accountRegisterException);
        }
    }

    public Flowable<Integer> login(final String identifier,
                                   final String password,
                                   final PublishProcessor<TLSUserInfo> OnPwdLoginSuccess,
                                   final BehaviorProcessor<byte[]> OnPwdLoginReaskImgcodeSuccess,
                                   final BehaviorProcessor<List> OnPwdLoginNeedImgcode,
                                   final BehaviorProcessor<TLSErrInfo> OnPwdLoginFail,
                                   final BehaviorProcessor<TLSErrInfo> OnPwdLoginTimeout) {
        return Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                emitter.setDisposable(new SafeDisposable() {
                    @Override
                    protected void onDispose() {
                    }
                });
                int result = mLoginBusiness.TLSPwdLogin(identifier, password, new TLSPwdLoginListener() {
                    @Override
                    public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {
                        if (OnPwdLoginSuccess != null) {
                            OnPwdLoginSuccess.onNext(tlsUserInfo);
                        }
                    }

                    @Override
                    public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {
                        if (OnPwdLoginReaskImgcodeSuccess != null) {
                            OnPwdLoginReaskImgcodeSuccess.onNext(bytes);
                        }
                    }

                    @Override
                    public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {
                        if (OnPwdLoginNeedImgcode != null) {
                            List args = new LinkedList();
                            args.add(bytes);
                            args.add(tlsErrInfo);
                            OnPwdLoginNeedImgcode.onNext(args);
                        }
                    }

                    @Override
                    public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
                        if (OnPwdLoginFail != null) {
                            OnPwdLoginFail.onNext(tlsErrInfo);
                        }
                    }

                    @Override
                    public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
                        if (OnPwdLoginTimeout != null) {
                            OnPwdLoginTimeout.onNext(tlsErrInfo);
                        }
                    }
                });
                emitter.onNext(Integer.valueOf(result));
            }
        }, BackpressureStrategy.LATEST);
    }
}
