package com.example.myproduct.lib.common.utils.db;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableOperator;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SafeSubscriber;

public class QueryFlowable extends Flowable<Cursor> {
    private Flowable<Cursor> mSource;

    public QueryFlowable(Flowable<Cursor> source) {
        mSource = source;
    }

    @Override
    protected void subscribeActual(Subscriber<? super Cursor> s) {
        mSource.subscribe(new SafeSubscriber<>(s));
    }

    @CheckResult
    @NonNull
    public final <T> Flowable<List<T>> mapToList(@NonNull Function<Cursor, T> mapper) {
        return lift(new QueryToListOperator<>(mapper));
    }

    @CheckResult
    @NonNull
    public final <T> Flowable<T> mapToOne(@NonNull Function<Cursor, T> mapper) {
        return lift(new QueryToOneOperator<>(mapper, false, null));
    }

    @CheckResult
    @NonNull
    public final <T> Flowable<T> mapToOneOrDefault(@NonNull Function<Cursor, T> mapper,
                                                   T defaultValue) {
        return lift(new QueryToOneOperator<>(mapper, true, defaultValue));
    }

    final static class QueryToListOperator<T> implements FlowableOperator<List<T>, Cursor> {
        final Function<Cursor, T> mMapper;

        QueryToListOperator(Function<Cursor, T> mapper) {
            this.mMapper = mapper;
        }

        @Override
        public Subscriber<? super Cursor> apply(final Subscriber<? super List<T>> observer) throws Exception {
            return new SafeSubscriber<>(new DisposableSubscriber<Cursor>() {
                @Override
                public void onNext(Cursor cursor) {
                    try {
                        if (cursor == null || cursor.isClosed() || cursor instanceof NullCursor) {
                            return;
                        }
                        List<T> items = new ArrayList<>(cursor.getCount());
                        try {
                            while (cursor.moveToNext()) {
                                T item = mMapper.apply(cursor);
                                items.add(item);
                            }
                        } finally {
                            cursor.close();
                        }
                        observer.onNext(items);
                    } catch (Throwable e) {
                        Exceptions.throwIfFatal(e);
                        onError(e);
                    }
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }

                @Override
                public void onError(Throwable e) {
                    observer.onError(e);
                }
            });
        }
    }

    final static class QueryToOneOperator<T> implements FlowableOperator<T, Cursor> {
        final Function<Cursor, T> mapper;
        boolean emitDefault;
        T defaultValue;

        QueryToOneOperator(Function<Cursor, T> mapper, boolean emitDefault, T defaultValue) {
            this.mapper = mapper;
            this.emitDefault = emitDefault;
            this.defaultValue = defaultValue;
        }

        @Override
        public Subscriber<? super Cursor> apply(final Subscriber<? super T> observer) {
            return new SafeSubscriber<>(new DisposableSubscriber<Cursor>() {
                @Override
                public void onNext(Cursor cursor) {
                    try {
                        boolean emit = false;
                        T item = null;
                        if (cursor != null && !(cursor instanceof NullCursor)) {
                            try {
                                if (cursor.moveToNext()) {
                                    item = mapper.apply(cursor);
                                    emit = true;
                                    if (cursor.moveToNext()) {
                                        throw new IllegalStateException("Cursor returned more than 1 row!");
                                    }
                                }
                            } finally {
                                cursor.close();
                            }
                        }

                        if (emit) {
                            observer.onNext(item);
                        } else if (emitDefault) {
                            observer.onNext(defaultValue);
                        } else {
                            request(1L); // Account upstream for the lack of downstream emission.
                        }
                    } catch (Throwable e) {
                        Exceptions.throwIfFatal(e);
                        onError(e);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    observer.onError(e);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            });
        }
    }
}