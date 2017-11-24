package com.example.myproduct.app.demo.db;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.myproduct.app.databinding.ListItemTodoBinding;
import com.example.myproduct.app.demo.db.model.pojo.TodoListItem;
import com.example.myproduct.lib.common.utils.rx.RxSchedulers;
import com.example.myproduct.lib.common_ui.ui.adapters.BaseRecyclerViewAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;

/**
 * @author lihanguang
 * @date 2017/5/13 17:32
 */
public class TestDatabaseListAdapter extends BaseRecyclerViewAdapter {

    private List<? extends Object> mItemDatas;

    public TestDatabaseListAdapter() {
        mItemDatas = new LinkedList<>();
        setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        return mItemDatas == null ? 0 : mItemDatas.size();
    }

    @Override
    protected Object getItem(int position) {
        return mItemDatas == null ? null : mItemDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        Object itemData = getItem(position);
        if (itemData instanceof TodoListItem) {
            return ((TodoListItem) itemData).todoList().rowId();
        }
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        Object itemData = getItem(position);
        if (itemData instanceof TodoListItem) {
            return 11;
        } else {
            return 99;
        }
    }

    public static class TodoListItemViewHolder extends RxBaseDataBindingViewHolder<TodoListItem, TestDatabaseListAdapter, ListItemTodoBinding> {

        private TodoListItemViewHolder(ListItemTodoBinding binding) {
            super(binding);
        }

        static TodoListItemViewHolder create(ViewGroup parent) {
            ListItemTodoBinding binding = ListItemTodoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new TodoListItemViewHolder(binding);
        }

        @Override
        protected void onViewRecycled(TestDatabaseListAdapter outerAdapter) {
            super.onViewRecycled(outerAdapter); // 这里会发出 ViewHolderEvent.RECYCLED 事件, 故一定要调用 super.onViewRecycled(...)

            mBinding.title.setText(null);
            mBinding.title.setOnClickListener(null);
        }

        @Override
        protected void onBindViewHolder(TodoListItem item, int position, TestDatabaseListAdapter outerAdapter) {
            super.onBindViewHolder(item, position, outerAdapter);

            mBinding.title.setText(new StringBuilder(item.todoList().name()).append('_').append('(').append(item.itemCount()).append(')'));
        }

        @Override
        protected void onBindViewHolder(TodoListItem item, int position, List<Object> payloads, TestDatabaseListAdapter outerAdapter) {
            super.onBindViewHolder(item, position, payloads, outerAdapter);
        }
    }

    public static class ObjectItemViewHolder extends RxBaseDataBindingViewHolder<Object, TestDatabaseListAdapter, ListItemTodoBinding> {

        private ObjectItemViewHolder(ListItemTodoBinding binding) {
            super(binding);
        }

        static TodoListItemViewHolder create(ViewGroup parent) {
            ListItemTodoBinding binding = ListItemTodoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new TodoListItemViewHolder(binding);
        }

        @Override
        protected void onViewRecycled(TestDatabaseListAdapter outerAdapter) {
            super.onViewRecycled(outerAdapter); // 这里会发出 ViewHolderEvent.RECYCLED 事件, 故一定要调用 super.onViewRecycled(...)

            mBinding.title.setText(null);
            mBinding.title.setOnClickListener(null);
        }

        @Override
        protected void onBindViewHolder(Object item, int position, TestDatabaseListAdapter outerAdapter) {
            super.onBindViewHolder(item, position, outerAdapter);

            mBinding.title.setText(String.valueOf(item));
        }

        @Override
        protected void onBindViewHolder(Object item, int position, List<Object> payloads, TestDatabaseListAdapter outerAdapter) {
            super.onBindViewHolder(item, position, payloads, outerAdapter);
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 11:
                return TodoListItemViewHolder.create(parent);
            case 99:
            default:
                return ObjectItemViewHolder.create(parent);
        }
    }

    public Completable changeAll(List<? extends Object> newData) {
        if (newData == null) {
            newData = new LinkedList<>();
        }

        final List<? extends Object> finalNewData = newData;
        Collections.sort(finalNewData, new InnerComparator());
        return Completable.fromCallable(
                new Callable<List<? extends Object>>() {
                    @Override
                    public List<? extends Object> call() throws Exception {
                        mItemDatas = finalNewData;
                        notifyDataSetChanged();
                        return mItemDatas;
                    }
                })
                .subscribeOn(RxSchedulers.mainThread())
                .observeOn(RxSchedulers.workerThread());
    }

    private static class InnerComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof TodoListItem && o2 instanceof TodoListItem) {
                return Long.compare(
                        Math.max(((TodoListItem) o2).itemCount(), ((TodoListItem) o2).todoItems().size()),
                        Math.max(((TodoListItem) o1).itemCount(), ((TodoListItem) o1).todoItems().size())
                );
            }
            return 0;
        }
    }
}
