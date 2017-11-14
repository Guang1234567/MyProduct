package com.example.myproduct.lib.common.utils.rx.eventbus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Rxjava2 版本的事件总线
 *
 * @author lihanguang
 * @date 2017/5/15 13:47:15
 */

public class RxEventBus {
    public static String TAG = "RxEventBus";

    private static volatile RxEventBus sDefault;

    private final FlowableProcessor<Object> mBus;
    private final Flowable mEventEmitter;
    private final Map<Class<?>, Object> mStickyEventMap;

    // someConfig
    /*private final boolean mThrowSubscriberException;
    private final boolean mLogSubscriberExceptions;
    private final boolean mSendSubscriberExceptionEvent;*/

    private RxEventBus(Builder builder) {
        /*mThrowSubscriberException = builder.mThrowSubscriberException;
        mLogSubscriberExceptions = builder.mLogSubscriberExceptions;
        mSendSubscriberExceptionEvent = builder.mSendSubscriberExceptionEvent;*/

        mBus = PublishProcessor.create().toSerialized();
        mEventEmitter = mBus.share(); // http://blog.csdn.net/xmxkf/article/details/51692493#3-refcount
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    public static RxEventBus getDefault() {
        if (sDefault == null) {
            synchronized (RxEventBus.class) {
                if (sDefault == null) {
                    sDefault = new RxEventBus(new Builder()
                            /*.throwSubscriberException(true)
                            .logSubscriberExceptions(true)
                            .sendSubscriberExceptionEvent(true)*/);
                }
            }
        }
        return sDefault;
    }

    public void post(Object event) {
        mBus.onNext(event);
    }

    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    public <T> Flowable<T> onEvent(final Class<T> eventType) {
        return mEventEmitter.ofType(eventType);
    }

    public <T> Flowable<T> onEventSticky(final Class<T> eventType) {
        synchronized (mStickyEventMap) {
            final Object event = mStickyEventMap.get(eventType);
            Flowable<T> f = mEventEmitter.ofType(eventType);
            if (event != null) {
                f = f.mergeWith(Flowable.just(eventType.cast(event)));
            }
            return f;
        }
    }

    public <T> T getStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }

    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

    /*private void handleSubscriberException(FlowableProcessor fp, Object event, Throwable cause) {
        if (event instanceof RxEventBusSubscriberException) {
            if (mLogSubscriberExceptions) {
                // Don't send another RxSubscriberExceptionEvent to avoid infinite event recursion, just log
                Log.e(TAG, "RxSubscriberExceptionEvent subscriber " + " threw an exception", cause);
                RxSubscriberExceptionEvent exEvent = (RxSubscriberExceptionEvent) event;
                Log.e(TAG, "Initial event " + exEvent.causingEvent + " caused exception in "
                        + exEvent.causingSubscriber, exEvent.throwable);
            }
        } else {
            if (mThrowSubscriberException) {
                fp.onError(new RxEventBusException("Invoking subscriber failed", cause));
            }
            if (mLogSubscriberExceptions) {
                Log.e(TAG, "Could not dispatch event: " + event.getClass() + " to subscribing class "
                        + String.valueOf(fp), cause);
            }
            if (mSendSubscriberExceptionEvent) {
                RxSubscriberExceptionEvent exEvent = new RxSubscriberExceptionEvent(this, cause, event, fp);
                post(exEvent);
            }
        }
    }*/

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        /*Boolean mThrowSubscriberException;
        Boolean mLogSubscriberExceptions;
        Boolean mSendSubscriberExceptionEvent;*/

        /**
         * Default: true
         */
        /*public Builder throwSubscriberException(boolean throwSubscriberException) {
            this.mThrowSubscriberException = Boolean.valueOf(throwSubscriberException);
            return this;
        }*/

        /**
         * Default: true
         */
        /*public Builder logSubscriberExceptions(boolean logSubscriberExceptions) {
            this.mLogSubscriberExceptions = Boolean.valueOf(logSubscriberExceptions);
            return this;
        }*/

        /**
         * Default: true
         */
        /*public Builder sendSubscriberExceptionEvent(boolean sendSubscriberExceptionEvent) {
            this.mSendSubscriberExceptionEvent = Boolean.valueOf(sendSubscriberExceptionEvent);
            return this;
        }*/

        public RxEventBus build() {
            /*String missing = "";
            if (this.mThrowSubscriberException == null) {
                missing += " throwSubscriberException";
            }
            if (this.mLogSubscriberExceptions == null) {
                missing += " logSubscriberExceptions";
            }
            if (this.mSendSubscriberExceptionEvent == null) {
                missing += " sendSubscriberExceptionEvent";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }*/

            return new RxEventBus(this);
        }
    }
}


