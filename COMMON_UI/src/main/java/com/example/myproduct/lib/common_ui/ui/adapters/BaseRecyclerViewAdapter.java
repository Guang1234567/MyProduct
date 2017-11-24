package com.example.myproduct.lib.common_ui.ui.adapters;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.OutsideLifecycleException;
import com.trello.rxlifecycle2.RxLifecycle;

import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

/**
 * 封装常用功能的 RecyclerView.Adapter 的子类.
 * <p>
 * 建议不要直接继承 RecyclerView.Adapter 而是继承它.
 *
 * @author lihanguang
 * @date 2017/5/13 17:52
 */

public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder> {

    public abstract static class BaseViewHolder<T, A extends BaseRecyclerViewAdapter> extends RecyclerView.ViewHolder {

        protected BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void onViewRecycled(A outerAdapter);

        protected abstract void onBindViewHolder(T item, int position, A outerAdapter);

        protected abstract void onBindViewHolder(T item, int position, List<Object> payloads, A outerAdapter);
    }


    public abstract static class BaseDataBindingViewHolder<T, A extends BaseRecyclerViewAdapter, B extends ViewDataBinding>
            extends BaseViewHolder<T, A> {
        protected final B mBinding;

        protected BaseDataBindingViewHolder(B binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @Nonnull
        protected final B getBinding() {
            return mBinding;
        }
    }


    public enum ViewHolderEvent {
        BIND,
        RECYCLED;

        public final static Function<ViewHolderEvent, ViewHolderEvent> VIEWHOLDER_LIFECYCLE =
                new Function<ViewHolderEvent, ViewHolderEvent>() {
                    @Override
                    public ViewHolderEvent apply(ViewHolderEvent lastEvent) throws Exception {
                        switch (lastEvent) {
                            case BIND:
                                return RECYCLED;
                            case RECYCLED:
                                throw new OutsideLifecycleException("Cannot bind to ViewHolder lifecycle when outside of it.");
                            default:
                                throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                        }
                    }
                };
    }


    public final static class RxViewHolderLifecycleProviderImpl implements LifecycleProvider<ViewHolderEvent> {

        private final BehaviorSubject<ViewHolderEvent> mLifecycleSubject;

        public RxViewHolderLifecycleProviderImpl() {
            mLifecycleSubject = BehaviorSubject.create();
        }

        public void onNext(@Nonnull ViewHolderEvent event) {
            mLifecycleSubject.onNext(event);
        }

        @Nonnull
        @Override
        public Observable<ViewHolderEvent> lifecycle() {
            return mLifecycleSubject.hide();
        }

        @Nonnull
        @Override
        public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ViewHolderEvent event) {
            return RxLifecycle.bindUntilEvent(mLifecycleSubject, event);
        }

        @Nonnull
        @Override
        public <T> LifecycleTransformer<T> bindToLifecycle() {
            return RxLifecycle.bind(mLifecycleSubject, ViewHolderEvent.VIEWHOLDER_LIFECYCLE);
        }
    }


    public abstract static class RxBaseViewHolder<T, A extends BaseRecyclerViewAdapter>
            extends BaseViewHolder<T, A>
            implements LifecycleProvider<ViewHolderEvent> {

        private final RxViewHolderLifecycleProviderImpl mLifecycleProvider;

        protected RxBaseViewHolder(View itemView) {
            super(itemView);
            mLifecycleProvider = new RxViewHolderLifecycleProviderImpl();
        }

        @Override
        protected void onViewRecycled(A outerAdapter) {
            mLifecycleProvider.onNext(ViewHolderEvent.RECYCLED);
        }

        @Override
        protected void onBindViewHolder(T item, int position, A outerAdapter) {
            mLifecycleProvider.onNext(ViewHolderEvent.BIND);
        }

        @Override
        protected void onBindViewHolder(T item, int position, List<Object> payloads, A outerAdapter) {
            mLifecycleProvider.onNext(ViewHolderEvent.BIND);
        }

        @Nonnull
        @Override
        public final Observable<ViewHolderEvent> lifecycle() {
            return mLifecycleProvider.lifecycle();
        }

        @Nonnull
        @Override
        public final <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ViewHolderEvent event) {
            return mLifecycleProvider.bindUntilEvent(event);
        }

        @Nonnull
        @Override
        public final <T> LifecycleTransformer<T> bindToLifecycle() {
            return mLifecycleProvider.bindToLifecycle();
        }
    }


    public abstract static class RxBaseDataBindingViewHolder<T, A extends BaseRecyclerViewAdapter, B extends ViewDataBinding>
            extends BaseDataBindingViewHolder<T, A, B>
            implements LifecycleProvider<ViewHolderEvent> {

        private final RxViewHolderLifecycleProviderImpl mLifecycleProvider;

        protected RxBaseDataBindingViewHolder(B binding) {
            super(binding);
            mLifecycleProvider = new RxViewHolderLifecycleProviderImpl();
        }

        @Override
        protected void onViewRecycled(A outerAdapter) {
            mLifecycleProvider.onNext(ViewHolderEvent.RECYCLED);
        }

        @Override
        protected void onBindViewHolder(T item, int position, A outerAdapter) {
            mLifecycleProvider.onNext(ViewHolderEvent.BIND);
        }

        @Override
        protected void onBindViewHolder(T item, int position, List<Object> payloads, A outerAdapter) {
            mLifecycleProvider.onNext(ViewHolderEvent.BIND);
        }

        @Nonnull
        @Override
        public final Observable<ViewHolderEvent> lifecycle() {
            return mLifecycleProvider.lifecycle();
        }

        @Nonnull
        @Override
        public final <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ViewHolderEvent event) {
            return mLifecycleProvider.bindUntilEvent(event);
        }

        @Nonnull
        @Override
        public final <T> LifecycleTransformer<T> bindToLifecycle() {
            return mLifecycleProvider.bindToLifecycle();
        }
    }

    protected abstract Object getItem(int position);

    @Override
    final public void onViewRecycled(BaseViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onViewRecycled(this);
    }

    @Override
    final public void onBindViewHolder(BaseViewHolder holder, int position) {
        Object item = getItem(position);
        holder.onBindViewHolder(item, position, this);
    }

    @Override
    final public void onBindViewHolder(BaseViewHolder holder, int position, List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Object item = getItem(position);
            holder.onBindViewHolder(item, position, payloads, this);
        }
    }
}
